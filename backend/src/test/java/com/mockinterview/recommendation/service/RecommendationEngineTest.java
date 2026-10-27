package com.mockinterview.recommendation.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationEngineTest {

    @Test
    void persistsFallbackRecommendationWhenCodingResultsHaveNoWeaknesses() {
        RecommendationRepository recommendationRepository = mock(RecommendationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        User user = mock(User.class);
        Interview interview = mock(Interview.class);
        when(userRepository.getReferenceById(7L)).thenReturn(user);
        when(interviewRepository.getReferenceById(12L)).thenReturn(interview);
        when(recommendationRepository.findByInterviewId(12L)).thenReturn(List.of());
        when(recommendationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationEngine engine = new RecommendationEngine(
                recommendationRepository, userRepository, interviewRepository);

        List<Recommendation> saved = engine.generateRecommendations(7L, 12L, strongCodingBreakdown(), 50);

        assertEquals(1, saved.size());
        assertEquals("Build on your progress", saved.get(0).getTitle());
        assertEquals(user, saved.get(0).getUser());
        assertEquals(interview, saved.get(0).getInterview());
        verify(recommendationRepository).saveAll(anyList());
    }

    @Test
    void doesNotPersistDuplicateRecommendationsForRepeatedInterviewSubmissions() {
        RecommendationRepository recommendationRepository = mock(RecommendationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        Recommendation existing = new Recommendation();
        existing.setCategory("NEXT_STEPS");
        existing.setTitle("Build on your progress");
        when(userRepository.getReferenceById(7L)).thenReturn(mock(User.class));
        when(recommendationRepository.findByInterviewId(12L))
                .thenReturn(List.of(), List.of(existing));
        when(recommendationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationEngine engine = new RecommendationEngine(
                recommendationRepository, userRepository, interviewRepository);

        List<Recommendation> firstSave = engine.generateRecommendations(7L, 12L, strongCodingBreakdown(), 50);
        List<Recommendation> secondSave = engine.generateRecommendations(7L, 12L, strongCodingBreakdown(), 50);

        assertEquals(1, firstSave.size());
        assertEquals(0, secondSave.size());
        verify(recommendationRepository, org.mockito.Mockito.times(2)).saveAll(anyList());
    }

    private ScoreBreakdown strongCodingBreakdown() {
        return new ScoreBreakdown(100, 100, 100, 100, 100, 100, 100, new ArrayList<>(), "O(n)");
    }
}
