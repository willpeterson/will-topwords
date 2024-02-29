package will.peterson.topwords;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainNoSpringTest {

     @Test
     void mainTest__sampleFile() {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         PrintStream originalOut = System.out;
         System.setOut(new PrintStream(outputStream));

         String[] args = {"4", "src/main/resources/test-files-2/sample.txt"};
         MainNoSpring.main(args); // call the main method, like we would from command line

         System.setOut(originalOut);
         String capturedOutput = outputStream.toString();
         System.out.println(capturedOutput);
         assertFalse(capturedOutput.contains("not allowed"), "Profile is not allowed for this test");
         assertTrue(capturedOutput.contains("a occurred 3 times"), "Word count expected");
         assertTrue(capturedOutput.contains("hello occurred 3 times"), "Word count expected");
         assertTrue(capturedOutput.contains("world occurred 2 times"), "Word count expected");
         assertTrue(capturedOutput.contains("you occurred 2 times"), "Word count expected");
         assertTrue(capturedOutput.indexOf("a occurred") < capturedOutput.indexOf("hello occurred"), "Sorting expected");
         assertTrue(capturedOutput.indexOf("world occurred") < capturedOutput.indexOf("you occurred"), "Sorting expected");
         assertFalse(capturedOutput.contains("are occurred"), "Should not contain extra results");
     }

    @Test
    void mainTest__sampleFile_ZeroResults() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        String[] args = {"0", "src/main/resources/test-files-2/sample.txt"};
        MainNoSpring.main(args); // call the main method, like we would from command line

        System.setOut(originalOut);
        String capturedOutput = outputStream.toString();
        System.out.println(capturedOutput);
        assertFalse(capturedOutput.contains("not allowed"), "Profile is not allowed for this test");
        assertFalse(capturedOutput.contains("are occurred"), "Should not contain extra results");
    }

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