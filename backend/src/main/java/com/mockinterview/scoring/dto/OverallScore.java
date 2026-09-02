package com.mockinterview.scoring.dto;

import java.util.Map;

public record OverallScore(
        double codingScore,
        double knowledgeScore,
        Double communicationScore,
        Double behaviorScore,
        double overallScore,
        String proficiencyLevel,
        Map<String, Double> weights,
        String explanation
) {}
