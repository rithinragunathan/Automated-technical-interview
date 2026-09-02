package com.mockinterview.question.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.question.dto.QuestionDto;
import com.mockinterview.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestions() {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestions(), "Questions retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDto>> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestionById(id), "Question retrieved"));
    }
}
