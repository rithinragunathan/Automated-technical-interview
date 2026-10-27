package com.mockinterview.interview.repository;

import com.mockinterview.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByInterviewIdOrderBySequenceNumber(Long interviewId);
    Optional<InterviewQuestion> findByInterview_IdAndCodingProblem_Id(Long interviewId, Long codingProblemId);
}
