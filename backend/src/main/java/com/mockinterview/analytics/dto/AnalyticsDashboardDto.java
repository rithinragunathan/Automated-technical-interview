package com.mockinterview.analytics.dto;

import java.util.List;
import java.util.Map;

public record AnalyticsDashboardDto(
        List<ScoreHistoryPoint> scoreHistory,
        Map<String, Double> topicPerformance,
        Map<String, Double> difficultyPerformance,
        double avgTimePerProblem,
        double successRate,
        List<String> weakestConcepts,
        List<String> strongestConcepts,
        int totalInterviews,
        int totalProblems
) {}
