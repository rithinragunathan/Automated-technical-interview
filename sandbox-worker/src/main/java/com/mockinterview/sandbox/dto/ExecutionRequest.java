package com.mockinterview.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExecutionRequest {
    @NotBlank
    private String language;
    @NotBlank
    private String sourceCode;
    private String runnerCode;
    private String stdin;
    private int timeoutSeconds = 10;
    private long memoryBytes = 268435456;
}
