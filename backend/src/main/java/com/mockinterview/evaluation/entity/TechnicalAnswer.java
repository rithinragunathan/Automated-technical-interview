package com.mockinterview.evaluation.entity;

import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.entity.InterviewQuestion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "technical_answers")
@Getter
@Setter
public class TechnicalAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_question_id", nullable = false)
    private InterviewQuestion interviewQuestion;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "accuracy_score")
    private Double accuracyScore;

    @Column(name = "relevance_score")
    private Double relevanceScore;

    @Column(name = "depth_score")
    private Double depthScore;

    @Column(name = "clarity_score")
    private Double clarityScore;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
