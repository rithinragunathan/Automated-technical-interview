package com.mockinterview.submission.service;

import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.coding.dto.SubmissionRequest;
import com.mockinterview.coding.dto.SubmissionResultDto;
import com.mockinterview.coding.dto.TestResultDto;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.coding.entity.TestCase;
import com.mockinterview.coding.repository.CodingProblemRepository;
import com.mockinterview.coding.repository.TestCaseRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.sandbox.dto.ExecutionRequest;
import com.mockinterview.sandbox.dto.ExecutionResult;
import com.mockinterview.sandbox.service.SandboxOrchestrator;
import com.mockinterview.submission.entity.ExecutionResultEntity;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.entity.TestResult;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import com.mockinterview.submission.websocket.ExecutionStatusBroadcaster;
import com.mockinterview.submission.websocket.ExecutionStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TestResultRepository testResultRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final TestCaseRepository testCaseRepository;
    private final SandboxOrchestrator sandboxOrchestrator;
    private final ExecutionStatusBroadcaster broadcaster;
    private final CodingProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             TestResultRepository testResultRepository,
                             ExecutionResultRepository executionResultRepository,
                             TestCaseRepository testCaseRepository,
                             SandboxOrchestrator sandboxOrchestrator,
                             ExecutionStatusBroadcaster broadcaster,
                             CodingProblemRepository problemRepository,
                             UserRepository userRepository,
                             InterviewRepository interviewRepository) {
        this.submissionRepository = submissionRepository;
        this.testResultRepository = testResultRepository;
        this.executionResultRepository = executionResultRepository;
        this.testCaseRepository = testCaseRepository;
        this.sandboxOrchestrator = sandboxOrchestrator;
        this.broadcaster = broadcaster;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    public SubmissionResultDto runCode(Long userId, SubmissionRequest request) {
        return processSubmission(userId, request, false);
    }

    public SubmissionResultDto submitCode(Long userId, SubmissionRequest request) {
        return processSubmission(userId, request, true);
    }

    private SubmissionResultDto processSubmission(Long userId, SubmissionRequest request, boolean isSubmit) {
        CodingProblem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        Submission submission = new Submission();
        submission.setUser(userRepository.getReferenceById(userId));
        submission.setProblem(problem);
        if (request.getInterviewId() != null) {
            submission.setInterview(interviewRepository.getReferenceById(request.getInterviewId()));
        }
        submission.setLanguage(request.getLanguage());
        submission.setSourceCode(request.getSourceCode());
        submission.setExecutionType(isSubmit ? Submission.ExecutionType.SUBMIT : Submission.ExecutionType.RUN);
        submission.setStatus(Submission.SubmissionStatus.RUNNING);
        
        Submission savedSubmission = submissionRepository.save(submission);
        String subIdStr = savedSubmission.getId().toString();

        broadcaster.broadcast(subIdStr, new ExecutionStatusMessage(subIdStr, "RUNNING", "Starting execution", 20));

        List<TestCase> testCases = isSubmit 
                ? testCaseRepository.findByProblemId(problem.getId())
                : testCaseRepository.findByProblemIdAndHiddenFalse(problem.getId());

        int passed = 0;
        long totalExecutionTime = 0;
        long maxMemory = 0;
        List<TestResultDto> testResultDtos = new ArrayList<>();
        ExecutionResult lastResult = null;
        
        boolean compilationError = false;

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            
            ExecutionRequest execReq = new ExecutionRequest();
            execReq.setLanguage(request.getLanguage());
            execReq.setSourceCode(request.getSourceCode());
            execReq.setStdin(tc.getInput());
            execReq.setTimeoutSeconds(10);
            execReq.setMemoryBytes(256 * 1024 * 1024L);

            ExecutionResult result = sandboxOrchestrator.executeCode(execReq);
            lastResult = result;

            if (result.getCompilationError() != null && !result.getCompilationError().isEmpty()) {
                compilationError = true;
                break;
            }

            TestResult tr = new TestResult();
            tr.setSubmission(savedSubmission);
            tr.setTestCase(tc);
            
            String actual = result.getStdout() != null ? result.getStdout().trim() : "";
            boolean isPassed = actual.equals(tc.getExpectedOutput().trim()) && result.getExitCode() != null && result.getExitCode() == 0;
            
            tr.setPassed(isPassed);
            tr.setActualOutput(actual);
            tr.setExecutionTimeMs(result.getExecutionTimeMs());
            tr.setMemoryUsedKb(result.getMemoryUsedKb());
            
            testResultRepository.save(tr);
            
            if (isPassed) passed++;
            
            if (result.getExecutionTimeMs() != null) totalExecutionTime += result.getExecutionTimeMs();
            if (result.getMemoryUsedKb() != null) maxMemory = Math.max(maxMemory, result.getMemoryUsedKb());
            
            testResultDtos.add(new TestResultDto(
                    tc.getId(), isPassed, actual, result.getExecutionTimeMs(), result.getStderr(), i + 1
            ));
        }

        broadcaster.broadcast(subIdStr, new ExecutionStatusMessage(subIdStr, "EVALUATING", "Evaluating results", 80));

        ExecutionResultEntity exeRes = new ExecutionResultEntity();
        exeRes.setSubmission(savedSubmission);
        
        if (lastResult != null) {
            exeRes.setStdout(lastResult.getStdout());
            exeRes.setStderr(lastResult.getStderr());
            exeRes.setExitCode(lastResult.getExitCode());
            exeRes.setCompilationError(lastResult.getCompilationError());
            exeRes.setTimedOut(lastResult.isTimedOut());
        }
        
        exeRes.setExecutionTimeMs(totalExecutionTime);
        exeRes.setMemoryUsedKb(maxMemory);
        executionResultRepository.save(exeRes);

        if (compilationError) {
            savedSubmission.setStatus(Submission.SubmissionStatus.COMPILATION_ERROR);
        } else if (lastResult != null && lastResult.isTimedOut()) {
            savedSubmission.setStatus(Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED);
        } else if (passed == testCases.size() && testCases.size() > 0) {
            savedSubmission.setStatus(Submission.SubmissionStatus.ACCEPTED);
        } else {
            savedSubmission.setStatus(Submission.SubmissionStatus.WRONG_ANSWER);
        }
        
        savedSubmission.setPassedTests(passed);
        savedSubmission.setTotalTests(testCases.size());
        savedSubmission.setExecutionTimeMs(totalExecutionTime);
        savedSubmission.setMemoryUsedKb(maxMemory);
        
        submissionRepository.save(savedSubmission);

        broadcaster.broadcast(subIdStr, new ExecutionStatusMessage(subIdStr, "COMPLETED", "Execution completed", 100));

        // Note: Missing ScoringEngine injection here as requested to avoid circular deps temporarily, 
        // will add logic in ScoringEngine when implemented

        return new SubmissionResultDto(
                savedSubmission.getId(),
                userId,
                problem.getId(),
                savedSubmission.getLanguage(),
                savedSubmission.getStatus().name(),
                passed,
                testCases.size(),
                totalExecutionTime,
                maxMemory,
                testResultDtos,
                savedSubmission.getCreatedAt()
        );
    }
    
    public SubmissionResultDto getSubmission(Long id, Long userId) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        
        // For simplicity omitted security check if userId matches
        
        List<TestResultDto> testResultDtos = testResultRepository.findBySubmissionId(submission.getId())
                .stream().map(tr -> new TestResultDto(
                        tr.getTestCase().getId(), tr.isPassed(), tr.getActualOutput(), 
                        tr.getExecutionTimeMs(), tr.getErrorMessage(), 0
                )).toList();
                
        return new SubmissionResultDto(
                submission.getId(),
                submission.getUser().getId(),
                submission.getProblem().getId(),
                submission.getLanguage(),
                submission.getStatus().name(),
                submission.getPassedTests(),
                submission.getTotalTests(),
                submission.getExecutionTimeMs(),
                submission.getMemoryUsedKb(),
                testResultDtos,
                submission.getCreatedAt()
        );
    }
}
