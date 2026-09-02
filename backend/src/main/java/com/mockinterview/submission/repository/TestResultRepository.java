package com.mockinterview.submission.repository;

import com.mockinterview.submission.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findBySubmissionId(Long submissionId);
    List<TestResult> findBySubmissionIdAndPassed(Long submissionId, boolean passed);
}
