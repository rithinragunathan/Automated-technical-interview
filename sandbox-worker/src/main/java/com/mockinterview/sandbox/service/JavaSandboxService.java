package com.mockinterview.sandbox.service;

import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
            String sourceCode = request.getSourceCode();
            if (request.getRunnerCode() != null && !request.getRunnerCode().isBlank()) {
                sourceCode += "\n\n" + request.getRunnerCode();
            }
            Files.writeString(sourceFile, sourceCode);
            Files.writeString(workspacePath.resolve("input.txt"),
                    request.getStdin() == null ? "" : request.getStdin());

            String containerWorkspace = "/workspace/" + uuid;
            String command = String.format(
                    "cd %s && javac Main.java 2>compile_errors.txt; " +
                            "if [ $? -ne 0 ]; then printf 'COMPILE_ERROR\\n'; cat compile_errors.txt; exit 0; fi; " +
                            "printf 'COMPILE_OK\\n'; timeout %d java -Xmx200m -cp %s Main < input.txt >run.out 2>run.err; " +
                            "code=$?; cat run.out; if [ -s run.err ]; then printf '\\n===STDERR===\\n'; cat run.err; fi; exit $code",
                    containerWorkspace,
                    request.getTimeoutSeconds(),
                    containerWorkspace);
            Path executionLog = workspacePath.resolve("execution.log");
            Process process = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "--network", "none",
                    "--memory", Long.toString(properties.getMemoryBytes()),
                    "--memory-swap", Long.toString(properties.getMemoryBytes()),
                    "--cpus", "0.5",
                    "--pids-limit", Long.toString(properties.getPidLimit()),
                    "--security-opt", "no-new-privileges",
                    "--read-only",
                    "--tmpfs", "/tmp:rw,noexec,nosuid,size=16m",
                    "--volume", properties.getWorkspaceVolume() + ":/workspace",
                    "--workdir", containerWorkspace,
                    properties.getJavaImage(),
                    "sh", "-c", command)
                    .redirectErrorStream(true)
                    .redirectOutput(executionLog.toFile())
                    .start();

            boolean finished = process.waitFor(request.getTimeoutSeconds() + 30L, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return ExecutionResult.builder()
                        .timedOut(true)
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            String output = Files.exists(executionLog) ? Files.readString(executionLog) : "";
            int markerEnd = output.indexOf('\n');
            String marker = markerEnd >= 0 ? output.substring(0, markerEnd) : output;
            String body = markerEnd >= 0 ? output.substring(markerEnd + 1) : "";
            if ("COMPILE_ERROR".equals(marker)) {
                return ExecutionResult.builder()
                        .compilationError(body)
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }
            if (!"COMPILE_OK".equals(marker)) {
                throw new IllegalStateException("Docker runner failed: " + output);
            }

            String stderr = "";
            int stderrMarker = body.indexOf("\n===STDERR===\n");
            if (stderrMarker >= 0) {
                stderr = body.substring(stderrMarker + "\n===STDERR===\n".length());
                body = body.substring(0, stderrMarker);
            }
            int exitCode = process.exitValue();
            return ExecutionResult.builder()
                    .stdout(body)
                    .stderr(stderr)
                    .exitCode(exitCode)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .memoryUsedKb(0L)
                    .timedOut(exitCode == 124)
                    .build();
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
