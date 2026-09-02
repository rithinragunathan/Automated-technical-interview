package com.mockinterview.submission.controller;

import com.mockinterview.coding.dto.SubmissionRequest;
import com.mockinterview.coding.dto.SubmissionResultDto;
import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.submission.service.SubmissionService;
import com.mockinterview.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserService userService;

    public SubmissionController(SubmissionService submissionService, UserService userService) {
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmissionResultDto>> createSubmission(@Valid @RequestBody SubmissionRequest request) {
        Long userId = userService.getCurrentUser().getId();
        SubmissionResultDto result = "RUN".equalsIgnoreCase(request.getExecutionType()) 
                ? submissionService.runCode(userId, request) 
                : submissionService.submitCode(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Submission processed"));
    }

    @PostMapping("/execution/run")
    public ResponseEntity<ApiResponse<SubmissionResultDto>> runCode(@Valid @RequestBody SubmissionRequest request) {
        Long userId = userService.getCurrentUser().getId();
        request.setExecutionType("RUN");
        SubmissionResultDto result = submissionService.runCode(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Code executed"));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<ApiResponse<SubmissionResultDto>> getSubmission(@PathVariable Long id) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(submissionService.getSubmission(id, userId), "Submission retrieved"));
    }
}
