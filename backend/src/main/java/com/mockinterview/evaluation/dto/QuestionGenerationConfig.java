package com.mockinterview.evaluation.dto;

import java.util.List;

public record QuestionGenerationConfig(
        String interviewType,
        String difficulty,
        String topic,
        int count,
        List<String> excludedQuestions
) {
    public QuestionGenerationConfig(String interviewType, String difficulty, String topic, int count) {
        this(interviewType, difficulty, topic, count, List.of());
    }

    public QuestionGenerationConfig {
        excludedQuestions = excludedQuestions == null ? List.of() : List.copyOf(excludedQuestions);
    }
}
