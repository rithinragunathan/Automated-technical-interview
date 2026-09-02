package com.mockinterview.scoring.dto;

import java.util.List;

public record ScoreBreakdown(
        double correctnessScore,
        double timeEfficiencyScore,
        double spaceEfficiencyScore,
        double codeQualityScore,
        double robustnessScore,
        double readabilityScore,
        double totalCodingScore,
        List<String> findings,
        String complexityEstimate
) {}
