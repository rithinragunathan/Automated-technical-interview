package com.mockinterview.evaluation.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mockinterview.config.AppProperties;
import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.InterviewFeedback;
import com.mockinterview.evaluation.dto.InterviewSummaryForAI;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Component("geminiProvider")
public class GeminiProvider implements LLMProvider {

    private final GeminiConfig config;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public GeminiProvider(AppProperties appProperties) {
        this(appProperties, new RestTemplate());
    }

    GeminiProvider(AppProperties appProperties, RestTemplate restTemplate) {
        AppProperties.Ai.Gemini gemini = appProperties.getAi().getGemini();
        this.config = new GeminiConfig(gemini.getApiKey(), gemini.getBaseUrl(), gemini.getModel());
        this.objectMapper = new ObjectMapper();
        this.restTemplate = restTemplate;
    }

    @Override
    public List<GeneratedQuestion> generateQuestions(QuestionGenerationConfig request) {
        List<String> excludedQuestions = new ArrayList<>(request.excludedQuestions());
        for (int attempt = 1; attempt <= 3; attempt++) {
            JsonNode response = generateJson(questionPrompt(request, excludedQuestions));
            List<GeneratedQuestion> result = new ArrayList<>();
            Set<String> seen = excludedQuestions.stream()
                    .map(GeminiProvider::normalizeQuestion)
                    .collect(java.util.stream.Collectors.toCollection(HashSet::new));
            boolean hasDuplicate = false;
            for (JsonNode question : requiredArray(response, "questions")) {
                GeneratedQuestion generated = new GeneratedQuestion(
                        requiredText(question, "questionText"),
                        requiredText(question, "questionType"),
                        requiredText(question, "topic"),
                        requiredText(question, "difficulty"));
                result.add(generated);
                if (!seen.add(normalizeQuestion(generated.questionText()))) {
                    hasDuplicate = true;
                }
            }
            if (result.size() != request.count()) {
                throw new IllegalStateException("Gemini returned %d questions; expected %d"
                        .formatted(result.size(), request.count()));
            }
            if (!hasDuplicate) {
                return result;
            }
            excludedQuestions.addAll(result.stream().map(GeneratedQuestion::questionText).toList());
        }
        throw new IllegalStateException("Gemini returned repeated questions after three generation attempts");
    }

    private String questionPrompt(QuestionGenerationConfig request, List<String> excludedQuestions) {
        String excluded = excludedQuestions.isEmpty()
                ? "None"
                : excludedQuestions.stream()
                        .skip(Math.max(0, excludedQuestions.size() - 25))
                        .limit(25)
                        .map(question -> "- " + question)
                        .collect(java.util.stream.Collectors.joining("\n"));
        String questionGuidance = "Questions must be answerable aloud without requiring code execution. Keep each question focused and concise.";
        if ("CODING".equalsIgnoreCase(request.interviewType())) {
            questionGuidance = """
                    Ask coding-interview questions about problem-solving approach, algorithm and data-structure choices,
                    complexity analysis, edge cases, testing, or trade-offs. The candidate will answer aloud while completing
                    a separate coding exercise in an IDE; do not ask them to write code as the spoken answer.""";
        }
        return """
                Act as a technical interviewer. Generate exactly %d distinct, natural, spoken-answer-friendly interview questions.
                Interview type: %s
                Difficulty: %s
                Topic: %s
                %s
                Make each question meaningfully different from every other question and from the previously asked questions below.
                Do not include answers, hints, introductions, markdown, or questions outside the requested topic.
                Previously asked questions to avoid:
                %s
                Return JSON only in this shape, with questionType set to TECHNICAL:
                {"questions":[{"questionText":"...","questionType":"TECHNICAL","topic":"...","difficulty":"..."}]}
                """.formatted(request.count(), request.interviewType(), request.difficulty(), questionGuidance,
                request.topic() == null ? "general technical interviews"
                        : request.topic().replace('_', ' ').toLowerCase(Locale.ROOT),
                excluded);
    }

