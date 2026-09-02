package com.mockinterview.interview.dto;

import java.time.LocalDateTime;

public record InterviewSummaryDto(
        Long id,
        String interviewType,
        String difficulty,
        String topic,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double overallScore,
        LocalDateTime createdAt
) {}
