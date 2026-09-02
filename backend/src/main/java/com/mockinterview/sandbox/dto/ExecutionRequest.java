package com.mockinterview.sandbox.dto;

import lombok.Data;

@Data
public class ExecutionRequest {
    private String language;
    private String sourceCode;
    private String stdin;
    private int timeoutSeconds;
    private long memoryBytes;
}
