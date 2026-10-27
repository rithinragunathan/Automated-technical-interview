package com.mockinterview.interview.service;

import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.common.exception.ApiException;
import com.mockinterview.evaluation.dto.GeneratedQuestion;
import com.mockinterview.evaluation.dto.QuestionGenerationConfig;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.interview.dto.InterviewDto;
import com.mockinterview.interview.dto.InterviewQuestionDto;
import com.mockinterview.interview.dto.InterviewSummaryDto;
import com.mockinterview.interview.dto.StartInterviewRequest;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.entity.InterviewQuestion;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.coding.repository.CodingProblemRepository;
import com.mockinterview.question.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final CodingProblemRepository codingProblemRepository;
    private final LLMProvider llmProvider;

    public InterviewService(InterviewRepository interviewRepository,
                            InterviewQuestionRepository interviewQuestionRepository,
                            UserRepository userRepository,
                            CodingProblemRepository codingProblemRepository,
                            LLMProvider llmProvider) {
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
        this.codingProblemRepository = codingProblemRepository;
        this.llmProvider = llmProvider;
    }

    public InterviewDto createInterview(Long userId, StartInterviewRequest request) {
        Interview interview = new Interview();
        interview.setUser(userRepository.getReferenceById(userId));
        interview.setInterviewType(Interview.InterviewType.valueOf(request.getInterviewType().toUpperCase()));
        interview.setDifficulty(Question.DifficultyLevel.valueOf(request.getDifficulty().toUpperCase()));
        interview.setTopic(request.getTopic());
        interview.setStatus(Interview.InterviewStatus.PENDING);
        interview.setDurationSeconds((request.getDurationMinutes() != null ? request.getDurationMinutes() : 45) * 60);
        
        Interview saved = interviewRepository.save(interview);

        attachQuestions(saved, request);
        if (interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(saved.getId()).isEmpty()) {
            throw new ApiException("No interview questions are available for this selection.", 422);
        }
        
        return mapToDto(saved);
    }

    private void attachQuestions(Interview interview, StartInterviewRequest request) {
        int sequence = 1;
        Interview.InterviewType type = interview.getInterviewType();
        if (type == Interview.InterviewType.CODING || type == Interview.InterviewType.MIXED) {
            List<CodingProblem> problems = codingProblemRepository.findByDifficultyAndActiveTrue(interview.getDifficulty());
            if (problems.isEmpty()) {
                problems = codingProblemRepository.findAllByActiveTrue();
                if (!problems.isEmpty()) {
                    log.warn("No active coding problem for difficulty {}; using an available active problem", interview.getDifficulty());
                }
            }
            List<CodingProblem> focusedProblems = problems.stream()
                    .filter(problem -> matchesTopic(request.getTopic(), problem.getTopic(), problem.getTags()))
                    .toList();
            if (!focusedProblems.isEmpty()) {
                problems = focusedProblems;
            } else if (request.getTopic() != null && !request.getTopic().isBlank()) {
                List<CodingProblem> topicProblems = codingProblemRepository.findAllByActiveTrue().stream()
                        .filter(problem -> matchesTopic(request.getTopic(), problem.getTopic(), problem.getTags()))
                        .toList();
                if (!topicProblems.isEmpty()) {
                    problems = topicProblems;
                    log.warn("No coding problem matches difficulty {}; using a problem for topic {}",
                            interview.getDifficulty(), request.getTopic());
                } else {
                    log.warn("No coding problem matches topic {}; using an available active problem", request.getTopic());
                }
            } else if (!problems.isEmpty()) {
                log.warn("No coding problem matches topic {}; using an available active problem", request.getTopic());
            }
            if (!problems.isEmpty()) {
                InterviewQuestion question = new InterviewQuestion();
                question.setInterview(interview);
                question.setCodingProblem(problems.get(0));
                question.setSequenceNumber(sequence++);
                interviewQuestionRepository.save(question);
            }
        }
        if (type == Interview.InterviewType.TECHNICAL_QA || type == Interview.InterviewType.MIXED) {
            int questionCount = type == Interview.InterviewType.MIXED ? 2 : 5;
            List<GeneratedQuestion> questions;
            try {
                questions = llmProvider.generateQuestions(new QuestionGenerationConfig(
                        type.name(),
                        interview.getDifficulty().name(),
                        request.getTopic(),
                        questionCount
                ));
            } catch (IllegalStateException ex) {
                log.warn("Gemini could not generate questions for interview {}", interview.getId(), ex);
                throw new ApiException("Gemini is temporarily unable to generate interview questions. Please try again shortly.", 503);
            }
            if (questions.size() != questionCount) {
                throw new ApiException("Gemini did not generate the required number of interview questions. Please try again.", 502);
            }
            for (GeneratedQuestion question : questions) {
                InterviewQuestion interviewQuestion = new InterviewQuestion();
                interviewQuestion.setInterview(interview);
                interviewQuestion.setAiGenerated(true);
                interviewQuestion.setAiQuestionText(question.questionText());
                interviewQuestion.setSequenceNumber(sequence++);
                interviewQuestionRepository.save(interviewQuestion);
            }
        }
    }

    private boolean matchesTopic(String requestedTopic, String actualTopic, String tags) {
        if (requestedTopic == null || requestedTopic.isBlank()) {
            return true;
        }
        String normalizedContent = normalizeTopic((actualTopic == null ? "" : actualTopic)
                + " " + (tags == null ? "" : tags));
        return topicTerms(requestedTopic).stream()
                .map(InterviewService::normalizeTopic)
                .anyMatch(normalizedContent::contains);
    }

    private List<String> topicTerms(String requestedTopic) {
        return switch (requestedTopic.toUpperCase(Locale.ROOT)) {
            case "DATA_STRUCTURES" -> List.of("data structures", "array", "linked list", "hash", "tree", "graph", "stack", "queue");
            case "ALGORITHMS" -> List.of("algorithm", "sorting", "searching", "dynamic programming", "two sum");
            case "SYSTEM_DESIGN" -> List.of("system design", "architecture", "scalability");
            case "JAVA_SPRING" -> List.of("java", "spring", "jvm");
            case "DATABASE" -> List.of("database", "sql", "mysql", "query");
            case "FRONTEND" -> List.of("frontend", "react", "javascript", "css", "html");
            default -> List.of(requestedTopic);
        };
    }

    private static String normalizeTopic(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    public InterviewDto startInterview(Long interviewId, Long userId) {
        Interview interview = getInterview(interviewId, userId);
        interview.setStatus(Interview.InterviewStatus.ACTIVE);
        interview.setStartTime(LocalDateTime.now());
        return mapToDto(interviewRepository.save(interview));
    }

    public InterviewDto finishInterview(Long interviewId, Long userId) {
        Interview interview = getInterview(interviewId, userId);
        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interview.setEndTime(LocalDateTime.now());
        
        if (interview.getStartTime() != null) {
            interview.setDurationSeconds((int) Duration.between(interview.getStartTime(), interview.getEndTime()).getSeconds());
        }
        
        // ReportService.generateReport(interviewId) would be triggered here (e.g., via events or direct call)
        
        return mapToDto(interviewRepository.save(interview));
    }

    public InterviewDto getInterviewById(Long id, Long userId) {
        return mapToDto(getInterview(id, userId));
    }

    public List<InterviewSummaryDto> getUserInterviews(Long userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(i -> new InterviewSummaryDto(
                        i.getId(),
                        i.getInterviewType().name(),
                        i.getDifficulty().name(),
                        i.getTopic(),
                        i.getStatus().name(),
                        i.getStartTime(),
                        i.getEndTime(),
                        null, // overallScore to be fetched from ScoreRepository
                        i.getCreatedAt()
                )).collect(Collectors.toList());
    }
    
    private Interview getInterview(Long id, Long userId) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        if (!interview.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Interview not found");
        }
        return interview;
    }

    private InterviewDto mapToDto(Interview i) {
        List<InterviewQuestionDto> questions = interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(i.getId())
                .stream().map(iq -> new InterviewQuestionDto(
                        iq.getId(),
                        iq.getSequenceNumber(),
                        iq.isAiGenerated() ? iq.getAiQuestionText()
                                : iq.getQuestion() != null ? iq.getQuestion().getContent()
                                : iq.getCodingProblem() != null ? iq.getCodingProblem().getTitle() : "",
                        iq.getQuestion() != null ? iq.getQuestion().getQuestionType().name()
                                : iq.getCodingProblem() != null ? "CODING" : "AI_GENERATED",
                        iq.getCodingProblem() != null ? iq.getCodingProblem().getId() : null,
                        iq.getAskedAt(),
                        iq.getCompletedAt()
                )).collect(Collectors.toList());

        return new InterviewDto(
                i.getId(),
                i.getUser().getId(),
                i.getInterviewType().name(),
                i.getDifficulty().name(),
                i.getTopic(),
                i.getStatus().name(),
                i.getStartTime(),
                i.getEndTime(),
                i.getDurationSeconds(),
                i.getQuestionsAttempted(),
                i.getQuestionsCompleted(),
                i.getCreatedAt(),
                questions
        );
    }
}
