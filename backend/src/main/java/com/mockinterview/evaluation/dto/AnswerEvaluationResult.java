package com.mockinterview.evaluation.dto;

public record AnswerEvaluationResult(
        double accuracyScore,
        double relevanceScore,
        double depthScore,
        double clarityScore,
        double overallScore,
        String feedback,
        String followUpQuestion
) {}
