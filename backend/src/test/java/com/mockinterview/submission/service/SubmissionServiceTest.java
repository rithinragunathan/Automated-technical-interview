package com.mockinterview.submission.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.coding.dto.SubmissionRequest;
import com.mockinterview.coding.dto.SubmissionResultDto;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.coding.entity.TestCase;
import com.mockinterview.coding.repository.CodingProblemRepository;
import com.mockinterview.coding.repository.TestCaseRepository;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.recommendation.service.RecommendationEngine;
import com.mockinterview.sandbox.dto.ExecutionResult;
import com.mockinterview.sandbox.service.SandboxOrchestrator;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.entity.TestResult;
import com.mockinterview.submission.repository.ExecutionResultRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import com.mockinterview.submission.repository.TestResultRepository;
import com.mockinterview.submission.websocket.ExecutionStatusBroadcaster;
import com.mockinterview.scoring.service.ScoringEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubmissionServiceTest {

    @TempDir
    Path workspacePath;

    @Test
    void returnsCompilationDiagnosticsInsteadOfAnEmptyTestResultList() {
        SubmissionRepository submissionRepository = mock(SubmissionRepository.class);
        TestResultRepository testResultRepository = mock(TestResultRepository.class);
        ExecutionResultRepository executionResultRepository = mock(ExecutionResultRepository.class);
        TestCaseRepository testCaseRepository = mock(TestCaseRepository.class);
        SandboxOrchestrator sandboxOrchestrator = mock(SandboxOrchestrator.class);
        ExecutionStatusBroadcaster broadcaster = mock(ExecutionStatusBroadcaster.class);
        CodingProblemRepository problemRepository = mock(CodingProblemRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        InterviewRepository interviewRepository = mock(InterviewRepository.class);
        InterviewQuestionRepository interviewQuestionRepository = mock(InterviewQuestionRepository.class);
        ScoringEngine scoringEngine = mock(ScoringEngine.class);
        RecommendationEngine recommendationEngine = mock(RecommendationEngine.class);

        CodingProblem problem = new CodingProblem();
        problem.setId(3L);
        problem.setSlug("two-sum");
        when(problemRepository.findById(3L)).thenReturn(Optional.of(problem));

        TestCase testCase = new TestCase();
        testCase.setId(8L);
        testCase.setInput("[2,7] 9");
        testCase.setExpectedOutput("[0,1]");
        when(testCaseRepository.findByProblemIdAndHiddenFalse(3L)).thenReturn(List.of(testCase));

        User user = mock(User.class);
        when(userRepository.getReferenceById(5L)).thenReturn(user);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission submission = invocation.getArgument(0);
            submission.setId(42L);
            return submission;
        });

        when(sandboxOrchestrator.executeCode(any())).thenReturn(ExecutionResult.builder()
                .compilationError("cannot find symbol: missing method")
                .executionTimeMs(0L)
                .build());

        SubmissionService service = new SubmissionService(
                submissionRepository,
                testResultRepository,
                executionResultRepository,
                testCaseRepository,
                sandboxOrchestrator,
                broadcaster,
                problemRepository,
                userRepository,
                interviewRepository,
                interviewQuestionRepository,
                scoringEngine,
                recommendationEngine);

        SubmissionRequest request = new SubmissionRequest();
        request.setProblemId(3L);
        request.setLanguage("JAVA");
        request.setSourceCode("class Solution {}");

        SubmissionResultDto result = service.runCode(5L, request);

        assertEquals("COMPILATION_ERROR", result.status());
        assertEquals(1, result.testResults().size());
        assertFalse(result.testResults().get(0).passed());
        assertTrue(result.testResults().get(0).errorMessage().contains("cannot find symbol"));
        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    void providesCompilableJavaRunnersForAllSeededProblems() throws IOException {
        SubmissionService service = new SubmissionService(
                null, null, null, null, null, null, null, null, null, null, null, null);
        Files.writeString(workspacePath.resolve("Solution.java"), """
                class Solution {
                    public int[] twoSum(int[] values, int target) { return values; }
                    public String reverseList(String value) { return value; }
                    public long largestWindowSum(int[] values, int k) { return 0; }
                    public int[] mergeOrdered(int[] first, int[] second) { return first; }
                    public int[] shiftRight(int[] values, int k) { return values; }
                    public int firstUniqueIndex(String value) { return 0; }
                    public int longestUphillRun(int[] values) { return 0; }
                    public boolean isBalanced(String value) { return false; }
                    public int bestForwardGain(int[] values) { return 0; }
                    public int countExactTokens(String text, String token) { return 0; }
                    public int diagonalGap(int[][] grid) { return 0; }
                    public int[] collapseNeighbors(int[] values) { return values; }
                }
                """);

        List<String> slugs = List.of(
                "two-sum",
                "reverse-linked-list",
                "largest-fixed-size-window",
                "merge-ordered-sequences",
                "circular-right-shift",
                "first-unique-signal",
                "longest-uphill-run",
                "balanced-bracket-sequence",
                "best-forward-gain",
                "count-exact-tokens",
                "grid-diagonal-gap",
                "collapse-neighbor-repeats");
        for (String slug : slugs) {
            Path runnerPath = workspacePath.resolve("Main.java");
            Files.writeString(runnerPath, service.runnerCodeFor(slug));
            int exitCode = ToolProvider.getSystemJavaCompiler().run(
                    null, null, null,
                    "-d", workspacePath.toString(),
                    runnerPath.toString(),
                    workspacePath.resolve("Solution.java").toString());
            assertEquals(0, exitCode, "Runner must compile for problem slug " + slug);
        }
    }
}
