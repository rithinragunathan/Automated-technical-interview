package com.mockinterview.evaluation.dto;

import java.util.List;

public record InterviewFeedback(
        String narrative,
        List<String> strengths,
        List<String> weaknesses
) {}
