package com.mockinterview.coding.dto;

import java.util.List;

public record CodingProblemSummaryDto(
        Long id,
        String title,
        String slug,
        String difficulty,
        String topic,
        List<String> tags
) {}