    private static String normalizeQuestion(String question) {
        return question.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(String question, String answer, String context) {
        JsonNode response = generateJson("""
                Evaluate the candidate answer on a 0-100 scale.
                Return JSON only with numeric accuracyScore, relevanceScore, depthScore,
                clarityScore, overallScore and string feedback, followUpQuestion.
                Question: %s
                Answer: %s
                Context: %s
                """.formatted(question, answer, context == null ? "" : context));
        return new AnswerEvaluationResult(
                requiredDouble(response, "accuracyScore"),
                requiredDouble(response, "relevanceScore"),
                requiredDouble(response, "depthScore"),
                requiredDouble(response, "clarityScore"),
                requiredDouble(response, "overallScore"),
                requiredText(response, "feedback"),
                requiredText(response, "followUpQuestion"));
    }

    @Override
    public String generateFollowUpQuestion(String originalQuestion, String answer, double score) {
        JsonNode response = generateJson("""
                Generate one concise follow-up interview question as JSON only:
                {"followUpQuestion":"..."}
                Original question: %s
                Candidate answer: %s
                Score: %.2f
                """.formatted(originalQuestion, answer, score));
        return requiredText(response, "followUpQuestion");
    }

    @Override
    public InterviewFeedback generateInterviewFeedback(InterviewSummaryForAI summary) {
        JsonNode response = generateJson("""
                Generate interview feedback as JSON only:
                {"narrative":"...","strengths":["..."],"weaknesses":["..."]}
                Interview type: %s
                Difficulty: %s
                Topic: %s
                Coding score: %.2f
                Knowledge score: %.2f
                Topics: %s
                Answered questions: %s
                """.formatted(summary.interviewType(), summary.difficulty(), summary.topic(),
                summary.codingScore(), summary.knowledgeScore(), summary.topics(), summary.answeredQuestions()));
        return new InterviewFeedback(
                requiredText(response, "narrative"),
                textList(response, "strengths"),
                textList(response, "weaknesses"));
    }

    @Override
    public List<String> generateRecommendations(ScoreBreakdown scores) {
        JsonNode response = generateJson("""
                Generate practical coding recommendations as JSON only:
                {"recommendations":["..."]}
                Scores: correctness %.2f, time efficiency %.2f, space efficiency %.2f,
                code quality %.2f, robustness %.2f, readability %.2f, total %.2f
                Findings: %s
                Complexity: %s
                """.formatted(scores.correctnessScore(), scores.timeEfficiencyScore(),
                scores.spaceEfficiencyScore(), scores.codeQualityScore(), scores.robustnessScore(),
                scores.readabilityScore(), scores.totalCodingScore(), scores.findings(),
                scores.complexityEstimate()));
        return textList(response, "recommendations");
    }

    private JsonNode generateJson(String prompt) {
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is required for AI evaluation");
        }
        String url = "%s%s:generateContent".formatted(config.baseUrl(), config.model());
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.putArray("contents").addObject()
                    .putArray("parts").addObject().put("text", prompt);
            body.putObject("generationConfig").put("responseMimeType", "application/json");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", config.apiKey());
            HttpEntity<ObjectNode> request = new HttpEntity<>(body, headers);
            JsonNode root = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    root = restTemplate.postForObject(url, request, JsonNode.class);
                    break;
                } catch (RestClientResponseException ex) {
                    if (!isTransientStatus(ex.getStatusCode().value()) || attempt == 3) {
                        throw ex;
                    }
                    log.warn("Gemini temporarily returned HTTP {}; retrying attempt {} of 3",
                            ex.getStatusCode().value(), attempt + 1);
                    pauseBeforeRetry(attempt);
                } catch (ResourceAccessException ex) {
                    if (attempt == 3) {
                        throw ex;
                    }
                    log.warn("Gemini request had a temporary network failure; retrying attempt {} of 3", attempt + 1);
                    pauseBeforeRetry(attempt);
                }
            }
            String text = root == null ? "" : root.path("candidates").path(0)
                    .path("content").path("parts").path(0).path("text").asText("");
            if (text.isBlank()) {
                throw new IllegalStateException("Gemini returned an empty response");
            }
            return objectMapper.readTree(text);
        } catch (RestClientException | java.io.IOException ex) {
            log.error("Gemini request failed", ex);
            throw new IllegalStateException("Gemini request failed", ex);
        }
    }

    private boolean isTransientStatus(int status) {
        return status == 429 || status == 502 || status == 503 || status == 504;
    }

    private void pauseBeforeRetry(int attempt) {
        try {
            Thread.sleep(attempt * 1_000L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Gemini request retry was interrupted", ex);
        }
    }

    private static JsonNode requiredField(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        if (value == null || value.isNull()) {
            throw new IllegalStateException("Gemini response is missing field: " + field);
        }
        return value;
    }

    private static String requiredText(JsonNode node, String field) {
        JsonNode value = requiredField(node, field);
        if (!value.isTextual() || value.asText().isBlank()) {
            throw new IllegalStateException("Gemini response field is not valid text: " + field);
        }
        return value.asText();
    }

    private static double requiredDouble(JsonNode node, String field) {
        JsonNode value = requiredField(node, field);
        if (!value.isNumber()) {
            throw new IllegalStateException("Gemini response field is not numeric: " + field);
        }
        return value.asDouble();
    }

    private static List<String> textList(JsonNode node, String field) {
        List<String> values = new ArrayList<>();
        for (JsonNode item : requiredArray(node, field)) {
            if (!item.isTextual() || item.asText().isBlank()) {
                throw new IllegalStateException("Gemini response contains invalid text in: " + field);
            }
            values.add(item.asText());
        }
        return values;
    }

    private static JsonNode requiredArray(JsonNode node, String field) {
        JsonNode value = requiredField(node, field);
        if (!value.isArray()) {
            throw new IllegalStateException("Gemini response field is not an array: " + field);
        }
        return value;
    }

    private record GeminiConfig(String apiKey, String baseUrl, String model) {}
}
