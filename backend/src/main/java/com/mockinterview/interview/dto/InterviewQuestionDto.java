package com.mockinterview.interview.dto;

import java.time.LocalDateTime;

public record InterviewQuestionDto(
        Long id,
        Integer sequenceNumber,
        String questionText,
        String questionType,
        Long codingProblemId,
        LocalDateTime askedAt,
        LocalDateTime completedAt
) {}
