package com.mockinterview.report.service;

import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.entity.TechnicalAnswer;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.evaluation.repository.TechnicalAnswerRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.dto.RecommendationDto;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.repository.RecommendationRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
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
    private final RecommendationRepository recommendationRepository;
    private final TestResultRepository testResultRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final TechnicalAnswerRepository technicalAnswerRepository;

    public ReportService(InterviewRepository interviewRepository,
                         SubmissionRepository submissionRepository,
                         FeedbackReportRepository feedbackReportRepository,
                         ScoreRepository scoreRepository,
                         ScoringEngine scoringEngine,
                         OverallScoreCalculator overallScoreCalculator,
                         LLMProvider llmProvider,
                         RecommendationEngine recommendationEngine,
                         RecommendationRepository recommendationRepository,
                         TestResultRepository testResultRepository,
                         ExecutionResultRepository executionResultRepository,
                         TechnicalAnswerRepository technicalAnswerRepository) {
        this.interviewRepository = interviewRepository;
        this.submissionRepository = submissionRepository;
        this.feedbackReportRepository = feedbackReportRepository;
        this.scoreRepository = scoreRepository;
        this.scoringEngine = scoringEngine;
        this.overallScoreCalculator = overallScoreCalculator;
        this.llmProvider = llmProvider;
        this.recommendationEngine = recommendationEngine;
        this.recommendationRepository = recommendationRepository;
        this.testResultRepository = testResultRepository;
        this.executionResultRepository = executionResultRepository;
        this.technicalAnswerRepository = technicalAnswerRepository;
    }

    public InterviewReportDto generateReport(Long interviewId, Long userId) {
        Interview interview = interviewRepository.findByIdForReport(interviewId)
                .filter(candidate -> candidate.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        List<Submission> submissions = latestSubmittedSubmissionsByProblem(
                submissionRepository.findByInterviewId(interviewId));
        
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
        
        Double avgCodingScore = submissions.isEmpty() ? null : totalCodingScore / submissions.size();
        
        List<TechnicalAnswer> answers = technicalAnswerRepository.findByInterviewId(interviewId);
        OptionalDouble knowledgeAverage = answers.stream()
                .map(TechnicalAnswer::getOverallScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average();
        Double avgKnowledgeScore = knowledgeAverage.isPresent() ? knowledgeAverage.getAsDouble() : null;

        Score score = scoreRepository.findByInterviewId(interviewId).orElseGet(Score::new);
        score.setInterview(interview);
        score.setCodingScore(avgCodingScore != null ? avgCodingScore : 0);
        score.setKnowledgeScore(avgKnowledgeScore != null ? avgKnowledgeScore : 0);
        OverallScore overall = overallScoreCalculator.calculate(
                avgCodingScore,
                avgKnowledgeScore,
                score.getCommunicationScore(),
                score.getBehaviorScore());
        score.setOverallScore(overall.overallScore());
        score.setProficiencyLevel(overall.proficiencyLevel());
        score.setCalculatedAt(java.time.LocalDateTime.now());
        score = scoreRepository.save(score);

        FeedbackReport report = feedbackReportRepository.findByInterviewId(interviewId).orElse(null);
        InterviewFeedback feedback;
        if (report == null) {
            try {
                feedback = llmProvider.generateInterviewFeedback(new InterviewSummaryForAI(
                        interview.getInterviewType().name(),
                        interview.getDifficulty().name(),
                        interview.getTopic(),
                        score.getCodingScore(),
                        score.getKnowledgeScore(),
                        answers.stream()
                                .map(answer -> answer.getInterviewQuestion().getQuestion() != null
                                        ? answer.getInterviewQuestion().getQuestion().getTopic()
                                        : interview.getTopic())
                                .filter(topic -> topic != null && !topic.isBlank())
                                .distinct()
                                .toList(),
                        answers.stream()
                                .map(this::formatAnswerForFeedback)
                                .toList()
                ));
            } catch (IllegalStateException ex) {
                log.warn("Gemini could not generate feedback for interview {}; using recorded results", interviewId, ex);
                feedback = createFallbackFeedback(interview, score, submissions, answers, totalPassed, totalTests);
            }

            report = new FeedbackReport();
            report.setInterview(interview);
            report.setUser(interview.getUser());
            report.setOverallSummary(feedback.narrative());
            report.setStrengthsList(feedback.strengths());
            report.setWeaknessesList(feedback.weaknesses());
            report.setAiNarrative(feedback.narrative());
            report = feedbackReportRepository.save(report);
        } else {
            feedback = new InterviewFeedback(
                    report.getAiNarrative(),
                    report.getStrengthsList(),
                    report.getWeaknessesList());
        }

        recommendationEngine.generateRecommendations(
                interview.getUser().getId(),
                interviewId,
                lastBreakdown,
                avgKnowledgeScore);
        List<RecommendationDto> recDtos = new ArrayList<>();
        List<Recommendation> recommendations = recommendationRepository.findByInterviewId(interviewId);
        if (!recommendations.isEmpty()) {
            List<Recommendation> recs = recommendations;
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

    static List<Submission> latestSubmittedSubmissionsByProblem(List<Submission> submissions) {
        return submissions.stream()
                .filter(submission -> submission.getExecutionType() == Submission.ExecutionType.SUBMIT)
                .collect(Collectors.toMap(
                        submission -> submission.getProblem().getId(),
                        submission -> submission,
                        (first, second) -> second.getCreatedAt().isAfter(first.getCreatedAt()) ? second : first
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(Submission::getCreatedAt))
                .toList();
    }

    private String formatAnswerForFeedback(TechnicalAnswer answer) {
        String question = answer.getInterviewQuestion().isAiGenerated()
                ? answer.getInterviewQuestion().getAiQuestionText()
                : answer.getInterviewQuestion().getQuestion() != null
                        ? answer.getInterviewQuestion().getQuestion().getContent()
                        : answer.getInterviewQuestion().getCodingProblem() != null
                                ? answer.getInterviewQuestion().getCodingProblem().getTitle()
                                : "Interview question";
        return "Question: %s | Candidate answer: %s | Evaluation score: %.1f"
                .formatted(question, answer.getAnswerText(), answer.getOverallScore() != null ? answer.getOverallScore() : 0.0);
    }

    private InterviewFeedback createFallbackFeedback(Interview interview,
                                                     Score score,
                                                     List<Submission> submissions,
                                                     List<TechnicalAnswer> answers,
                                                     int totalPassed,
                                                     int totalTests) {
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        if (!submissions.isEmpty() && totalTests > 0) {
            if (totalPassed == totalTests) {
                strengths.add("Your submitted code passed all %d recorded test cases.".formatted(totalTests));
            } else if (totalPassed > 0) {
                strengths.add("Your code passed %d of %d recorded test cases.".formatted(totalPassed, totalTests));
            }
            if (totalPassed < totalTests) {
                weaknesses.add("Review the failing test cases and check boundary conditions before submitting again.");
            }
        }

        if (!answers.isEmpty()) {
            if (score.getKnowledgeScore() >= 70) {
                strengths.add("Your technical answers showed a solid understanding of the interview topics.");
            } else {
                weaknesses.add("Practice explaining technical concepts with a concrete example and a clear sequence of steps.");
            }
        }

        if (submissions.isEmpty() && answers.isEmpty()) {
            weaknesses.add("No code submission or evaluated technical answer was recorded, so this report is a starting point rather than a full assessment.");
        }

        if (strengths.isEmpty() && !submissions.isEmpty()) {
            strengths.add("You completed a coding interview and now have a baseline to build on.");
        }
        if (weaknesses.isEmpty()) {
            weaknesses.add("Try a more challenging problem to explore optimization and additional edge cases.");
        }

        String narrative = "This report summarizes your %s %s interview on %s using the results recorded during the session."
                .formatted(interview.getDifficulty().name().toLowerCase(),
                        interview.getInterviewType().name().replace('_', ' ').toLowerCase(),
                        interview.getTopic() == null ? "your selected topic" : interview.getTopic().replace('_', ' '));
        return new InterviewFeedback(narrative, strengths, weaknesses);
    }
}
