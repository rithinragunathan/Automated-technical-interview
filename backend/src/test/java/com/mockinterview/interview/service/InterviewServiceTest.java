package com.mockinterview.interview.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.interview.dto.StartInterviewRequest;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.entity.InterviewQuestion;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.coding.repository.CodingProblemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterviewServiceTest {

    @Test
    void mixedInterviewCreatesOneCodingTaskAndFourGeminiQuestions() {
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        InterviewQuestionRepository interviewQuestionRepository = mock(InterviewQuestionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CodingProblemRepository codingProblemRepository = mock(CodingProblemRepository.class);
        LLMProvider llmProvider = mock(LLMProvider.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(10L);
        when(userRepository.getReferenceById(10L)).thenReturn(user);

        Interview interview = new Interview();
        interview.setId(22L);
        interview.setUser(user);
        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> {
            Interview saved = invocation.getArgument(0);
            saved.setId(22L);
            return saved;
        });

        CodingProblem codingProblem = new CodingProblem();
        codingProblem.setId(31L);
        codingProblem.setTopic("DATA_STRUCTURES");
        when(codingProblemRepository.findByDifficultyAndActiveTrue(any())).thenReturn(List.of(codingProblem));
        when(interviewQuestionRepository
                .findTop25ByInterview_User_IdAndAiGeneratedTrueOrderByCreatedAtDesc(10L))
                .thenReturn(List.of());

        List<InterviewQuestion> questions = new ArrayList<>();
        when(interviewQuestionRepository.save(any(InterviewQuestion.class))).thenAnswer(invocation -> {
            InterviewQuestion question = invocation.getArgument(0);
            questions.add(question);
            return question;
        });
        when(interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(22L)).thenAnswer(invocation -> questions);
        when(llmProvider.generateQuestions(any())).thenAnswer(invocation -> {
            QuestionGenerationConfig config = invocation.getArgument(0);
            return java.util.stream.IntStream.range(0, config.count())
                    .mapToObj(index -> new GeneratedQuestion(
                            "Mixed interview question " + index,
                            "TECHNICAL",
                            "DATA_STRUCTURES",
                            "EASY"))
                    .toList();
        });

        StartInterviewRequest request = new StartInterviewRequest();
        request.setInterviewType("MIXED");
        request.setDifficulty("EASY");
        request.setTopic("DATA_STRUCTURES");
        InterviewService service = new InterviewService(
                interviewRepository,
                interviewQuestionRepository,
                userRepository,
                codingProblemRepository,
                llmProvider);

        service.createInterview(10L, request);

        ArgumentCaptor<QuestionGenerationConfig> configCaptor = ArgumentCaptor.forClass(QuestionGenerationConfig.class);
        verify(llmProvider).generateQuestions(configCaptor.capture());
        assertEquals(4, configCaptor.getValue().count());
        assertEquals(5, questions.size());
        assertEquals(1, questions.stream().filter(question -> question.getCodingProblem() != null).count());
    }

    @Test
    void generatesEightGeminiQuestionsAndExcludesRecentlyAskedQuestions() {
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        InterviewQuestionRepository interviewQuestionRepository = mock(InterviewQuestionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CodingProblemRepository codingProblemRepository = mock(CodingProblemRepository.class);
        LLMProvider llmProvider = mock(LLMProvider.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(9L);
        when(userRepository.getReferenceById(9L)).thenReturn(user);

        Interview interview = new Interview();
        interview.setId(21L);
        interview.setUser(user);
        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> {
            Interview saved = invocation.getArgument(0);
            saved.setId(21L);
            return saved;
        });

        InterviewQuestion previousQuestion = new InterviewQuestion();
        previousQuestion.setAiGenerated(true);
        previousQuestion.setAiQuestionText("How does a hash map handle collisions?");
        when(interviewQuestionRepository
                .findTop25ByInterview_User_IdAndAiGeneratedTrueOrderByCreatedAtDesc(9L))
                .thenReturn(List.of(previousQuestion));

        List<InterviewQuestion> questions = new ArrayList<>();
        when(interviewQuestionRepository.save(any(InterviewQuestion.class))).thenAnswer(invocation -> {
            InterviewQuestion question = invocation.getArgument(0);
            questions.add(question);
            return question;
        });
        when(interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(21L)).thenAnswer(invocation -> questions);
        when(llmProvider.generateQuestions(any())).thenAnswer(invocation -> {
            QuestionGenerationConfig config = invocation.getArgument(0);
            return java.util.stream.IntStream.range(0, config.count())
                    .mapToObj(index -> new GeneratedQuestion(
                            "Distinct question " + index,
                            "TECHNICAL",
                            "DATA_STRUCTURES",
                            "EASY"))
                    .toList();
        });

        StartInterviewRequest request = new StartInterviewRequest();
        request.setInterviewType("TECHNICAL_QA");
        request.setDifficulty("EASY");
        request.setTopic("DATA_STRUCTURES");
        InterviewService service = new InterviewService(
                interviewRepository,
                interviewQuestionRepository,
                userRepository,
                codingProblemRepository,
                llmProvider);

        service.createInterview(9L, request);

        ArgumentCaptor<QuestionGenerationConfig> configCaptor = ArgumentCaptor.forClass(QuestionGenerationConfig.class);
        verify(llmProvider).generateQuestions(configCaptor.capture());
        assertEquals(8, configCaptor.getValue().count());
        assertEquals(List.of("How does a hash map handle collisions?"), configCaptor.getValue().excludedQuestions());
        assertEquals(8, questions.size());
    }

    @Test
    void codingInterviewGeneratesEightGeminiQuestionsAndAddsOneRandomCodingProblem() {
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        InterviewQuestionRepository interviewQuestionRepository = mock(InterviewQuestionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CodingProblemRepository codingProblemRepository = mock(CodingProblemRepository.class);
        LLMProvider llmProvider = mock(LLMProvider.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(12L);
        when(userRepository.getReferenceById(12L)).thenReturn(user);

        Interview interview = new Interview();
        interview.setId(23L);
        interview.setUser(user);
        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> {
            Interview saved = invocation.getArgument(0);
            saved.setId(23L);
            return saved;
        });

        CodingProblem firstProblem = new CodingProblem();
        firstProblem.setId(41L);
        firstProblem.setTopic("DATA_STRUCTURES");
        CodingProblem secondProblem = new CodingProblem();
        secondProblem.setId(42L);
        secondProblem.setTopic("DATA_STRUCTURES");
        when(codingProblemRepository.findByDifficultyAndActiveTrue(any()))
                .thenReturn(List.of(firstProblem, secondProblem));
        when(interviewQuestionRepository
                .findTop25ByInterview_User_IdAndAiGeneratedTrueOrderByCreatedAtDesc(12L))
                .thenReturn(List.of());

        List<InterviewQuestion> questions = new ArrayList<>();
        when(interviewQuestionRepository.save(any(InterviewQuestion.class))).thenAnswer(invocation -> {
            InterviewQuestion question = invocation.getArgument(0);
            questions.add(question);
            return question;
        });
        when(interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(23L))
                .thenAnswer(invocation -> questions);
        when(llmProvider.generateQuestions(any())).thenAnswer(invocation -> {
            QuestionGenerationConfig config = invocation.getArgument(0);
            return java.util.stream.IntStream.range(0, config.count())
                    .mapToObj(index -> new GeneratedQuestion(
                            "Coding interview question " + index,
                            "TECHNICAL",
                            "DATA_STRUCTURES",
                            "EASY"))
                    .toList();
        });

        StartInterviewRequest request = new StartInterviewRequest();
        request.setInterviewType("CODING");
        request.setDifficulty("EASY");
        request.setTopic("DATA_STRUCTURES");
        InterviewService service = new InterviewService(
                interviewRepository,
                interviewQuestionRepository,
                userRepository,
                codingProblemRepository,
                llmProvider);

        service.createInterview(12L, request);

        ArgumentCaptor<QuestionGenerationConfig> configCaptor = ArgumentCaptor.forClass(QuestionGenerationConfig.class);
        verify(llmProvider).generateQuestions(configCaptor.capture());
        assertEquals("CODING", configCaptor.getValue().interviewType());
        assertEquals(8, configCaptor.getValue().count());
        assertEquals(9, questions.size());
        assertEquals(8, questions.stream().filter(InterviewQuestion::isAiGenerated).count());
        assertEquals(1, questions.stream().filter(question -> question.getCodingProblem() != null).count());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9),
                questions.stream().map(InterviewQuestion::getSequenceNumber).sorted().toList());
        assertTrue(List.of(41L, 42L).contains(questions.stream()
                .filter(question -> question.getCodingProblem() != null)
                .findFirst()
                .orElseThrow()
                .getCodingProblem()
                .getId()));
    }
}
