package com.mockinterview.recommendation.repository;

import com.mockinterview.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByPriorityAsc(Long userId);
    List<Recommendation> findByUserIdAndCompleted(Long userId, boolean completed);
}
