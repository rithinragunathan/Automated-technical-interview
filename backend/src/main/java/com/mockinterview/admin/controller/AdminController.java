package com.mockinterview.admin.controller;

import com.mockinterview.admin.dto.AdminStatsDto;
import com.mockinterview.admin.dto.CreateProblemRequest;
import com.mockinterview.admin.dto.CreateQuestionRequest;
import com.mockinterview.admin.service.AdminService;
import com.mockinterview.auth.entity.User;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/problems")
    public ResponseEntity<ApiResponse<CodingProblem>> createProblem(@RequestBody CreateProblemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.createProblem(request), "Problem created"));
    }

    @PutMapping("/problems/{id}")
    public ResponseEntity<ApiResponse<CodingProblem>> updateProblem(@PathVariable Long id, @RequestBody CreateProblemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateProblem(id, request), "Problem updated"));
    }

    @DeleteMapping("/problems/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProblem(@PathVariable Long id) {
        adminService.deleteProblem(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Problem deleted"));
    }

    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<Question>> createQuestion(@RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.createQuestion(request), "Question created"));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(pageable), "Users retrieved"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStats(), "Stats retrieved"));
    }
}
