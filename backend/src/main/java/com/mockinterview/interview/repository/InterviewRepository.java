package com.mockinterview.interview.repository;

import com.mockinterview.interview.entity.Interview;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Interview> findByUserIdAndStatus(Long userId, Interview.InterviewStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select interview from Interview interview where interview.id = :interviewId")
    Optional<Interview> findByIdForReport(@Param("interviewId") Long interviewId);
}
