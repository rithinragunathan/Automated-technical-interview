package com.mockinterview.scoring.service;

import com.mockinterview.config.AppProperties;
import com.mockinterview.scoring.dto.OverallScore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OverallScoreCalculator {

    private final AppProperties appProperties;

    public OverallScoreCalculator(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public OverallScore calculate(double codingScore, double knowledgeScore, Double communicationScore, Double behaviorScore) {
        double wCoding = appProperties.getScoring().getWeights().getOrDefault("coding", 0.40);
        double wKnowledge = appProperties.getScoring().getWeights().getOrDefault("knowledge", 0.20);
        double wComm = appProperties.getScoring().getWeights().getOrDefault("communication", 0.20);
        double wBehavior = appProperties.getScoring().getWeights().getOrDefault("behavior", 0.20);

        if (communicationScore == null) {
            double extra = wComm / 2;
            wCoding += extra;
            wKnowledge += extra;
            wComm = 0;
            communicationScore = 0.0;
        }

        if (behaviorScore == null) {
            double extra = wBehavior / 2;
            wCoding += extra;
            wKnowledge += extra;
            wBehavior = 0;
            behaviorScore = 0.0;
        }

        double overall = (codingScore * wCoding) +
                         (knowledgeScore * wKnowledge) +
                         (communicationScore * wComm) +
                         (behaviorScore * wBehavior);

        Map<String, Integer> t = appProperties.getScoring().getThresholds();
        String level = "Expert";
        if (overall <= t.getOrDefault("beginner-max", 39)) level = "Beginner";
        else if (overall <= t.getOrDefault("developing-max", 59)) level = "Developing";
        else if (overall <= t.getOrDefault("intermediate-max", 74)) level = "Intermediate";
        else if (overall <= t.getOrDefault("advanced-max", 89)) level = "Advanced";

        Map<String, Double> weights = new HashMap<>();
        weights.put("coding", wCoding);
        weights.put("knowledge", wKnowledge);
        weights.put("communication", wComm);
        weights.put("behavior", wBehavior);

        String explanation = String.format("Coding(%.0f%%): %.1f + Knowledge(%.0f%%): %.1f + Comm(%.0f%%): %.1f + Behavior(%.0f%%): %.1f",
                wCoding * 100, codingScore, wKnowledge * 100, knowledgeScore, wComm * 100, communicationScore, wBehavior * 100, behaviorScore);

        return new OverallScore(codingScore, knowledgeScore, communicationScore, behaviorScore, overall, level, weights, explanation);
    }
}
