package will.peterson.topwords.counter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface WordCounter {

    /**
     * Counts the occurrences of words in the specified file.
     *
     * @param file the path to the file to be processed
     */
    void countWords(Path file);

    /**
     * Gets the normalized word count with the key being the word and the value being the count
     * @param n count to get
     * @return map of counts
     */
    List<Map.Entry<String, Integer>> getWordCount(int n);
}

