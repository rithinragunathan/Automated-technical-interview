package com.mockinterview.scoring.service;

import org.springframework.stereotype.Service;

@Service
public class PerformanceScorer {

    public double scoreTimeEfficiency(long executionTimeMs, String difficulty) {
        return switch (difficulty.toUpperCase()) {
            case "EASY" -> executionTimeMs < 100 ? 100 : executionTimeMs < 200 ? 80 : executionTimeMs < 500 ? 60 : executionTimeMs < 1000 ? 40 : 20;
            case "MEDIUM" -> executionTimeMs < 200 ? 100 : executionTimeMs < 500 ? 80 : executionTimeMs < 1000 ? 60 : executionTimeMs < 2000 ? 40 : 20;
            case "HARD" -> executionTimeMs < 500 ? 100 : executionTimeMs < 1000 ? 80 : executionTimeMs < 2000 ? 60 : executionTimeMs < 4000 ? 40 : 20;
            default -> 50;
        };
    }

    public double scoreSpaceEfficiency(long memoryUsedKb, String difficulty) {
        long memoryUsedMb = memoryUsedKb / 1024;
        return switch (difficulty.toUpperCase()) {
            case "EASY" -> memoryUsedMb < 5 ? 100 : memoryUsedMb < 15 ? 80 : memoryUsedMb < 30 ? 60 : 40;
            case "MEDIUM" -> memoryUsedMb < 15 ? 100 : memoryUsedMb < 30 ? 80 : memoryUsedMb < 60 ? 60 : 40;
            case "HARD" -> memoryUsedMb < 30 ? 100 : memoryUsedMb < 60 ? 80 : memoryUsedMb < 100 ? 60 : 40;
            default -> 50;
        };
    }
}
