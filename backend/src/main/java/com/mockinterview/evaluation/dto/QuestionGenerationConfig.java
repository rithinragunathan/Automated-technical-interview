package com.mockinterview.evaluation.dto;

public record QuestionGenerationConfig(
        String interviewType,
        String difficulty,
        String topic,
        int count
) {}
