package com.mockinterview.scoring.service;

import org.springframework.stereotype.Service;

@Service
public class CorrectnessScorer {

    public double score(int passedTests, int totalTests) {
        if (totalTests == 0) return 0.0;
        return ((double) passedTests / totalTests) * 100.0;
    }
}
