package com.mockinterview.analytics.service;

import com.mockinterview.analytics.dto.AnalyticsDashboardDto;
import com.mockinterview.config.AppProperties;
import com.mockinterview.evaluation.entity.TechnicalAnswer;
import com.mockinterview.evaluation.repository.TechnicalAnswerRepository;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.question.entity.Question;
import com.mockinterview.scoring.entity.Score;
import com.mockinterview.scoring.repository.ScoreRepository;
import com.mockinterview.scoring.service.OverallScoreCalculator;
import com.mockinterview.scoring.service.ScoringEngine;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    @Test
    void recalculatesStaleScoresFromEvaluatedAnswersAndSkipsUnscoredInterviews() {
        Interview scoredInterview = interview(1L, LocalDateTime.of(2026, 1, 1, 0, 0));
        Interview unscoredInterview = interview(2L, LocalDateTime.of(2026, 1, 2, 0, 0));
        TechnicalAnswer evaluatedAnswer = answerWithScore(56.0);
        Score scoredInterviewRecord = staleScore();
        Score unscoredInterviewRecord = staleScore();
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        ScoreRepository scoreRepository = mock(ScoreRepository.class);
        SubmissionRepository submissionRepository = mock(SubmissionRepository.class);
        TechnicalAnswerRepository answerRepository = mock(TechnicalAnswerRepository.class);
        ScoringEngine scoringEngine = mock(ScoringEngine.class);
        ExecutionResultRepository executionResultRepository = mock(ExecutionResultRepository.class);
        TestResultRepository testResultRepository = mock(TestResultRepository.class);
        AppProperties properties = new AppProperties();
        properties.getScoring().setWeights(Map.of(
                "coding", 0.40,
                "knowledge", 0.20,
                "communication", 0.20,
                "behavior", 0.20));
        properties.getScoring().setThresholds(Map.of(
                "beginner-max", 39,
                "developing-max", 59,
                "intermediate-max", 74,
                "advanced-max", 89));

        when(interviewRepository.findByUserIdAndStatus(7L, Interview.InterviewStatus.COMPLETED))
                .thenReturn(List.of(unscoredInterview, scoredInterview));
        when(submissionRepository.findByInterviewId(1L)).thenReturn(List.of());
        when(submissionRepository.findByInterviewId(2L)).thenReturn(List.of());
        when(submissionRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of());
        when(answerRepository.findByInterviewId(1L)).thenReturn(List.of(evaluatedAnswer));
        when(answerRepository.findByInterviewId(2L)).thenReturn(List.of());
        when(scoreRepository.findByInterviewId(1L)).thenReturn(Optional.of(scoredInterviewRecord));
        when(scoreRepository.findByInterviewId(2L)).thenReturn(Optional.of(unscoredInterviewRecord));

        AnalyticsService service = new AnalyticsService(
                interviewRepository,
                scoreRepository,
                submissionRepository,
                answerRepository,
                scoringEngine,
                new OverallScoreCalculator(properties),
                executionResultRepository,
                testResultRepository);

        AnalyticsDashboardDto dashboard = service.getDashboard(7L);

        assertEquals(2, dashboard.totalInterviews());
        assertEquals(1, dashboard.scoreHistory().size());
        assertEquals(56.0, dashboard.scoreHistory().get(0).overallScore());
        assertEquals(56.0, dashboard.scoreHistory().get(0).knowledgeScore());
    }

    private Interview interview(long id, LocalDateTime createdAt) {
        Interview interview = mock(Interview.class);
        when(interview.getId()).thenReturn(id);
        when(interview.getCreatedAt()).thenReturn(createdAt);
        when(interview.getDifficulty()).thenReturn(Question.DifficultyLevel.EASY);
        return interview;
    }

    private TechnicalAnswer answerWithScore(double score) {
        TechnicalAnswer answer = mock(TechnicalAnswer.class);
        when(answer.getOverallScore()).thenReturn(score);
        return answer;
    }

    private Score staleScore() {
        Score score = mock(Score.class);
        when(score.getOverallScore()).thenReturn(20.0);
        when(score.getCodingScore()).thenReturn(0.0);
        when(score.getKnowledgeScore()).thenReturn(50.0);
        when(score.getCommunicationScore()).thenReturn(null);
        when(score.getBehaviorScore()).thenReturn(null);
        return score;
    }
}
