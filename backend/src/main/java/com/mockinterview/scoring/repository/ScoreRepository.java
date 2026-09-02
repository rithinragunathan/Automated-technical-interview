package com.mockinterview.scoring.repository;

import com.mockinterview.scoring.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByInterviewId(Long interviewId);
}
