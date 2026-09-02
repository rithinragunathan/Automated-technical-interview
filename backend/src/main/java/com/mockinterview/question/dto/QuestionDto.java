package com.mockinterview.question.dto;

public record QuestionDto(
        Long id,
        String title,
        String content,
        String questionType,
        String difficulty,
        String topic,
        String tags,
        String evaluationRubric,
        boolean active
) {}
