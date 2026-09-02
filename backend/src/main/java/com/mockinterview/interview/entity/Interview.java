package com.mockinterview.interview.entity;

import com.mockinterview.auth.entity.User;
import com.mockinterview.common.audit.Auditable;
import com.mockinterview.question.entity.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
public class Interview extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false)
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Question.DifficultyLevel difficulty;

    @Column(length = 100)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.PENDING;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "questions_attempted", nullable = false)
    private int questionsAttempted = 0;

    @Column(name = "questions_completed", nullable = false)
    private int questionsCompleted = 0;

    public enum InterviewType { CODING, TECHNICAL_QA, MIXED }
    public enum InterviewStatus { PENDING, ACTIVE, COMPLETED, ABANDONED }
}
