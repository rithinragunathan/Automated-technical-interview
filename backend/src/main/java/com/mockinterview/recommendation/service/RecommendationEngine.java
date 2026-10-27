package com.mockinterview.recommendation.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RecommendationEngine {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public RecommendationEngine(RecommendationRepository recommendationRepository,
                                UserRepository userRepository,
                                InterviewRepository interviewRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    public List<Recommendation> generateRecommendations(Long userId, Long interviewId, ScoreBreakdown codingBreakdown, double knowledgeScore) {
        List<Recommendation> recs = new ArrayList<>();
        User user = userRepository.getReferenceById(userId);
        Set<String> existingRecommendations = interviewId == null
                ? new HashSet<>()
                : recommendationRepository.findByInterviewId(interviewId).stream()
                        .map(recommendation -> recommendation.getCategory() + ":" + recommendation.getTitle())
                        .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        if (codingBreakdown.spaceEfficiencyScore() < 60) {
            addIfNew(recs, existingRecommendations, user, interviewId, "ALGORITHMS", "Space Optimization", "Practice memory optimization and in-place algorithms.", 2);
        }

        if (codingBreakdown.timeEfficiencyScore() < 60 && codingBreakdown.correctnessScore() > 70) {
            addIfNew(recs, existingRecommendations, user, interviewId, "ALGORITHMS", "Time Complexity", "Focus on algorithm selection and time-complexity analysis.", 1);
        }

        if (knowledgeScore > 70 && codingBreakdown.totalCodingScore() < 50) {
            addIfNew(recs, existingRecommendations, user, interviewId, "PRACTICE", "Coding Practice", "Increase hands-on implementation practice to match theoretical knowledge.", 1);
        }

        if (codingBreakdown.correctnessScore() < 50) {
            addIfNew(recs, existingRecommendations, user, interviewId, "FUNDAMENTALS", "Core DSA", "Review fundamental data structures and algorithms.", 1);
        }

        if (codingBreakdown.codeQualityScore() < 60) {
            addIfNew(recs, existingRecommendations, user, interviewId, "CLEAN_CODE", "Code Quality", "Study clean code principles and naming conventions.", 3);
        }

        if (recs.isEmpty() && (interviewId == null || existingRecommendations.isEmpty())) {
            addIfNew(recs, existingRecommendations, user, interviewId,
                    "NEXT_STEPS",
                    "Build on your progress",
                    "Your coding results show a solid foundation. Try a more challenging problem and compare its runtime and memory usage.",
                    4);
        }

        return recommendationRepository.saveAll(recs);
    }

    private void addIfNew(List<Recommendation> recs,
                          Set<String> existingRecommendations,
                          User user,
                          Long interviewId,
                          String category,
                          String title,
                          String description,
                          int priority) {
        String key = category + ":" + title;
        if (existingRecommendations.add(key)) {
            recs.add(createRec(user, interviewId, category, title, description, priority));
        }
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
