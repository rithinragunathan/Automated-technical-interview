package com.mockinterview.admin.dto;

import com.mockinterview.question.entity.Question;
import java.util.List;

public record CreateProblemRequest(
        String title,
        String slug,
        String description,
        Question.DifficultyLevel difficulty,
        String topic,
        String tags,
        String constraints,
        String inputFormat,
        String outputFormat,
        String examples,
        String timeComplexityHint,
        String spaceComplexityHint,
        String starterCodeJava,
        String starterCodePython,
        String starterCodeCpp,
        List<CreateTestCaseRequest> testCases
) {}
