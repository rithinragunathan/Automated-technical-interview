package com.mockinterview.sandbox.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionResult {
    private String stdout;
    private String stderr;
    private int exitCode;
    private long executionTimeMs;
    private long memoryUsedKb;
    private String compilationError;
    private boolean timedOut;
    private String systemError;
}
