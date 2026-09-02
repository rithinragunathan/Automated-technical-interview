package com.mockinterview.sandbox.service;

import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;

public interface SandboxOrchestrator {
    ExecutionResult executeCode(ExecutionRequest request);
}
