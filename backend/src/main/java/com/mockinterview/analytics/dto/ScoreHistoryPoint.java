package com.mockinterview.analytics.dto;

import java.time.LocalDateTime;

public record ScoreHistoryPoint(
        Long interviewId,
        LocalDateTime date,
        double overallScore,
        double codingScore,
        double knowledgeScore
) {}
