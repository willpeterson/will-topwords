package will.peterson.topwords.counter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

public class HashMapWordCounterImpl implements WordCounter {
    private final Map<String, Integer> wordCount = new ConcurrentHashMap<>();

    @Override
    public void countWords(Path file) {
        System.out.println("... hash counting words for file " + file.getFileName());
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + file + "; error is " + e.getMessage());
        }
    }

    private void processLine(String line) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!word.isEmpty()) {
                wordCount.merge(word, 1, Integer::sum);
            }
        }
    }

    @Override
    public List<Map.Entry<String, Integer>> getWordCount(int n) {
        var topNEntries = getTopNEntriesSortedByValueDescending(wordCount, n);
        return topNEntries;
    }

    public static TreeMap<String, Integer> getTopNEntries(Map<String, Integer> wordCounts, int N) {
        return wordCounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(N)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
    }

    public static List<Map.Entry<String, Integer>> getTopNEntriesSortedByValueDescending(Map<String, Integer> wordCount, int N) {
        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(N)
                .collect(Collectors.toList());
    }
}
