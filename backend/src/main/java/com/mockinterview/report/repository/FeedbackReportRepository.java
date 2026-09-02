package com.mockinterview.report.repository;

import com.mockinterview.report.entity.FeedbackReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackReportRepository extends JpaRepository<FeedbackReport, Long> {
    Optional<FeedbackReport> findByInterviewId(Long interviewId);
}
