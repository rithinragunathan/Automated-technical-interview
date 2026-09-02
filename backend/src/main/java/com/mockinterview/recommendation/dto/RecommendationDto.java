package com.mockinterview.recommendation.dto;

import java.util.List;

public record RecommendationDto(
        Long id,
        String category,
        String title,
        String description,
        int priority,
        List<String> resourceLinks,
        boolean completed
) {}
