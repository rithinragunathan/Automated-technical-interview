package com.mockinterview.sandbox.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionResult {
    private String stdout;
    private String stderr;
    private Integer exitCode;
    private Long executionTimeMs;
    private Long memoryUsedKb;
    private String compilationError;
    private boolean timedOut;
    private boolean systemError;
}
