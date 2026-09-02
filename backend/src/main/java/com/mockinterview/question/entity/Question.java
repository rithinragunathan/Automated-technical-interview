package com.mockinterview.question.entity;

import com.mockinterview.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;

    @Column(length = 100)
    private String topic;

    // tags as comma separated for simplicity
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String expectedAnswer;

    @Column(columnDefinition = "TEXT")
    private String evaluationRubric;

    @Column(nullable = false)
    private boolean active = true;

    public enum QuestionType {
        TECHNICAL, BEHAVIORAL, CODING, FOLLOW_UP
    }

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}
