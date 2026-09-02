package com.mockinterview.evaluation.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    public ResponseEntity<ApiResponse<AnswerEvaluationResult>> evaluateAnswer(
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body) {
        String answerText = body.getOrDefault("answerText", "");
        AnswerEvaluationResult result = evaluationService.evaluateAnswer(interviewId, questionId, answerText);
        return ResponseEntity.ok(ApiResponse.success(result, "Answer evaluated"));
    }
}
