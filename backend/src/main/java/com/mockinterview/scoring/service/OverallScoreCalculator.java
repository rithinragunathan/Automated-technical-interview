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

    public OverallScore calculate(Double codingScore, Double knowledgeScore, Double communicationScore, Double behaviorScore) {
        double wCoding = appProperties.getScoring().getWeights().getOrDefault("coding", 0.40);
        double wKnowledge = appProperties.getScoring().getWeights().getOrDefault("knowledge", 0.20);
        double wComm = appProperties.getScoring().getWeights().getOrDefault("communication", 0.20);
        double wBehavior = appProperties.getScoring().getWeights().getOrDefault("behavior", 0.20);

        if (communicationScore == null) {
            wCoding += wComm / 2;
            wKnowledge += wComm / 2;
            wComm = 0;
        }

        if (behaviorScore == null) {
            wCoding += wBehavior / 2;
            wKnowledge += wBehavior / 2;
            wBehavior = 0;
        }

        if (codingScore == null) wCoding = 0;
        if (knowledgeScore == null) wKnowledge = 0;
        if (communicationScore == null) wComm = 0;
        if (behaviorScore == null) wBehavior = 0;

        double totalWeight = wCoding + wKnowledge + wComm + wBehavior;
        double overall = totalWeight == 0
                ? 0
                : ((codingScore != null ? codingScore * wCoding : 0)
                    + (knowledgeScore != null ? knowledgeScore * wKnowledge : 0)
                    + (communicationScore != null ? communicationScore * wComm : 0)
                    + (behaviorScore != null ? behaviorScore * wBehavior : 0)) / totalWeight;

        Map<String, Integer> t = appProperties.getScoring().getThresholds();
        String level = "Expert";
        if (overall <= t.getOrDefault("beginner-max", 39)) level = "Beginner";
        else if (overall <= t.getOrDefault("developing-max", 59)) level = "Developing";
        else if (overall <= t.getOrDefault("intermediate-max", 74)) level = "Intermediate";
        else if (overall <= t.getOrDefault("advanced-max", 89)) level = "Advanced";

        Map<String, Double> weights = new HashMap<>();
        weights.put("coding", totalWeight == 0 ? 0 : wCoding / totalWeight);
        weights.put("knowledge", totalWeight == 0 ? 0 : wKnowledge / totalWeight);
        weights.put("communication", totalWeight == 0 ? 0 : wComm / totalWeight);
        weights.put("behavior", totalWeight == 0 ? 0 : wBehavior / totalWeight);

        String explanation = String.format("Coding(%.0f%%): %.1f + Knowledge(%.0f%%): %.1f + Comm(%.0f%%): %.1f + Behavior(%.0f%%): %.1f",
                weights.get("coding") * 100, codingScore != null ? codingScore : 0,
                weights.get("knowledge") * 100, knowledgeScore != null ? knowledgeScore : 0,
                weights.get("communication") * 100, communicationScore != null ? communicationScore : 0,
                weights.get("behavior") * 100, behaviorScore != null ? behaviorScore : 0);

        return new OverallScore(
                codingScore != null ? codingScore : 0,
                knowledgeScore != null ? knowledgeScore : 0,
                communicationScore,
                behaviorScore,
                overall,
                level,
                weights,
                explanation);
    }
}
