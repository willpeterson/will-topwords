package will.peterson.topwords.counter;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WordCountExecutorTest {

    @Test
    void fetchSortedWordsTest__sampleFile() {
        var N = 4;
        var files = "src/main/resources/test-files-2/sample.txt";
        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("a", 3),
                Map.entry("hello", 3),
                Map.entry("world", 2),
                Map.entry("you", 2)
        );
        assertTopNEntriesMatchMap(expected.size(), files, expected);
    }

    @Test
    void fetchSortedWordsTest__sameWords() {
        var N = 2;
        var files = "src/main/resources/test-files-2/same-words.txt";
        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("bank", 4)
        );
        assertTopNEntriesMatchMap(expected.size(), files, expected);
    }

    @Test
    void fetchSortedWordsTest__differentWords() {
        var N = 2;
        var files = "src/main/resources/test-files-2/different-words.txt";
        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("200", 3),
                Map.entry("redesign", 2)
        );
        assertTopNEntriesMatchMap(expected.size(), files, expected);
    }

    @Test
    void fetchSortedWordsTest__knownCount_recursive_concurrent() {
        var N = 1;
        var files = "src/main/resources/test-files-2/known-count-555";
        var expected = List.of(
                Map.entry("hello", 555)
        );
        assertTopNEntriesMatchMap(expected.size(), files, expected);
    }

    @Test
    void fetchSortedWordsTest__letters1_grepCompare() {
        assertGrepWordCountMatches(25, "src/main/resources/test-files/letters/1.txt");
    }

    @Test
    void fetchSortedWordsTest__r1_grepCompare() {
        assertGrepWordCountMatches(100, "src/main/resources/test-files/random/r1.txt");
    }

    @Test
    void fetchSortedWordsTest__r2_grepCompare() {
        assertGrepWordCountMatches(25, "src/main/resources/test-files/random/r2.txt");
    }

    @Test
    void fetchSortedWordsTest__r3_grepCompare() {
        assertGrepWordCountMatches(100, "src/main/resources/test-files/random/r3.txt");
    }

    @Test
    void fetchSortedWordsTest__asv_grepCompare() {
        assertGrepWordCountMatches(5, "src/main/resources/test-files-2/asv.txt");
    }

    /**
     * Helper function for comparing file counts with a single file
     * @param N
     * @param files
     * @param expected
     */
    public void assertTopNEntriesMatchMap(int N, String files, List<Map.Entry<String, Integer>> expected) {
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(Path.of(files));
        var topNEntries = wordCounter.getWordCount(N);
        assertEquals(topNEntries, expected, "Expected word counts should be the same");
    }

    /***
     * Helper function for comparing file counts with a single file
     * @param N
     * @param filename
     */
    private void assertGrepWordCountMatches(int N, String filename) {
        var wordCounter  = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(Path.of(filename));
        var topNEntries = wordCounter.getWordCount(N);

        var grepWordCounter  = new GrepWordCounterImpl();
        var grepExecutor = new WordCountExecutor(grepWordCounter);
        grepExecutor.processPaths(Path.of(filename));
        var grepTopNEntries = grepWordCounter.getWordCount(N);

        assertEquals(topNEntries, grepTopNEntries, "word counts should be the same as grep counts");
    }

}