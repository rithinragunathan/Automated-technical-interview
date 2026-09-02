package com.mockinterview.scoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CodeQualityAnalyzer {

    public record CodeQualityResult(double score, List<String> findings, List<String> suggestions) {}

    public CodeQualityResult analyze(String sourceCode, String language) {
        if (!"java".equalsIgnoreCase(language)) {
            return new CodeQualityResult(100.0, List.of("Quality analysis only supported for Java"), List.of());
        }

        double score = 100.0;
        List<String> findings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // Single letter variable names
        Pattern singleLetterPattern = Pattern.compile("\\b(?:int|long|double|float|String)\\s+([a-hl-zA-Z])\\b");
        Matcher slm = singleLetterPattern.matcher(sourceCode);
        int singleLetterCount = 0;
        while (slm.find()) {
            singleLetterCount++;
        }
        if (singleLetterCount > 0) {
            score -= Math.min(15, singleLetterCount * 5);
            findings.add("Found " + singleLetterCount + " single-letter variables.");
            suggestions.add("Use descriptive variable names instead of single letters.");
        }

        // Magic numbers
        Pattern magicNumberPattern = Pattern.compile("(?<![-a-zA-Z0-9_])\\b[2-9]\\b|\\b[1-9][0-9]+\\b");
        Matcher mnm = magicNumberPattern.matcher(sourceCode.replaceAll("\".*?\"", ""));
        int magicNumberCount = 0;
        while (mnm.find()) {
            magicNumberCount++;
        }
        if (magicNumberCount > 3) {
            score -= 10;
            findings.add("Found magic numbers.");
            suggestions.add("Extract magic numbers into named constants.");
        }

        // System.out.println
        if (sourceCode.contains("System.out.println")) {
            findings.add("Debug print statements detected.");
            suggestions.add("Remove System.out.println in production code.");
        }

        // Comments
        if (sourceCode.contains("//") || sourceCode.contains("/*")) {
            score = Math.min(100, score + 5);
            findings.add("Code contains comments.");
        } else {
            score -= 5;
            findings.add("No comments found.");
            suggestions.add("Add comments to explain complex logic.");
        }
        
        // Nested loops simple heuristic
        if (sourceCode.split("for\\s*\\(").length > 2 || sourceCode.split("while\\s*\\(").length > 2) {
            findings.add("Detected multiple loops, check for possible O(N^2) complexity or higher.");
        }

        return new CodeQualityResult(Math.max(0, score), findings, suggestions);
    }
}
