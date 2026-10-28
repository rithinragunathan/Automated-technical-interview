package com.mockinterview.evaluation.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mockinterview.config.AppProperties;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiProviderTest {

    @Test
    void includesRecentQuestionsInGeminiPromptAndReturnsUniqueQuestions() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("https://gemini.test/models/gemini-test:generateContent"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "How does a hash map handle collisions?")))
                .andRespond(withSuccess(geminiResponse("How do trees balance themselves?", "What is a graph traversal?"),
                        MediaType.APPLICATION_JSON));

        GeminiProvider provider = new GeminiProvider(configuredProperties(), restTemplate);
        List<com.mockinterview.evaluation.dto.GeneratedQuestion> questions = provider.generateQuestions(
                new QuestionGenerationConfig(
                        "TECHNICAL_QA",
                        "MEDIUM",
                        "DATA_STRUCTURES",
                        2,
                        List.of("How does a hash map handle collisions?")));

        assertEquals(2, questions.size());
        assertEquals("How do trees balance themselves?", questions.get(0).questionText());
        server.verify();
    }

    @Test
    void retriesWhenGeminiReturnsDuplicateQuestions() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("https://gemini.test/models/gemini-test:generateContent"))
                .andRespond(withSuccess(geminiResponse("What is a hash map?", "What is a hash map!"),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://gemini.test/models/gemini-test:generateContent"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("What is a hash map!")))
                .andRespond(withSuccess(geminiResponse("How do trees balance?", "When should a graph use BFS?"),
                        MediaType.APPLICATION_JSON));

        GeminiProvider provider = new GeminiProvider(configuredProperties(), restTemplate);
        List<com.mockinterview.evaluation.dto.GeneratedQuestion> questions = provider.generateQuestions(
                new QuestionGenerationConfig("TECHNICAL_QA", "EASY", "DATA_STRUCTURES", 2));

        assertEquals(2, questions.size());
        assertEquals("How do trees balance?", questions.get(0).questionText());
        server.verify();
    }

    @Test
    void asksForSpokenCodingInterviewQuestionsInCodingInterviews() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("https://gemini.test/models/gemini-test:generateContent"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "separate coding exercise in an IDE")))
                .andRespond(withSuccess(geminiResponse("How would you test this algorithm?"),
                        MediaType.APPLICATION_JSON));

        GeminiProvider provider = new GeminiProvider(configuredProperties(), restTemplate);
        List<com.mockinterview.evaluation.dto.GeneratedQuestion> questions = provider.generateQuestions(
                new QuestionGenerationConfig("CODING", "MEDIUM", "ALGORITHMS", 1));

        assertEquals(1, questions.size());
        assertEquals("How would you test this algorithm?", questions.getFirst().questionText());
        server.verify();
    }

    private AppProperties configuredProperties() {
        AppProperties properties = new AppProperties();
        properties.getAi().getGemini().setApiKey("test-api-key");
        properties.getAi().getGemini().setBaseUrl("https://gemini.test/models/");
        properties.getAi().getGemini().setModel("gemini-test");
        return properties;
    }

    private String geminiResponse(String... questions) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ArrayNode candidates = response.putArray("candidates");
        ObjectNode candidate = candidates.addObject();
        ObjectNode content = candidate.putObject("content");
        ArrayNode parts = content.putArray("parts");
        ObjectNode part = parts.addObject();
        ObjectNode generated = mapper.createObjectNode();
        ArrayNode questionArray = generated.putArray("questions");
        for (String questionText : questions) {
            questionArray.addObject()
                    .put("questionText", questionText)
                    .put("questionType", "TECHNICAL")
                    .put("topic", "DATA_STRUCTURES")
                    .put("difficulty", "EASY");
        }
        part.put("text", mapper.writeValueAsString(generated));
        return mapper.writeValueAsString(response);
    }
}
