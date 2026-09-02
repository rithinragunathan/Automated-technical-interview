package com.mockinterview.scoring.repository;

import com.mockinterview.scoring.entity.ScoreComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreComponentRepository extends JpaRepository<ScoreComponent, Long> {
    List<ScoreComponent> findByScoreId(Long scoreId);
}
