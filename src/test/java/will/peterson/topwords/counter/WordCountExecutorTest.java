package will.peterson.topwords.counter;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WordCountExecutorTest {

    @Test
    void fetchSortedWordsTest__sampleFile() {
        var N = 4;
        var fileList = "src/main/resources/test-files-2/sample.txt";

        // get from TopWord
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(fileList);
        executor.shutdown();
        var fromTopWord = executor.fetchSortedWords(wordCounter.getWordCount());

        // test
        var entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("a") && entry.getValue() == 3, "Expected count");
        entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("hello") && entry.getValue() == 3, "Expected count");
        entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("world") && entry.getValue() == 2, "Expected count");
        entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("you") && entry.getValue() == 2, "Expected count");
    }

    @Test
    void fetchSortedWordsTest__sameWords() {
        var N = 2;
        var fileList = "src/main/resources/test-files-2/same-words.txt";

        // get from TopWord
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(fileList);
        executor.shutdown();
        var fromTopWord = executor.fetchSortedWords(wordCounter.getWordCount());

        // test
        var entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("bank") && entry.getValue() == 4, "Expected count");
    }

    @Test
    void fetchSortedWordsTest__differentWords() {
        var N = 2;
        var fileList = "src/main/resources/test-files-2/different-words.txt";

        // get from TopWord
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(fileList);
        executor.shutdown();
        var fromTopWord = executor.fetchSortedWords(wordCounter.getWordCount());

        // test
        var entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("redesign") && entry.getValue() == 2, "Expected count");
        entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("design") && entry.getValue() == 1, "Expected count");
        entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("re") && entry.getValue() == 1, "Expected count");
    }

    @Test
    void fetchSortedWordsTest__knownCount_recursive_concurrent() {
        var N = 1;
        var fileList = "src/main/resources/test-files-2/known-count-555";

        // get from TopWord
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(fileList);
        executor.shutdown();
        var fromTopWord = executor.fetchSortedWords(wordCounter.getWordCount());

        // test
        var entry = fromTopWord.poll();
        assertTrue(entry.getKey().equals("hello") && entry.getValue() == 555, "Expected count");
    }

    @Test
    void fetchSortedWordsTest__letters1_grepCompare() {
        doFetchSortedWordsTestWithGrep(100, "src/main/resources/test-files/letters/1.txt");
    }

    @Test
    void fetchSortedWordsTest__r31_grepCompare() {
        doFetchSortedWordsTestWithGrep(100, "src/main/resources/test-files/random/r1.txt");
    }

    @Test
    void fetchSortedWordsTest__r2_grepCompare() {
        doFetchSortedWordsTestWithGrep(100, "src/main/resources/test-files/random/r2.txt");
    }

    @Test
    void fetchSortedWordsTest__r3_grepCompare() {
        doFetchSortedWordsTestWithGrep(100, "src/main/resources/test-files/random/r3.txt");
    }

    @Test
    void fetchSortedWordsTest__asv_grepCompare() {
        doFetchSortedWordsTestWithGrep(5, "src/main/resources/test-files-2/asv.txt");
    }

    @Test
    void fetchSortedWordsTest__kjv_grepCompare() {
        doFetchSortedWordsTestWithGrep(5, "src/main/resources/test-files-2/kjv.txt");
    }

    /***
     * Helper function for comparing file counts with a single file
     * @param N
     * @param dataFile
     */
    private void doFetchSortedWordsTestWithGrep(int N, String dataFile) {
        // get from TopWord
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(dataFile);
        executor.shutdown();
        var fromTopWord = executor.fetchSortedWords(wordCounter.getWordCount());

        // get from grep
        var fromGrep = executeGrepAndGetWordCounts(dataFile);

        // assert
        for (int i = 0; i < N; i++) {
            var entry = fromTopWord.poll();
            if (entry == null)
                break;
            var expected = fromGrep.get(entry.getKey());
            var msg = entry.getKey() + " occurred " + entry.getValue() + " times, but expected " + expected;
            assertEquals(entry.getValue(), expected, msg);
            System.out.println(msg.substring(0,msg.indexOf(", ")));
        }
    }

    /***
     * executeGrepAndGetWordCounts: Check the count using grep for testing
     *  - Limited platform uses
     *  - Limited scale
     *  - For testing only
     *
     * @param filePath The file to get words from
     * @return Map<String, Integer> Map of top words
     */
    private static Map<String, Integer> executeGrepAndGetWordCounts(String filePath) {
        Map<String, Integer> wordCountMap = new HashMap<>();

        // Execute the command
        try {
            Process process = new ProcessBuilder()
                    .command("bash", "-c", "sed 's/[^a-zA-Z ]//g' " + filePath + " | grep -oE '\\b[a-zA-Z]+\\b' | tr '[:upper:]' '[:lower:]' | sort | uniq -c | sort -nr")  // | head -n 1
                    .redirectErrorStream(true)
                    .start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Process the output line
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    String word = parts[1];
                    int count = Integer.parseInt(parts[0]);
                    wordCountMap.put(word, count);
                }
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return wordCountMap;
    }

}