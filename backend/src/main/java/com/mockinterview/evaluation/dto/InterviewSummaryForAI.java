package com.mockinterview.evaluation.dto;

import java.util.List;

public record InterviewSummaryForAI(
        String interviewType,
        String difficulty,
        String topic,
        double codingScore,
        double knowledgeScore,
        List<String> topics,
        List<String> answeredQuestions
) {}
