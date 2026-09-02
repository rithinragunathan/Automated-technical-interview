package com.mockinterview.submission.websocket;

public record ExecutionStatusMessage(
        String submissionId,
        String status,
        String message,
        int progressPercent
) {}
