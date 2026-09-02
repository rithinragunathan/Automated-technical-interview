package com.mockinterview.user.dto;

import com.mockinterview.analytics.dto.ScoreHistoryPoint;
import com.mockinterview.interview.dto.InterviewSummaryDto;

import java.util.List;
import java.util.Map;

public record DashboardStatsDto(
        double overallScore,
        double codingScore,
        double knowledgeScore,
        Double communicationScore,
        Double behaviorScore,
        int interviewsCompleted,
        int problemsSolved,
        long avgExecutionTimeMs,
        double successRate,
        List<InterviewSummaryDto> recentInterviews,
        List<Object> recentSubmissions,
        List<ScoreHistoryPoint> scoreHistory,
        Map<String, Double> topicPerformance,
        String proficiencyLevel
) {}
