package com.mockinterview.coding.dto;

import java.util.List;

public record TestCaseDto(
        Long id,
        String input,
        String expectedOutput,
        String description
) {}
