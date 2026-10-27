package com.mockinterview.scoring.service;

import com.mockinterview.config.AppProperties;
import com.mockinterview.scoring.dto.OverallScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverallScoreCalculatorTest {

    private OverallScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getScoring().setWeights(Map.of(
                "coding", 0.40,
                "knowledge", 0.20,
                "communication", 0.20,
                "behavior", 0.20));
        properties.getScoring().setThresholds(Map.of(
                "beginner-max", 39,
                "developing-max", 59,
                "intermediate-max", 74,
                "advanced-max", 89));
        calculator = new OverallScoreCalculator(properties);
    }

    @Test
    void doesNotInventKnowledgeScoreWhenOnlyCodingEvidenceExists() {
        OverallScore score = calculator.calculate(0.0, null, null, null);

        assertEquals(0.0, score.overallScore());
        assertEquals(1.0, score.weights().get("coding"));
        assertEquals(0.0, score.weights().get("knowledge"));
    }

    @Test
    void usesEvaluatedKnowledgeScoreWhenCodingEvidenceIsMissing() {
        OverallScore score = calculator.calculate(null, 56.0, null, null);

        assertEquals(56.0, score.overallScore());
        assertEquals(0.0, score.weights().get("coding"));
        assertEquals(1.0, score.weights().get("knowledge"));
    }

    @Test
    void returnsZeroWhenNoScoredEvidenceExists() {
        OverallScore score = calculator.calculate(null, null, null, null);

        assertEquals(0.0, score.overallScore());
        assertEquals(0.0, score.weights().get("coding"));
        assertEquals(0.0, score.weights().get("knowledge"));
    }
}
