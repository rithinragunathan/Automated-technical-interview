package com.mockinterview.evaluation.provider;

import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mockLLMProvider")
public class MockLLMProvider implements LLMProvider {

    @Override
    public List<GeneratedQuestion> generateQuestions(QuestionGenerationConfig config) {
        List<GeneratedQuestion> questions = new ArrayList<>();
        for (int i = 0; i < config.count(); i++) {
            questions.add(new GeneratedQuestion(
                    "Explain the concept of " + config.topic() + " in detail.",
                    "TECHNICAL",
                    config.topic(),
                    config.difficulty()
            ));
        }
        return questions;
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(String question, String answer, String context) {
        boolean hasKeywords = answer.toLowerCase().contains("memory") || answer.toLowerCase().contains("time complexity");
        double score = answer.length() > 50 ? (hasKeywords ? 85.0 : 70.0) : 40.0;
        
        return new AnswerEvaluationResult(
                score, score, score, score, score,
                "Good attempt. Consider adding more technical details.",
                "Can you provide a real-world example?"
        );
    }

    @Override
    public String generateFollowUpQuestion(String originalQuestion, String answer, double score) {
        return "What are the trade-offs of the approach you mentioned?";
    }

    @Override
    public InterviewFeedback generateInterviewFeedback(InterviewSummaryForAI summary) {
        return new InterviewFeedback(
                "The candidate showed solid understanding but needs more practice.",
                List.of("Good coding basics", "Clear communication"),
                List.of("Struggled with optimal space complexity")
        );
    }

    @Override
    public List<String> generateRecommendations(ScoreBreakdown scores) {
        return List.of("Practice more dynamic programming", "Review advanced data structures");
    }
}
