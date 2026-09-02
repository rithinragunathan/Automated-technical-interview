package com.mockinterview.report.service;

import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.dto.RecommendationDto;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.service.RecommendationEngine;
import com.mockinterview.report.dto.InterviewReportDto;
import com.mockinterview.report.entity.FeedbackReport;
import com.mockinterview.report.repository.FeedbackReportRepository;
import com.mockinterview.scoring.dto.OverallScore;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import com.mockinterview.scoring.entity.Score;
import com.mockinterview.scoring.repository.ScoreRepository;
import com.mockinterview.scoring.service.OverallScoreCalculator;
import com.mockinterview.scoring.service.ScoringEngine;
import com.mockinterview.submission.entity.ExecutionResultEntity;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReportService {

    private final InterviewRepository interviewRepository;
    private final SubmissionRepository submissionRepository;
    private final FeedbackReportRepository feedbackReportRepository;
    private final ScoreRepository scoreRepository;
    private final ScoringEngine scoringEngine;
    private final OverallScoreCalculator overallScoreCalculator;
    private final LLMProvider llmProvider;
    private final RecommendationEngine recommendationEngine;
    private final TestResultRepository testResultRepository;
    private final ExecutionResultRepository executionResultRepository;

    public ReportService(InterviewRepository interviewRepository,
                         SubmissionRepository submissionRepository,
                         FeedbackReportRepository feedbackReportRepository,
                         ScoreRepository scoreRepository,
                         ScoringEngine scoringEngine,
                         OverallScoreCalculator overallScoreCalculator,
                         LLMProvider llmProvider,
                         RecommendationEngine recommendationEngine,
                         TestResultRepository testResultRepository,
                         ExecutionResultRepository executionResultRepository) {
        this.interviewRepository = interviewRepository;
        this.submissionRepository = submissionRepository;
        this.feedbackReportRepository = feedbackReportRepository;
        this.scoreRepository = scoreRepository;
        this.scoringEngine = scoringEngine;
        this.overallScoreCalculator = overallScoreCalculator;
        this.llmProvider = llmProvider;
        this.recommendationEngine = recommendationEngine;
        this.testResultRepository = testResultRepository;
        this.executionResultRepository = executionResultRepository;
    }

    public InterviewReportDto generateReport(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        List<Submission> submissions = submissionRepository.findByInterviewId(interviewId);
        
        // Compute coding scores
        double totalCodingScore = 0;
        ScoreBreakdown lastBreakdown = null;
        int totalPassed = 0;
        int totalTests = 0;
        
        for (Submission sub : submissions) {
            ExecutionResultEntity ere = executionResultRepository.findBySubmissionId(sub.getId()).orElse(null);
            ScoreBreakdown sb = scoringEngine.scoreSubmission(sub, testResultRepository.findBySubmissionId(sub.getId()), ere, interview.getDifficulty().name());
            totalCodingScore += sb.totalCodingScore();
            lastBreakdown = sb;
            totalPassed += sub.getPassedTests();
            totalTests += sub.getTotalTests();
        }
        
        double avgCodingScore = submissions.isEmpty() ? 0 : totalCodingScore / submissions.size();
        
        // Assume knowledgeScore for now (no technical answers fetched yet)
        double avgKnowledgeScore = 50.0;
        
        OverallScore overall = overallScoreCalculator.calculate(avgCodingScore, avgKnowledgeScore, null, null);
        
        Score score = new Score();
        score.setInterview(interview);
        score.setOverallScore(overall.overallScore());
        score.setCodingScore(overall.codingScore());
        score.setKnowledgeScore(overall.knowledgeScore());
        score.setProficiencyLevel(overall.proficiencyLevel());
        scoreRepository.save(score);

        InterviewFeedback feedback = llmProvider.generateInterviewFeedback(new InterviewSummaryForAI(
                interview.getInterviewType().name(),
                interview.getDifficulty().name(),
                interview.getTopic(),
                avgCodingScore,
                avgKnowledgeScore,
                new ArrayList<>(),
                new ArrayList<>()
        ));

        FeedbackReport report = new FeedbackReport();
        report.setInterview(interview);
        report.setUser(interview.getUser());
        report.setOverallSummary(feedback.narrative());
        report.setStrengthsList(feedback.strengths());
        report.setWeaknessesList(feedback.weaknesses());
        report.setAiNarrative(feedback.narrative());
        feedbackReportRepository.save(report);

        List<RecommendationDto> recDtos = new ArrayList<>();
        if (lastBreakdown != null) {
            List<Recommendation> recs = recommendationEngine.generateRecommendations(interview.getUser().getId(), interviewId, lastBreakdown, avgKnowledgeScore);
            recDtos = recs.stream().map(r -> new RecommendationDto(
                    r.getId(), r.getCategory(), r.getTitle(), r.getDescription(), r.getPriority(), r.getResourceLinksList(), r.isCompleted()
            )).collect(Collectors.toList());
        }

        return new InterviewReportDto(
                interview.getId(),
                interview.getUser().getId(),
                interview.getInterviewType().name(),
                interview.getDifficulty().name(),
                interview.getTopic(),
                interview.getStartTime(),
                interview.getEndTime(),
                interview.getDurationSeconds(),
                overall,
                lastBreakdown,
                new ArrayList<>(),
                feedback.strengths(),
                feedback.weaknesses(),
                recDtos,
                feedback.narrative(),
                totalPassed,
                totalTests
        );
    }
}
