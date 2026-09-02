package com.mockinterview.admin.dto;

public record AdminStatsDto(
        long totalUsers,
        long totalInterviews,
        long totalProblems,
        long totalSubmissions,
        double avgOverallScore
) {}
