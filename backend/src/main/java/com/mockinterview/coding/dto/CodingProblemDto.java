package com.mockinterview.coding.dto;

import java.util.List;

public record CodingProblemDto(
        Long id,
        String title,
        String slug,
        String description,
        String difficulty,
        String topic,
        List<String> tags,
        String constraints,
        String inputFormat,
        String outputFormat,
        String examples,
        String timeComplexityHint,
        String spaceComplexityHint,
        String starterCodeJava,
        String starterCodePython,
        String starterCodeCpp,
        boolean active,
        List<TestCaseDto> publicTestCases
) {}
