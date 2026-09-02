package com.mockinterview.admin.dto;

public record CreateTestCaseRequest(
        String input,
        String expectedOutput,
        boolean hidden,
        double weight,
        String description
) {}
