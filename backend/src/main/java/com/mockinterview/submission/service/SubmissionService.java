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
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.entity.InterviewQuestion;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.service.RecommendationEngine;
import com.mockinterview.scoring.dto.ScoreBreakdown;
import com.mockinterview.scoring.service.ScoringEngine;
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
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final ScoringEngine scoringEngine;
    private final RecommendationEngine recommendationEngine;

    public SubmissionService(SubmissionRepository submissionRepository,
                             TestResultRepository testResultRepository,
                             ExecutionResultRepository executionResultRepository,
                             TestCaseRepository testCaseRepository,
                             SandboxOrchestrator sandboxOrchestrator,
                             ExecutionStatusBroadcaster broadcaster,
                             CodingProblemRepository problemRepository,
                             UserRepository userRepository,
                             InterviewRepository interviewRepository,
                             InterviewQuestionRepository interviewQuestionRepository,
                             @Lazy ScoringEngine scoringEngine,
                             RecommendationEngine recommendationEngine) {
        this.submissionRepository = submissionRepository;
        this.testResultRepository = testResultRepository;
        this.executionResultRepository = executionResultRepository;
        this.testCaseRepository = testCaseRepository;
        this.sandboxOrchestrator = sandboxOrchestrator;
        this.broadcaster = broadcaster;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.scoringEngine = scoringEngine;
        this.recommendationEngine = recommendationEngine;
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

        Interview interview = null;
        InterviewQuestion interviewQuestion = null;
        if (request.getInterviewId() != null) {
            interview = interviewRepository.findById(request.getInterviewId())
                    .filter(candidate -> candidate.getUser().getId().equals(userId)
                            && candidate.getStatus() == Interview.InterviewStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Active interview not found"));
            Interview linkedInterview = interview;
            interviewQuestion = interviewQuestionRepository
                    .findByInterview_IdAndCodingProblem_Id(linkedInterview.getId(), problem.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Problem is not assigned to this interview"));
        }

        Submission submission = new Submission();
        submission.setUser(userRepository.getReferenceById(userId));
        submission.setProblem(problem);
        if (request.getInterviewId() != null) {
            submission.setInterview(interview);
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
        boolean systemError = false;
        boolean runtimeError = false;
        boolean timedOut = false;

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            
            ExecutionRequest execReq = new ExecutionRequest();
            execReq.setLanguage(request.getLanguage());
            execReq.setSourceCode(request.getSourceCode());
            execReq.setRunnerCode(runnerCodeFor(problem.getSlug()));
            execReq.setStdin(tc.getInput());
            execReq.setTimeoutSeconds(10);
            execReq.setMemoryBytes(256 * 1024 * 1024L);

            ExecutionResult result = sandboxOrchestrator.executeCode(execReq);
            lastResult = result;

            if (result.getSystemError() != null && !result.getSystemError().isBlank()) {
                systemError = true;
                TestResult tr = new TestResult();
                tr.setSubmission(savedSubmission);
                tr.setTestCase(tc);
                tr.setPassed(false);
                tr.setActualOutput("");
                tr.setErrorMessage(result.getSystemError());
                tr.setExecutionTimeMs(result.getExecutionTimeMs());
                testResultRepository.save(tr);
                testResultDtos.add(new TestResultDto(
                        tc.getId(), false, "", result.getExecutionTimeMs(), result.getSystemError(), i + 1
                ));
                break;
            }

            if (result.getCompilationError() != null && !result.getCompilationError().isEmpty()) {
                compilationError = true;
                TestResult tr = new TestResult();
                tr.setSubmission(savedSubmission);
                tr.setTestCase(tc);
                tr.setPassed(false);
                tr.setActualOutput("");
                tr.setErrorMessage(result.getCompilationError());
                tr.setExecutionTimeMs(result.getExecutionTimeMs());
                testResultRepository.save(tr);
                testResultDtos.add(new TestResultDto(
                        tc.getId(),
                        false,
                        "",
                        result.getExecutionTimeMs(),
                        result.getCompilationError(),
                        i + 1
                ));
                break;
            }
            if (result.getExitCode() != null && result.getExitCode() != 0 && !result.isTimedOut()) {
                runtimeError = true;
            }
            timedOut |= result.isTimedOut();

            TestResult tr = new TestResult();
            tr.setSubmission(savedSubmission);
            tr.setTestCase(tc);
            
            String actual = result.getStdout() != null ? result.getStdout().trim() : "";
            String expected = tc.getExpectedOutput() != null ? tc.getExpectedOutput().trim() : "";
            boolean isPassed = normalizedOutput(actual).equals(normalizedOutput(expected))
                    && result.getExitCode() != null && result.getExitCode() == 0;
            
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
            if (timedOut) {
                break;
            }
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

        if (systemError) {
            savedSubmission.setStatus(Submission.SubmissionStatus.SYSTEM_ERROR);
        } else if (compilationError) {
            savedSubmission.setStatus(Submission.SubmissionStatus.COMPILATION_ERROR);
        } else if (timedOut) {
            savedSubmission.setStatus(Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED);
        } else if (runtimeError) {
            savedSubmission.setStatus(Submission.SubmissionStatus.RUNTIME_ERROR);
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

        if (isSubmit && savedSubmission.getStatus() == Submission.SubmissionStatus.ACCEPTED
                && interviewQuestion != null && interviewQuestion.getCompletedAt() == null) {
            interviewQuestion.setCompletedAt(java.time.LocalDateTime.now());
            interviewQuestionRepository.save(interviewQuestion);
            interview.setQuestionsCompleted(interview.getQuestionsCompleted() + 1);
            interviewRepository.save(interview);
        }

        broadcaster.broadcast(subIdStr, new ExecutionStatusMessage(subIdStr, "COMPLETED", "Execution completed", 100));

        if (isSubmit && savedSubmission.getStatus() != Submission.SubmissionStatus.SYSTEM_ERROR) {
            String difficulty = problem.getDifficulty().name();
            List<TestResult> savedTestResults = testResultRepository.findBySubmissionId(savedSubmission.getId());
            ScoreBreakdown sb = scoringEngine.scoreSubmission(savedSubmission, savedTestResults, exeRes, difficulty);

            if (savedSubmission.getInterview() != null) {
                recommendationEngine.generateRecommendations(
                        savedSubmission.getUser().getId(),
                        savedSubmission.getInterview().getId(),
                        sb,
                        null
                );
            } else {
                recommendationEngine.generateRecommendations(
                        savedSubmission.getUser().getId(),
                        null,
                        sb,
                        null
                );
            }
        }

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

    private String normalizedOutput(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r\n", "\n").replace("\r", "\n")
                .trim()
                .replaceAll("\\s+", " ");
    }

    String runnerCodeFor(String slug) {
        return switch (slug) {
            case "two-sum" -> """
                    class Main {
                        public static void main(String[] args) throws Exception {
                            String input = new String(System.in.readAllBytes()).trim();
                            int arrayStart = input.indexOf('[');
                            int arrayEnd = input.indexOf(']', arrayStart);
                            if (arrayStart < 0 || arrayEnd < 0) throw new IllegalArgumentException("Expected an array in bracket notation.");
                            String arrayLine = input.substring(arrayStart + 1, arrayEnd).trim();
                            String targetText = input.substring(arrayEnd + 1).trim();
                            if (targetText.isEmpty()) throw new IllegalArgumentException("Expected a target after the array.");
                            int target = Integer.parseInt(targetText.split("\\\\s+")[0]);
                            int[] nums = java.util.Arrays.stream(arrayLine.split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                            int[] result = new Solution().twoSum(nums, target);
                            System.out.println("[" + result[0] + "," + result[1] + "]");
                        }
                    }
                    """;
            case "reverse-linked-list" -> """
                    class Main {
                        public static void main(String[] args) throws Exception {
                            String input = new java.io.BufferedReader(new java.io.InputStreamReader(System.in)).readLine().trim();
                            System.out.println(new Solution().reverseList(input));
                        }
                    }
                    """;
            case "largest-fixed-size-window" -> """
                    class Main {
                        public static void main(String[] args) {
                            java.util.Scanner scanner = new java.util.Scanner(System.in);
                            int[] values = parseArray(scanner.nextLine());
                            int k = Integer.parseInt(scanner.nextLine().trim());
                            System.out.println(new Solution().largestWindowSum(values, k));
                        }
                        private static int[] parseArray(String line) {
                            String values = line.replace("[", "").replace("]", "").trim();
                            if (values.isEmpty()) return new int[0];
                            return java.util.Arrays.stream(values.split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                        }
                    }
                    """;
            case "merge-ordered-sequences" -> """
                    class Main {
                        public static void main(String[] args) {
                            java.util.Scanner scanner = new java.util.Scanner(System.in);
                            int[] first = parseArray(scanner.nextLine());
                            int[] second = parseArray(scanner.nextLine());
                            System.out.println(java.util.Arrays.toString(new Solution().mergeOrdered(first, second)).replace(" ", ""));
                        }
                        private static int[] parseArray(String line) {
                            String values = line.replace("[", "").replace("]", "").trim();
                            if (values.isEmpty()) return new int[0];
                            return java.util.Arrays.stream(values.split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                        }
                    }
                    """;
            case "circular-right-shift" -> """
                    class Main {
                        public static void main(String[] args) {
                            java.util.Scanner scanner = new java.util.Scanner(System.in);
                            int[] values = parseArray(scanner.nextLine());
                            int k = Integer.parseInt(scanner.nextLine().trim());
                            System.out.println(java.util.Arrays.toString(new Solution().shiftRight(values, k)).replace(" ", ""));
                        }
                        private static int[] parseArray(String line) {
                            String values = line.replace("[", "").replace("]", "").trim();
                            if (values.isEmpty()) return new int[0];
                            return java.util.Arrays.stream(values.split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                        }
                    }
                    """;
            case "first-unique-signal" -> """
                    class Main {
                        public static void main(String[] args) {
                            String value = new java.util.Scanner(System.in).nextLine();
                            System.out.println(new Solution().firstUniqueIndex(value));
                        }
                    }
                    """;
            case "longest-uphill-run" -> """
                    class Main {
                        public static void main(String[] args) {
                            String line = new java.util.Scanner(System.in).nextLine();
                            String rawValues = line.replace("[", "").replace("]", "").trim();
                            int[] values = rawValues.isEmpty() ? new int[0] : java.util.Arrays.stream(rawValues.split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                            System.out.println(new Solution().longestUphillRun(values));
                        }
                    }
                    """;
            case "balanced-bracket-sequence" -> """
                    class Main {
                        public static void main(String[] args) {
                            String value = new java.util.Scanner(System.in).nextLine();
                            System.out.println(new Solution().isBalanced(value));
                        }
                    }
                    """;
            case "best-forward-gain" -> """
                    class Main {
                        public static void main(String[] args) {
                            String line = new java.util.Scanner(System.in).nextLine();
                            int[] values = java.util.Arrays.stream(line.replace("[", "").replace("]", "").split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                            System.out.println(new Solution().bestForwardGain(values));
                        }
                    }
                    """;
            case "count-exact-tokens" -> """
                    class Main {
                        public static void main(String[] args) {
                            java.util.Scanner scanner = new java.util.Scanner(System.in);
                            String text = scanner.nextLine();
                            String token = scanner.nextLine();
                            System.out.println(new Solution().countExactTokens(text, token));
                        }
                    }
                    """;
            case "grid-diagonal-gap" -> """
                    class Main {
                        public static void main(String[] args) {
                            java.util.Scanner scanner = new java.util.Scanner(System.in);
                            int n = Integer.parseInt(scanner.nextLine().trim());
                            int[][] grid = new int[n][n];
                            for (int row = 0; row < n; row++) {
                                String line = scanner.nextLine();
                                grid[row] = java.util.Arrays.stream(line.split(","))
                                        .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                            }
                            System.out.println(new Solution().diagonalGap(grid));
                        }
                    }
                    """;
            case "collapse-neighbor-repeats" -> """
                    class Main {
                        public static void main(String[] args) {
                            String line = new java.util.Scanner(System.in).nextLine();
                            int[] values = java.util.Arrays.stream(line.replace("[", "").replace("]", "").split(","))
                                    .mapToInt(value -> Integer.parseInt(value.trim())).toArray();
                            System.out.println(java.util.Arrays.toString(new Solution().collapseNeighbors(values)).replace(" ", ""));
                        }
                    }
                    """;
            default -> "";
        };
    }
    
    public SubmissionResultDto getSubmission(Long id, Long userId) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        if (!submission.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Submission not found");
        }
        
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
