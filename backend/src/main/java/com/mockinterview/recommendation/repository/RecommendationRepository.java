package com.mockinterview.recommendation.repository;

import com.mockinterview.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByPriorityAsc(Long userId);
    List<Recommendation> findByInterviewId(Long interviewId);
    List<Recommendation> findByUserIdAndCompleted(Long userId, boolean completed);
    java.util.Optional<Recommendation> findByIdAndUserId(Long id, Long userId);
}
