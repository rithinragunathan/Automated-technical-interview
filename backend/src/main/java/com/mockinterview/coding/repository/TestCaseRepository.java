package com.mockinterview.coding.repository;

import com.mockinterview.coding.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemIdAndHiddenFalse(Long problemId);
    List<TestCase> findByProblemId(Long problemId);
}
