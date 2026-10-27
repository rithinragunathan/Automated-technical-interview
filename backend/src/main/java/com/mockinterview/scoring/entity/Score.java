package com.mockinterview.scoring.entity;

import com.mockinterview.interview.entity.Interview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
@Getter
@Setter
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    @Column(name = "overall_score", nullable = false)
    private double overallScore = 0.0;

    @Column(name = "coding_score", nullable = false)
    private double codingScore = 0.0;

    @Column(name = "knowledge_score", nullable = false)
    private double knowledgeScore = 0.0;

    @Column(name = "communication_score")
    private Double communicationScore;

    @Column(name = "behavior_score")
    private Double behaviorScore;

    @Column(name = "proficiency_level", length = 50)
    private String proficiencyLevel;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();
}
