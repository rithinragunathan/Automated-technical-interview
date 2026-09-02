package com.mockinterview.analytics.repository;

import com.mockinterview.analytics.entity.SkillMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillMetricRepository extends JpaRepository<SkillMetric, Long> {
    List<SkillMetric> findByUserIdOrderByRecordedAtDesc(Long userId);
    List<SkillMetric> findByUserIdAndSkillName(Long userId, String skillName);
}
