package com.mockinterview.interview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartInterviewRequest {
    @NotNull
    private String interviewType;
    
    @NotNull
    private String difficulty;
    
    private String topic;

    private Integer durationMinutes;
}
