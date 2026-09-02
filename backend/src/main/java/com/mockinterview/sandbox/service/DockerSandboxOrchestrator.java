package com.mockinterview.sandbox.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.mockinterview.config.AppProperties;
import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DockerSandboxOrchestrator implements SandboxOrchestrator {

    private final AppProperties appProperties;
    private final DockerClient dockerClient;

    public DockerSandboxOrchestrator(AppProperties appProperties) {
        this.appProperties = appProperties;

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(appProperties.getSandbox().getDockerHost())
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        this.dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    @Override
    public ExecutionResult executeCode(ExecutionRequest request) {
        String submissionId = UUID.randomUUID().toString();
        Path workspaceDir = Paths.get(appProperties.getSandbox().getWorkspaceBase(), submissionId);
        
        try {
            Files.createDirectories(workspaceDir);
            
            if ("java".equalsIgnoreCase(request.getLanguage())) {
                Files.writeString(workspaceDir.resolve("Main.java"), request.getSourceCode());
            } else {
                throw new UnsupportedOperationException("Only java is currently supported in this orchestrator");
            }

            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withNetworkMode("none")
                    .withMemory(appProperties.getSandbox().getMemoryBytes())
                    .withCpuPeriod(appProperties.getSandbox().getCpuPeriod())
                    .withCpuQuota(appProperties.getSandbox().getCpuQuota())
                    .withPidsLimit(50L)
                    .withBinds(new Bind(workspaceDir.toAbsolutePath().toString(), new Volume("/workspace")));

            String[] command = {
                    "sh", "-c",
                    "cd /workspace && javac Main.java 2> compile.err && echo '===COMPILE_OK===' && timeout " +
                    appProperties.getSandbox().getTimeoutSeconds() +
                    " java -Xmx200m -Djava.security.manager -cp /workspace Main > run.out 2> run.err; echo $? > exit.code"
            };

            CreateContainerResponse container = dockerClient.createContainerCmd(appProperties.getSandbox().getJavaImage())
                    .withHostConfig(hostConfig)
                    .withCmd(command)
                    .withWorkingDir("/workspace")
                    .exec();

            String containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();

            WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
            dockerClient.waitContainerCmd(containerId).exec(waitCallback);
            
            boolean finished = waitCallback.awaitCompletion(appProperties.getSandbox().getTimeoutSeconds() + 2, TimeUnit.SECONDS);

            if (!finished) {
                dockerClient.killContainerCmd(containerId).exec();
                return ExecutionResult.builder().timedOut(true).systemError(false).build();
            }

            // Cleanup container
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            
            // Read results from workspace
            Path compileErrPath = workspaceDir.resolve("compile.err");
            Path runOutPath = workspaceDir.resolve("run.out");
            Path runErrPath = workspaceDir.resolve("run.err");
            Path exitCodePath = workspaceDir.resolve("exit.code");

            String compileErr = Files.exists(compileErrPath) ? Files.readString(compileErrPath) : "";
            
            if (!compileErr.isEmpty()) {
                return ExecutionResult.builder()
                        .compilationError(compileErr)
                        .systemError(false)
                        .build();
            }

            String stdout = Files.exists(runOutPath) ? Files.readString(runOutPath) : "";
            String stderr = Files.exists(runErrPath) ? Files.readString(runErrPath) : "";
            Integer exitCode = Files.exists(exitCodePath) ? Integer.parseInt(Files.readString(exitCodePath).trim()) : -1;

            return ExecutionResult.builder()
                    .stdout(stdout)
                    .stderr(stderr)
                    .exitCode(exitCode)
                    .systemError(false)
                    .build();

        } catch (Exception e) {
            log.error("System error during execution", e);
            return ExecutionResult.builder().systemError(true).build();
        } finally {
            try {
                FileSystemUtils.deleteRecursively(workspaceDir);
            } catch (IOException e) {
                log.warn("Failed to clean up workspace: " + workspaceDir, e);
            }
        }
    }
}
