package com.mockinterview.coding.controller;

import com.mockinterview.coding.dto.CodingProblemDto;
import com.mockinterview.coding.dto.CodingProblemSummaryDto;
import com.mockinterview.coding.service.CodingProblemService;
import com.mockinterview.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@PreAuthorize("isAuthenticated()")
public class CodingProblemController {

    private final CodingProblemService codingProblemService;

    public CodingProblemController(CodingProblemService codingProblemService) {
        this.codingProblemService = codingProblemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CodingProblemSummaryDto>>> getProblems(
            Pageable pageable,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic
    ) {
        Page<CodingProblemSummaryDto> page = codingProblemService.getProblems(pageable, difficulty, topic);
        return ResponseEntity.ok(ApiResponse.success(page, "Problems retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CodingProblemDto>> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(codingProblemService.getProblemById(id), "Problem retrieved"));
    }
}
