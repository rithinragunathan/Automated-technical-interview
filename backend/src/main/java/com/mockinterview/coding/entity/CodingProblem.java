package com.mockinterview.coding.entity;

import com.mockinterview.common.audit.Auditable;
import com.mockinterview.question.entity.Question.DifficultyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coding_problems")
@Getter
@Setter
public class CodingProblem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;

    @Column(length = 100)
    private String topic;

    // tags as comma separated string
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(columnDefinition = "TEXT")
    private String inputFormat;

    @Column(columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "jsonb")
    private String examples;

    @Column(length = 100)
    private String timeComplexityHint;

    @Column(length = 100)
    private String spaceComplexityHint;

    @Column(columnDefinition = "TEXT")
    private String starterCodeJava;

    @Column(columnDefinition = "TEXT")
    private String starterCodePython;

    @Column(columnDefinition = "TEXT")
    private String starterCodeCpp;

    @Column(nullable = false)
    private boolean active = true;
}
