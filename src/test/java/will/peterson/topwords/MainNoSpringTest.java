package will.peterson.topwords;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainNoSpringTest {

     @Test
     void mainTest__sampleFile() {
         var expected = List.of("a occurred 3 times", "hello occurred 3 times", "world occurred 2 times", "you occurred 2 times");
         var notExpected = List.of("not allowed", "are allowed");
         assertCapturedOutputContains(expected.size(), "src/main/resources/test-files-2/sample.txt", expected, notExpected);
     }

    @Test
    void mainTest__sampleFile_ZeroResults() {
         var notExpected = List.of("occurred");
        assertCapturedOutputContains(0, "src/main/resources/test-files-2/sample.txt", null, notExpected);
    }

    public void assertCapturedOutputContains(int N, String files, List<String> expected, List<String> notExpected) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        String[] args = {String.valueOf(N), files};
        MainNoSpring.main(args); // call the main method, like we would from command line

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

    // 'tests' below used to run code with different parameters

    @Test
    void mainTest__folder() {
        String[] args = {"20", "src/main/resources/test-files/letters"};
        MainNoSpring.main(args);
    }

    @Test
    void mainTest__folder_anotherFolder() {
        String[] args = {"2", "src/main/resources/test-files/poems/free/alliterative"};
        MainNoSpring.main(args);
    }

    @Test
    void mainTest_folder_allFiles() {
        String[] args = {"10", "src/main/resources"};
        MainNoSpring.main(args);
    }

    @Test
    void mainTest__kjv() {
        String[] args = {"10", "src/main/resources/test-files-2/kjv.txt"};
        MainNoSpring.main(args);
    }

    @Test
    void mainTest__kjv_10x() {
        String[] args = {"10", "src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt src/main/resources/test-files-2/kjv.txt"};
        MainNoSpring.main(args);
    }

    @Test
    void mainTest__two_files() {
        String[] args = {"10", "src/main/resources/test-files-2/known-count-555/hello5.txt src/main/resources/test-files-2/known-count-555/hello50.txt"};
        MainNoSpring.main(args);
    }

}