package com.mockinterview.evaluation.provider;

import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.scoring.dto.ScoreBreakdown;

import java.util.List;

public interface LLMProvider {
    List<GeneratedQuestion> generateQuestions(QuestionGenerationConfig config);
    AnswerEvaluationResult evaluateAnswer(String question, String answer, String context);
    String generateFollowUpQuestion(String originalQuestion, String answer, double score);
    InterviewFeedback generateInterviewFeedback(InterviewSummaryForAI summary);
    List<String> generateRecommendations(ScoreBreakdown scores);
}
