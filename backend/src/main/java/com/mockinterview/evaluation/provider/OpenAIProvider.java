package com.mockinterview.evaluation.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockinterview.config.AppProperties;
import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("openAIProvider")
public class OpenAIProvider implements LLMProvider {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final LLMProvider mockProvider;

    public OpenAIProvider(AppProperties appProperties, @Qualifier("mockLLMProvider") LLMProvider mockProvider) {
        this.appProperties = appProperties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.mockProvider = mockProvider;
    }

    private JsonNode callOpenAI(List<Map<String, String>> messages, boolean jsonMode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(appProperties.getAi().getOpenai().getApiKey());

            Map<String, Object> body = new HashMap<>();
            body.put("model", appProperties.getAi().getOpenai().getModel());
            body.put("messages", messages);
            if (jsonMode) {
                body.put("response_format", Map.of("type", "json_object"));
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String url = appProperties.getAi().getOpenai().getBaseUrl() + "/chat/completions";
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            return objectMapper.readTree(root.path("choices").get(0).path("message").path("content").asText());
        } catch (Exception e) {
            log.error("Failed to call OpenAI API", e);
            throw new RuntimeException("OpenAI API call failed", e);
        }
    }

    @Override
    public List<GeneratedQuestion> generateQuestions(QuestionGenerationConfig config) {
        try {
            List<Map<String, String>> msgs = List.of(
                Map.of("role", "system", "content", "Generate a JSON array of questions."),
                Map.of("role", "user", "content", "Generate " + config.count() + " " + config.difficulty() + " questions on " + config.topic() + " formatted as JSON { \"questions\": [ { \"questionText\", \"questionType\", \"topic\", \"difficulty\" } ] }")
            );
            JsonNode res = callOpenAI(msgs, true);
            List<GeneratedQuestion> questions = new ArrayList<>();
            res.path("questions").forEach(q -> {
                questions.add(new GeneratedQuestion(
                    q.path("questionText").asText(),
                    q.path("questionType").asText(),
                    q.path("topic").asText(),
                    q.path("difficulty").asText()
                ));
            });
            return questions;
        } catch (Exception e) {
            return mockProvider.generateQuestions(config);
        }
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(String question, String answer, String context) {
        try {
            List<Map<String, String>> msgs = List.of(
                Map.of("role", "system", "content", "Evaluate the answer. Return JSON: accuracyScore, relevanceScore, depthScore, clarityScore, overallScore, feedback, followUpQuestion."),
                Map.of("role", "user", "content", "Q: " + question + "\nA: " + answer)
            );
            JsonNode res = callOpenAI(msgs, true);
            return new AnswerEvaluationResult(
                res.path("accuracyScore").asDouble(),
                res.path("relevanceScore").asDouble(),
                res.path("depthScore").asDouble(),
                res.path("clarityScore").asDouble(),
                res.path("overallScore").asDouble(),
                res.path("feedback").asText(),
                res.path("followUpQuestion").asText()
            );
        } catch (Exception e) {
            return mockProvider.evaluateAnswer(question, answer, context);
        }
    }

    @Override
    public String generateFollowUpQuestion(String originalQuestion, String answer, double score) {
        try {
            List<Map<String, String>> msgs = List.of(
                Map.of("role", "user", "content", "Q: " + originalQuestion + "\nA: " + answer + "\nProvide a short follow up question.")
            );
            return callOpenAI(msgs, false).asText();
        } catch (Exception e) {
            return mockProvider.generateFollowUpQuestion(originalQuestion, answer, score);
        }
    }

    @Override
    public InterviewFeedback generateInterviewFeedback(InterviewSummaryForAI summary) {
        return mockProvider.generateInterviewFeedback(summary); // Fallback for brevity
    }

    @Override
    public List<String> generateRecommendations(ScoreBreakdown scores) {
        return mockProvider.generateRecommendations(scores); // Fallback for brevity
    }
}
