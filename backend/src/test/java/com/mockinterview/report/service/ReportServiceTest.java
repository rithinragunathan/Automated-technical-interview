package com.mockinterview.report.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.evaluation.repository.TechnicalAnswerRepository;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.recommendation.service.RecommendationEngine;
import com.mockinterview.report.dto.InterviewReportDto;
import com.mockinterview.report.repository.FeedbackReportRepository;
import com.mockinterview.scoring.dto.OverallScore;
import com.mockinterview.scoring.repository.ScoreRepository;
import com.mockinterview.scoring.service.OverallScoreCalculator;
import com.mockinterview.scoring.service.ScoringEngine;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import com.mockinterview.submission.entity.Submission;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    @Test
    void reportUsesOnlyTheLatestSubmittedSolutionAndIgnoresRunAttempts() {
        CodingProblem problem = new CodingProblem();
        problem.setId(3L);

        Submission run = new Submission();
        run.setProblem(problem);
        run.setExecutionType(Submission.ExecutionType.RUN);
        run.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));

        Submission earlierSubmit = new Submission();
        earlierSubmit.setProblem(problem);
        earlierSubmit.setExecutionType(Submission.ExecutionType.SUBMIT);
        earlierSubmit.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 1));

        Submission acceptedSubmit = new Submission();
        acceptedSubmit.setProblem(problem);
        acceptedSubmit.setExecutionType(Submission.ExecutionType.SUBMIT);
        acceptedSubmit.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 2));

        List<Submission> finalSubmissions = ReportService.latestSubmittedSubmissionsByProblem(
                List.of(run, earlierSubmit, acceptedSubmit));

        assertEquals(1, finalSubmissions.size());
        assertSame(acceptedSubmit, finalSubmissions.get(0));
    }

    @Test
    void createsAnalysisAndRecommendationsWhenAiFeedbackIsUnavailable() {
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        SubmissionRepository submissionRepository = mock(SubmissionRepository.class);
        FeedbackReportRepository feedbackReportRepository = mock(FeedbackReportRepository.class);
        ScoreRepository scoreRepository = mock(ScoreRepository.class);
        ScoringEngine scoringEngine = mock(ScoringEngine.class);
        OverallScoreCalculator overallScoreCalculator = mock(OverallScoreCalculator.class);
        LLMProvider llmProvider = mock(LLMProvider.class);
        RecommendationRepository recommendationRepository = mock(RecommendationRepository.class);
        TestResultRepository testResultRepository = mock(TestResultRepository.class);
        ExecutionResultRepository executionResultRepository = mock(ExecutionResultRepository.class);
        TechnicalAnswerRepository technicalAnswerRepository = mock(TechnicalAnswerRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        Interview interview = new Interview();
        interview.setId(12L);
        interview.setUser(user);
        interview.setInterviewType(Interview.InterviewType.CODING);
        interview.setDifficulty(com.mockinterview.question.entity.Question.DifficultyLevel.EASY);
        interview.setTopic("DATA_STRUCTURES");
        when(interviewRepository.findByIdForReport(12L)).thenReturn(Optional.of(interview));
        when(interviewRepository.getReferenceById(12L)).thenReturn(interview);
        when(submissionRepository.findByInterviewId(12L)).thenReturn(List.of());
        when(technicalAnswerRepository.findByInterviewId(12L)).thenReturn(List.of());
        when(scoreRepository.findByInterviewId(12L)).thenReturn(Optional.empty());

        OverallScore overallScore = new OverallScore(0, 0, null, null, 0, "Beginner", Map.of(), "No scored evidence");
        when(overallScoreCalculator.calculate(null, null, null, null)).thenReturn(overallScore);
        when(scoreRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(feedbackReportRepository.findByInterviewId(12L)).thenReturn(Optional.empty());
        when(feedbackReportRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(llmProvider.generateInterviewFeedback(any())).thenThrow(new IllegalStateException("AI provider unavailable"));
        List<Recommendation> savedRecommendations = new ArrayList<>();
        when(recommendationRepository.findByInterviewId(12L))
                .thenAnswer(invocation -> List.copyOf(savedRecommendations));
        when(recommendationRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Recommendation> recommendations = invocation.getArgument(0);
            savedRecommendations.addAll(recommendations);
            return recommendations;
        });
        when(userRepository.getReferenceById(7L)).thenReturn(user);

        RecommendationEngine recommendationEngine = new RecommendationEngine(
                recommendationRepository, userRepository, interviewRepository);
        ReportService reportService = new ReportService(
                interviewRepository,
                submissionRepository,
                feedbackReportRepository,
                scoreRepository,
                scoringEngine,
                overallScoreCalculator,
                llmProvider,
                recommendationEngine,
                recommendationRepository,
                testResultRepository,
                executionResultRepository,
                technicalAnswerRepository);

        InterviewReportDto report = reportService.generateReport(12L, 7L);

        assertNotNull(report.aiNarrative());
        assertFalse(report.aiNarrative().isBlank());
        assertFalse(report.weaknesses().isEmpty());
        assertEquals(1, report.recommendations().size());
        assertEquals("Complete a practice exercise", report.recommendations().get(0).title());
    }
}
