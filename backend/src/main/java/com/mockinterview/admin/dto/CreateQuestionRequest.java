package com.mockinterview.admin.dto;

import com.mockinterview.question.entity.Question;

public record CreateQuestionRequest(
        String title,
        String content,
        Question.QuestionType questionType,
        Question.DifficultyLevel difficulty,
        String topic,
        String tags,
        String expectedAnswer,
        String evaluationRubric
) {}
