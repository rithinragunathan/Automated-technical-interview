package com.mockinterview.submission.repository;

import com.mockinterview.submission.entity.ExecutionResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExecutionResultRepository extends JpaRepository<ExecutionResultEntity, Long> {
    Optional<ExecutionResultEntity> findBySubmissionId(Long submissionId);
}
