package com.mockinterview.coding.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubmissionResultDto(
        Long id,
        Long userId,
        Long problemId,
        String language,
        String status,
        int passedTests,
        int totalTests,
        Long executionTimeMs,
        Long memoryUsedKb,
        List<TestResultDto> testResults,
        LocalDateTime createdAt
) {}
