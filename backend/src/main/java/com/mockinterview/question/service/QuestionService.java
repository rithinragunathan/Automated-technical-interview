package com.mockinterview.question.service;

import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.question.dto.QuestionDto;
import com.mockinterview.question.entity.Question;
import com.mockinterview.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionDto> getQuestions() {
        return questionRepository.findAll().stream()
                .filter(Question::isActive)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public QuestionDto getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        return mapToDto(q);
    }

    public List<QuestionDto> getRandomQuestions(String type, String difficulty, int count) {
        return questionRepository.findRandom(type, difficulty, count).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private QuestionDto mapToDto(Question q) {
        return new QuestionDto(
                q.getId(),
                q.getTitle(),
                q.getContent(),
                q.getQuestionType().name(),
                q.getDifficulty().name(),
                q.getTopic(),
                q.getTags(),
                q.getEvaluationRubric(),
                q.isActive()
        );
    }
}
