package com.mockinterview.sandbox.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JavaSandboxService {

    private final SandboxProperties properties;

    public ExecutionResult execute(ExecutionRequest request) {
        long startTime = System.currentTimeMillis();
        
        if (!"java".equalsIgnoreCase(request.getLanguage())) {
            return ExecutionResult.builder()
                    .systemError("Unsupported language: " + request.getLanguage())
                    .build();
        }

        String uuid = UUID.randomUUID().toString();
        Path workspacePath = Paths.get(properties.getWorkspaceBase(), uuid);

        try {
            Files.createDirectories(workspacePath);
            Path sourceFile = workspacePath.resolve("Main.java");
            Files.writeString(sourceFile, request.getSourceCode());

            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("unix:///var/run/docker.sock")
                    .build();
            
            try (DockerClient dockerClient = DockerClientBuilder.getInstance(config).build()) {
                
                try {
                    dockerClient.inspectImageCmd(properties.getJavaImage()).exec();
                } catch (NotFoundException e) {
                    log.info("Pulling image {}", properties.getJavaImage());
                    dockerClient.pullImageCmd(properties.getJavaImage()).start().awaitCompletion();
                }

                String cmd = String.format("cd /workspace && javac Main.java 2>compile_errors.txt; if [ $? -ne 0 ]; then echo 'COMPILE_ERROR'; cat compile_errors.txt; exit 1; fi; echo 'COMPILE_OK'; timeout %d java -Xmx200m -cp /workspace Main", request.getTimeoutSeconds());

                HostConfig hostConfig = HostConfig.newHostConfig()
                        .withBinds(new Bind(workspacePath.toAbsolutePath().toString(), new Volume("/workspace")))
                        .withMemory(properties.getMemoryBytes())
                        .withMemorySwap(properties.getMemoryBytes())
                        .withCpuPeriod(properties.getCpuPeriod())
                        .withCpuQuota(properties.getCpuQuota())
                        .withPidsLimit(properties.getPidLimit())
                        .withReadonlyRootfs(false)
                        .withSecurityOpts(Collections.singletonList("no-new-privileges"));

                CreateContainerResponse container = dockerClient.createContainerCmd(properties.getJavaImage())
                        .withCmd("sh", "-c", cmd)
                        .withNetworkDisabled(true)
                        .withHostConfig(hostConfig)
                        .exec();

                dockerClient.startContainerCmd(container.getId()).exec();

                WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
                dockerClient.waitContainerCmd(container.getId()).exec(waitCallback);
                waitCallback.awaitCompletion(request.getTimeoutSeconds() + 5, TimeUnit.SECONDS);

                StringBuilder stdout = new StringBuilder();
                StringBuilder stderr = new StringBuilder();

                dockerClient.logContainerCmd(container.getId())
                        .withStdOut(true)
                        .withStdErr(true)
                        .exec(new LogContainerResultCallback() {
                            @Override
                            public void onNext(Frame item) {
                                String payload = new String(item.getPayload(), StandardCharsets.UTF_8);
                                if (item.getStreamType() == StreamType.STDERR) {
                                    stderr.append(payload);
                                } else {
                                    stdout.append(payload);
                                }
                            }
                        }).awaitCompletion();

                int exitCode = dockerClient.inspectContainerCmd(container.getId()).exec().getState().getExitCodeLong().intValue();
                
                dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();

                String out = stdout.toString();
                String err = stderr.toString();
                
                ExecutionResult.ExecutionResultBuilder resultBuilder = ExecutionResult.builder()
                        .exitCode(exitCode)
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .stderr(err);

                if (out.startsWith("COMPILE_ERROR\n")) {
                    resultBuilder.compilationError(out.substring("COMPILE_ERROR\n".length()));
                } else if (out.startsWith("COMPILE_OK\n")) {
                    resultBuilder.stdout(out.substring("COMPILE_OK\n".length()));
                    if (exitCode == 124) {
                        resultBuilder.timedOut(true);
                    }
                } else {
                    resultBuilder.stdout(out);
                }

                return resultBuilder.build();
            }

        } catch (Exception e) {
            log.error("Execution failed", e);
            return ExecutionResult.builder()
                    .systemError("Execution failed: " + e.getMessage())
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } finally {
            deleteDirectory(workspacePath.toFile());
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
