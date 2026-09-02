package com.mockinterview.evaluation.repository;

import com.mockinterview.evaluation.entity.TechnicalAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicalAnswerRepository extends JpaRepository<TechnicalAnswer, Long> {
    List<TechnicalAnswer> findByInterviewId(Long interviewId);
}
