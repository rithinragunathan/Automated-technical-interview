package com.mockinterview.scoring.service;

import com.mockinterview.config.AppProperties;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import com.mockinterview.submission.entity.ExecutionResultEntity;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.entity.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScoringEngine {

    private final CorrectnessScorer correctnessScorer;
    private final PerformanceScorer performanceScorer;
    private final CodeQualityAnalyzer codeQualityAnalyzer;
    private final AppProperties appProperties;

    public ScoringEngine(CorrectnessScorer correctnessScorer,
                         PerformanceScorer performanceScorer,
                         CodeQualityAnalyzer codeQualityAnalyzer,
                         AppProperties appProperties) {
        this.correctnessScorer = correctnessScorer;
        this.performanceScorer = performanceScorer;
        this.codeQualityAnalyzer = codeQualityAnalyzer;
        this.appProperties = appProperties;
    }

    public ScoreBreakdown scoreSubmission(Submission submission, List<TestResult> testResults, ExecutionResultEntity executionResult, String difficulty) {
        double correctnessWeight = appProperties.getScoring().getCodingWeights().getOrDefault("correctness", 0.40);
        double timeWeight = appProperties.getScoring().getCodingWeights().getOrDefault("time-efficiency", 0.15);
        double spaceWeight = appProperties.getScoring().getCodingWeights().getOrDefault("space-efficiency", 0.10);
        double qualityWeight = appProperties.getScoring().getCodingWeights().getOrDefault("code-quality", 0.15);
        double robustnessWeight = appProperties.getScoring().getCodingWeights().getOrDefault("robustness", 0.10);
        double readabilityWeight = appProperties.getScoring().getCodingWeights().getOrDefault("readability", 0.10);

        double rawCorrectness = correctnessScorer.score(submission.getPassedTests(), submission.getTotalTests());
        double rawTime = performanceScorer.scoreTimeEfficiency(submission.getExecutionTimeMs() != null ? submission.getExecutionTimeMs() : 0, difficulty);
        double rawSpace = performanceScorer.scoreSpaceEfficiency(submission.getMemoryUsedKb() != null ? submission.getMemoryUsedKb() : 0, difficulty);
        
        CodeQualityAnalyzer.CodeQualityResult quality = codeQualityAnalyzer.analyze(submission.getSourceCode(), submission.getLanguage());
        double rawQuality = quality.score();
        
        // Robustness: penalty if public pass but hidden fail
        long publicPassed = testResults.stream().filter(tr -> !tr.getTestCase().isHidden() && tr.isPassed()).count();
        long publicTotal = testResults.stream().filter(tr -> !tr.getTestCase().isHidden()).count();
        double robustness = rawCorrectness;
        if (publicTotal > 0 && publicPassed == publicTotal && submission.getPassedTests() < submission.getTotalTests()) {
            robustness = rawCorrectness * 0.9; 
        }
        
        double rawReadability = rawQuality * 0.8;

        double totalScore = (rawCorrectness * correctnessWeight) +
                            (rawTime * timeWeight) +
                            (rawSpace * spaceWeight) +
                            (rawQuality * qualityWeight) +
                            (robustness * robustnessWeight) +
                            (rawReadability * readabilityWeight);

        String complexity = analyzeComplexity(submission.getSourceCode());

        return new ScoreBreakdown(
                rawCorrectness,
                rawTime,
                rawSpace,
                rawQuality,
                robustness,
                rawReadability,
                totalScore,
                quality.findings(),
                complexity
        );
    }

    private String analyzeComplexity(String code) {
        int loopCount = code.split("for\\s*\\(").length - 1 + code.split("while\\s*\\(").length - 1;
        if (loopCount >= 3) return "O(n³) (estimated)";
        if (loopCount == 2) return "O(n²) (estimated)";
        if (loopCount == 1) return "O(n) (estimated)";
        return "O(1) or O(log n) (estimated)";
    }
}
