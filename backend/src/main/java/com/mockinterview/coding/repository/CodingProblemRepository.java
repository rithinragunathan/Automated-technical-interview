package com.mockinterview.coding.repository;

import com.mockinterview.coding.entity.CodingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long> {
    Optional<CodingProblem> findBySlug(String slug);
    List<CodingProblem> findByDifficultyAndActiveTrue(String difficulty);
    List<CodingProblem> findAllByActiveTrue();
}
