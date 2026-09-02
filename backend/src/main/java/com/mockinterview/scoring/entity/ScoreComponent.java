package com.mockinterview.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_components")
@Getter
@Setter
public class ScoreComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Column(name = "component_value", nullable = false, precision = 5, scale = 2)
    private double componentValue;

    @Column(nullable = false, precision = 5, scale = 4)
    private double weight;

    @Column(name = "weighted_value", nullable = false, precision = 5, scale = 2)
    private double weightedValue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
