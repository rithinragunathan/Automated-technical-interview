package com.mockinterview.sandbox.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaSandboxServiceTest {

    @TempDir
    Path workspacePath;

    @Test
    void writesSolutionAndRunnerToMatchingJavaSourceFiles() throws IOException {
        String solution = "public class Solution { public int add(int left, int right) { return left + right; } }";
        String runner = "class Main { public static void main(String[] args) { System.out.println(new Solution().add(2, 3)); } }";

        assertTrue(JavaSandboxService.writeJavaSources(workspacePath, solution, runner));

        assertEquals(0, ToolProvider.getSystemJavaCompiler().run(
                null,
                null,
                null,
                "-d",
                workspacePath.toString(),
                workspacePath.resolve("Main.java").toString(),
                workspacePath.resolve("Solution.java").toString()));
    }

    @Test
    void writesSingleSourceCodeAsMainWhenNoRunnerIsProvided() throws IOException {
        String source = "public class Main { public static void main(String[] args) {} }";

        assertFalse(JavaSandboxService.writeJavaSources(workspacePath, source, null));

        assertEquals(0, ToolProvider.getSystemJavaCompiler().run(
                null,
                null,
                null,
                "-d",
                workspacePath.toString(),
                workspacePath.resolve("Main.java").toString()));
    }
}
