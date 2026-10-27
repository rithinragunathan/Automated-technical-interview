package com.mockinterview.sandbox.service;

import com.mockinterview.config.AppProperties;
import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DockerSandboxOrchestrator implements SandboxOrchestrator {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public DockerSandboxOrchestrator(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ExecutionResult executeCode(ExecutionRequest request) {
        try {
            ExecutionResult result = restTemplate.postForObject(
                    appProperties.getSandbox().getWorkerUrl() + "/sandbox/execute",
                    request,
                    ExecutionResult.class);
            if (result == null) {
                throw new IllegalStateException("Sandbox worker returned an empty response");
            }
            return result;
        } catch (RestClientException | IllegalStateException ex) {
            log.error("Sandbox worker request failed", ex);
            return ExecutionResult.builder()
                    .systemError("Code execution service is temporarily unavailable.")
                    .stderr("Code execution service is temporarily unavailable.")
                    .build();
        }
    }
}
