package com.mockinterview.interview.repository;

import com.mockinterview.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Interview> findByUserIdAndStatus(Long userId, Interview.InterviewStatus status);
}
