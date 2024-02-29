package will.peterson.topwords;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainNoSpringTest {

    @Test
    void mainTest__sampleFile() {
        var files = new String[]{"src/main/resources/test-files-2/sample.txt"};
        var expected = List.of("a occurred 3 times", "hello occurred 3 times", "world occurred 2 times", "you occurred 2 times");
        var notExpected = List.of("not allowed", "are allowed");
        assertCapturedOutputContains(expected.size(), files, expected, notExpected);
    }

    @Test
    void mainTest__sampleFile_ZeroResults() {
        var files = new String[]{"src/main/resources/test-files-2/sample.txt"};
        var notExpected = List.of("occurred");
        assertCapturedOutputContains(0, files, null, notExpected);
    }

    @Test
    void mainTest__folder() {
        var files = new String[]{"src/main/resources"};
        var expected = List.of("shall occurred 18944 times");
        assertCapturedOutputContains(10, files, expected, null);
    }

    @Test
    void mainTest__kjv() {
        var files = new String[]{"src/main/resources/test-files-2/kjv.txt"};
        var expected = List.of("the occurred 64016 times");
        assertCapturedOutputContains(10, files, expected, null);
    }

    @Test
    void mainTest__kjv_10x() {
        var files = new String[10];
        Arrays.fill(files, "src/main/resources/test-files-2/kjv.txt");
        var expected = List.of("the occurred 640160 times");
        assertCapturedOutputContains(2, files, expected, null);
    }

    public void assertCapturedOutputContains(int N, String[] files, List<String> expected, List<String> notExpected) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        String[] allArgs = new String[1 + files.length];
        allArgs[0] = String.valueOf(N);
        System.arraycopy(files, 0, allArgs, 1, files.length);

        MainNoSpring.main(allArgs); // call the main method, like we would from command line

        System.setOut(originalOut);
        String capturedOutput = outputStream.toString();
        System.out.println(capturedOutput);
        assertFalse(capturedOutput.contains("not allowed"), "Profile is not allowed for this test");
        if (expected != null) {
            for (var val : expected) {
                assertTrue(capturedOutput.contains(val), "Should contain result '" + val + "'");
            }
            if (expected.size() > 2) {
                for (int i = 0; i < expected.size() - 1; i++) {
                    assertTrue(capturedOutput.indexOf(expected.get(i)) < capturedOutput.indexOf(expected.get(i + 1)),
                            "Correct sorting expected");
                }
            }
        }
        if (notExpected != null) {
            for (var val : notExpected) {
                assertFalse(capturedOutput.contains(val), "Should not contain result" + val);
            }
        }
    }
}