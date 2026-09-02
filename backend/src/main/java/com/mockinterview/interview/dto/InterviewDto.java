package com.mockinterview.interview.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewDto(
        Long id,
        Long userId,
        String interviewType,
        String difficulty,
        String topic,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationSeconds,
        int questionsAttempted,
        int questionsCompleted,
        LocalDateTime createdAt,
        List<InterviewQuestionDto> questions
) {}
