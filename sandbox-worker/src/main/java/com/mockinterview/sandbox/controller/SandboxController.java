package com.mockinterview.sandbox.controller;

import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import com.mockinterview.sandbox.service.JavaSandboxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sandbox")
@RequiredArgsConstructor
public class SandboxController {

    private final JavaSandboxService sandboxService;

    @PostMapping("/execute")
    public ResponseEntity<ExecutionResult> executeCode(@Valid @RequestBody ExecutionRequest request) {
        return ResponseEntity.ok(sandboxService.execute(request));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
