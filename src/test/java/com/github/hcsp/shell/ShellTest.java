package com.github.hcsp.shell;

import com.github.hcsp.test.helper.ExecResult;
import com.github.hcsp.test.helper.ProcessRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShellTest {
    @Test
    public void testLs() {
        String output =
                runIn("practise-ls", "bash", "solution.sh").assertZeroExitCode().getOutput();
        Assertions.assertTrue(output.contains("file.txt"));
        Assertions.assertTrue(output.contains(".hidden"));
    }

    @Test
    public void testMv() {
        runIn("practise-mv", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-mv", "src", "D.txt");
        assertDirectoryHas("practise-mv/src/main", "A.java", "B.java", "C.java");
    }

    @Test
    public void testRename() {
        runIn("practise-rename", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-rename", "src");
        assertDirectoryHas("practise-rename/src", "test");
    }

    @Test
    public void testRemove() {
        runIn("practise-remove", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-remove", "other.txt");
    }

    @Test
    public void testRemoveAll() {
        runIn("practise-remove-all", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-remove-all");
    }

    @Test
    public void testCp() {
        runIn("practise-cp", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-cp/src", "A.java", "B.java", "C.txt");
        assertDirectoryHas("practise-cp/dest", "A.java", "B.java");
    }

    @Test
    public void testCpAll() {
        runIn("practise-cp-all", "bash", "solution.sh").assertZeroExitCode();

        assertDirectoryHas("practise-cp-all/src", "main", "test");
        assertDirectoryHas("practise-cp-all/dest", "main", "test");
    }

    @Test
    public void testAppend() throws IOException {
        runIn("practise-append", "bash", "solution.sh").assertZeroExitCode();

        String fileContent =
                new String(
                        Files.readAllBytes(
                                getTestDir("practise-append").toPath().resolve("output.txt")));
        System.out.println(fileContent);
        Assertions.assertTrue(fileContent.matches("(?s).*A\\s+B\\s+(\\d+-?){3} (\\d+:?){3}.*"));
    }

    private void assertDirectoryHas(String dir, String... fileName) {
        File[] files = getTestDir(dir).listFiles();
        Assertions.assertEquals(
                Stream.of(fileName).collect(Collectors.toSet()),
                Stream.of(files)
                        .map(File::getName)
                        .filter(this::included)
                        .collect(Collectors.toSet()));
    }

    private boolean included(String fileName) {
        return !"solution.sh".equals(fileName) && !".placeholder".equals(fileName);
    }

    private ExecResult runIn(String subDir, String... commands) {
        return ProcessRunner.exec(getTestDir(subDir), commands);
    }

    private File getTestDir(String subDir) {
        return Paths.get(System.getProperty("user.dir")).resolve(subDir).toFile();
    }
}
