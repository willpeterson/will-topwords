package will.peterson.topwords.counter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the WordCounter interface using a ConcurrentHashMap to store word counts.
 */
@Component
public class HashMapWordCounterImpl implements WordCounter {
    private final Map<String, Integer> wordCount = new ConcurrentHashMap<>(); //Collections.synchronizedMap(new HashMap<>());

    public HashMapWordCounterImpl() {
        // Constructor logic
    }

    @Override
    public void countWords(Path file) {
        System.out.println("...counting words for file " + file.getFileName());
        try {
            Files.lines(file)
                    .forEach(line -> processLine(line));
        } catch (IOException e) {
            System.out.println("Error reading file: " + file + "; error is " + e.getMessage());
        }
    }

    private void processLine(String line) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!word.isEmpty()) {
                wordCount.merge(word, 1, Integer::sum);
            }
        }
    }

    @Override
    public Map<String, Integer> getWordCount() {
        return wordCount;
    }

}
