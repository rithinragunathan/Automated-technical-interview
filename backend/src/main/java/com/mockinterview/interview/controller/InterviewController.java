package com.mockinterview.interview.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.interview.dto.InterviewDto;
import com.mockinterview.interview.dto.InterviewSummaryDto;
import com.mockinterview.interview.dto.StartInterviewRequest;
import com.mockinterview.interview.service.InterviewService;
import com.mockinterview.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final UserService userService;

    public InterviewController(InterviewService interviewService, UserService userService) {
        this.interviewService = interviewService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewDto>> createInterview(@Valid @RequestBody StartInterviewRequest request) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(interviewService.createInterview(userId, request), "Interview created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewSummaryDto>>> getUserInterviews() {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(interviewService.getUserInterviews(userId), "Interviews retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewDto>> getInterviewById(@PathVariable Long id) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(interviewService.getInterviewById(id, userId), "Interview retrieved"));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<InterviewDto>> startInterview(@PathVariable Long id) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(interviewService.startInterview(id, userId), "Interview started"));
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ApiResponse<InterviewDto>> finishInterview(@PathVariable Long id) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(interviewService.finishInterview(id, userId), "Interview finished"));
    }
}
