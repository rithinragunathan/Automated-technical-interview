package com.mockinterview.coding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull
    private Long problemId;

    private Long interviewId;

    @NotBlank
    private String language;

    @NotBlank
    private String sourceCode;

    private String executionType;
}
