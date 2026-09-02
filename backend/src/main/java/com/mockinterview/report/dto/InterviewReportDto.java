package com.mockinterview.report.dto;

import com.mockinterview.scoring.dto.OverallScore;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import com.mockinterview.recommendation.dto.RecommendationDto;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewReportDto(
        Long interviewId,
        Long userId,
        String interviewType,
        String difficulty,
        String topic,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationSeconds,
        OverallScore score,
        ScoreBreakdown codingBreakdown,
        List<Object> testResults,
        List<String> strengths,
        List<String> weaknesses,
        List<RecommendationDto> recommendations,
        String aiNarrative,
        int passedTests,
        int totalTests
) {}
