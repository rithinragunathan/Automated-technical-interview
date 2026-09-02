package com.mockinterview.recommendation.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class RecommendationEngine {

    private final RecommendationRepository recommendationRepository;
    private final LLMProvider llmProvider;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public RecommendationEngine(RecommendationRepository recommendationRepository,
                                LLMProvider llmProvider,
                                UserRepository userRepository,
                                InterviewRepository interviewRepository) {
        this.recommendationRepository = recommendationRepository;
        this.llmProvider = llmProvider;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    public List<Recommendation> generateRecommendations(Long userId, Long interviewId, ScoreBreakdown codingBreakdown, double knowledgeScore) {
        List<Recommendation> recs = new ArrayList<>();
        User user = userRepository.getReferenceById(userId);

        if (codingBreakdown.spaceEfficiencyScore() < 60) {
            recs.add(createRec(user, interviewId, "ALGORITHMS", "Space Optimization", "Practice memory optimization and in-place algorithms.", 2));
        }

        if (codingBreakdown.timeEfficiencyScore() < 60 && codingBreakdown.correctnessScore() > 70) {
            recs.add(createRec(user, interviewId, "ALGORITHMS", "Time Complexity", "Focus on algorithm selection and time-complexity analysis.", 1));
        }

        if (knowledgeScore > 70 && codingBreakdown.totalCodingScore() < 50) {
            recs.add(createRec(user, interviewId, "PRACTICE", "Coding Practice", "Increase hands-on implementation practice to match theoretical knowledge.", 1));
        }

        if (codingBreakdown.correctnessScore() < 50) {
            recs.add(createRec(user, interviewId, "FUNDAMENTALS", "Core DSA", "Review fundamental data structures and algorithms.", 1));
        }

        if (codingBreakdown.codeQualityScore() < 60) {
            recs.add(createRec(user, interviewId, "CLEAN_CODE", "Code Quality", "Study clean code principles and naming conventions.", 3));
        }

        // Call AI for additional recommendations
        try {
            List<String> aiRecs = llmProvider.generateRecommendations(codingBreakdown);
            for (String rec : aiRecs) {
                recs.add(createRec(user, interviewId, "AI_INSIGHT", "AI Suggestion", rec, 4));
            }
        } catch (Exception e) {
            log.warn("Failed to get AI recommendations", e);
        }

        return recommendationRepository.saveAll(recs);
    }

    private Recommendation createRec(User u, Long iId, String category, String title, String desc, int priority) {
        Recommendation r = new Recommendation();
        r.setUser(u);
        if (iId != null) {
            r.setInterview(interviewRepository.getReferenceById(iId));
        }
        r.setCategory(category);
        r.setTitle(title);
        r.setDescription(desc);
        r.setPriority(priority);
        return r;
    }
}
