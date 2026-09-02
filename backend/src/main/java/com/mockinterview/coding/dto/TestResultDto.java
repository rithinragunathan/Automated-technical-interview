package com.mockinterview.coding.dto;

public record TestResultDto(
        Long testCaseId,
        boolean passed,
        String actualOutput,
        Long executionTimeMs,
        String errorMessage,
        int testCaseNumber
) {}
