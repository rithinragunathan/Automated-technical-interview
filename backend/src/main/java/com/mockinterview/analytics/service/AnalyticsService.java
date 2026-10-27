package com.mockinterview.analytics.service;

import com.mockinterview.analytics.dto.AnalyticsDashboardDto;
import com.mockinterview.analytics.dto.ScoreHistoryPoint;
import com.mockinterview.evaluation.entity.TechnicalAnswer;
import com.mockinterview.evaluation.repository.TechnicalAnswerRepository;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.scoring.dto.OverallScore;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import com.mockinterview.scoring.entity.Score;
import com.mockinterview.scoring.repository.ScoreRepository;
import com.mockinterview.scoring.service.OverallScoreCalculator;
import com.mockinterview.scoring.service.ScoringEngine;
import com.mockinterview.submission.entity.ExecutionResultEntity;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.entity.TestResult;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final InterviewRepository interviewRepository;
    private final ScoreRepository scoreRepository;
    private final SubmissionRepository submissionRepository;
    private final TechnicalAnswerRepository technicalAnswerRepository;
    private final ScoringEngine scoringEngine;
    private final OverallScoreCalculator overallScoreCalculator;
    private final ExecutionResultRepository executionResultRepository;
    private final TestResultRepository testResultRepository;

    public AnalyticsService(InterviewRepository interviewRepository,
                            ScoreRepository scoreRepository,
                            SubmissionRepository submissionRepository,
                            TechnicalAnswerRepository technicalAnswerRepository,
                            ScoringEngine scoringEngine,
                            OverallScoreCalculator overallScoreCalculator,
                            ExecutionResultRepository executionResultRepository,
                            TestResultRepository testResultRepository) {
        this.interviewRepository = interviewRepository;
        this.scoreRepository = scoreRepository;
        this.submissionRepository = submissionRepository;
        this.technicalAnswerRepository = technicalAnswerRepository;
        this.scoringEngine = scoringEngine;
        this.overallScoreCalculator = overallScoreCalculator;
        this.executionResultRepository = executionResultRepository;
        this.testResultRepository = testResultRepository;
    }

    public AnalyticsDashboardDto getDashboard(Long userId) {
        List<Interview> interviews = new ArrayList<>(
                interviewRepository.findByUserIdAndStatus(userId, Interview.InterviewStatus.COMPLETED));
        interviews.sort(Comparator.comparing(Interview::getCreatedAt));
        
        List<ScoreHistoryPoint> history = new ArrayList<>();
        for (Interview i : interviews) {
            List<Submission> interviewSubmissions = submissionRepository.findByInterviewId(i.getId());
            Double codingScore = interviewSubmissions.isEmpty()
                    ? null
                    : interviewSubmissions.stream()
                            .mapToDouble(submission -> {
                                ExecutionResultEntity executionResult = executionResultRepository
                                        .findBySubmissionId(submission.getId())
                                        .orElse(null);
                                List<TestResult> testResults = testResultRepository.findBySubmissionId(submission.getId());
                                ScoreBreakdown breakdown = scoringEngine.scoreSubmission(
                                        submission, testResults, executionResult, i.getDifficulty().name());
                                return breakdown.totalCodingScore();
                            })
                            .average()
                            .orElse(0);

            OptionalDouble knowledgeAverage = technicalAnswerRepository.findByInterviewId(i.getId()).stream()
                    .map(TechnicalAnswer::getOverallScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average();
            Double knowledgeScore = knowledgeAverage.isPresent() ? knowledgeAverage.getAsDouble() : null;

            Score score = scoreRepository.findByInterviewId(i.getId()).orElse(null);
            Double communicationScore = score != null ? score.getCommunicationScore() : null;
            Double behaviorScore = score != null ? score.getBehaviorScore() : null;
            if (codingScore == null && knowledgeScore == null
                    && communicationScore == null && behaviorScore == null) {
                continue;
            }

            OverallScore overall = overallScoreCalculator.calculate(
                    codingScore, knowledgeScore, communicationScore, behaviorScore);
            history.add(new ScoreHistoryPoint(
                    i.getId(),
                    i.getCreatedAt(),
                    overall.overallScore(),
                    codingScore != null ? codingScore : 0,
                    knowledgeScore != null ? knowledgeScore : 0));
        }

        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        Map<String, Double> topicScoreTotals = new HashMap<>();
        Map<String, Integer> topicAttemptCounts = new HashMap<>();
        Map<String, Double> difficultyScoreTotals = new HashMap<>();
        Map<String, Integer> difficultyAttemptCounts = new HashMap<>();
        
        int totalPassed = 0;
        int totalTests = 0;
        long totalExecutionTime = 0;
        int execCount = 0;
        
        for (Submission s : submissions) {
            String topic = s.getProblem().getTopic();
            String diff = s.getProblem().getDifficulty().name();
            
            totalPassed += s.getPassedTests();
            totalTests += s.getTotalTests();
            if (s.getExecutionTimeMs() != null) {
                totalExecutionTime += s.getExecutionTimeMs();
                execCount++;
            }
            
            double score = (s.getTotalTests() > 0) ? (s.getPassedTests() * 100.0 / s.getTotalTests()) : 0.0;
            
            String topicName = topic != null ? topic : "Unknown";
            topicScoreTotals.merge(topicName, score, Double::sum);
            topicAttemptCounts.merge(topicName, 1, Integer::sum);
            difficultyScoreTotals.merge(diff, score, Double::sum);
            difficultyAttemptCounts.merge(diff, 1, Integer::sum);
        }

        Map<String, Double> topicPerformance = new HashMap<>();
        topicScoreTotals.forEach((topic, total) ->
                topicPerformance.put(topic, total / topicAttemptCounts.get(topic)));
        Map<String, Double> diffPerformance = new HashMap<>();
        difficultyScoreTotals.forEach((difficulty, total) ->
                diffPerformance.put(difficulty, total / difficultyAttemptCounts.get(difficulty)));
        
        double avgTime = execCount > 0 ? (double) totalExecutionTime / execCount : 0.0;
        double successRate = totalTests > 0 ? (totalPassed * 100.0) / totalTests : 0.0;

        List<String> weakest = topicPerformance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> strongest = topicPerformance.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new AnalyticsDashboardDto(
                history, topicPerformance, diffPerformance, avgTime, successRate,
                weakest, strongest, interviews.size(), submissions.size()
        );
    }
}
