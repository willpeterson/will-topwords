package will.peterson.topwords.counter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * WordCountExecutor used for word counting
 */
public class WordCountExecutor {
    private final WordCounter wordCounter;
    private final ExecutorService executor;

    public WordCountExecutor(WordCounter wordCounter) {
        this.wordCounter = wordCounter;
        this.executor = Executors.newWorkStealingPool();
    }

    public void processPaths(Path... paths) {
        for (Path path : paths) {
            if (Files.isDirectory(path)) {
                processDirectory(path);
            } else {
                executor.submit(() -> wordCounter.countWords(path));
            }
        }
        shutdown();
    }

    private void processDirectory(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.filter(Files::isRegularFile)
                    .forEach(file -> executor.submit(() -> wordCounter.countWords(file)));
        } catch (Exception e) {
            System.out.println("Error processing directory: " + e.getMessage());
        }
    }

    private void shutdown() {
        executor.shutdown();
        try {
            var completed = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (!completed)
                System.out.println("Warning: Timeout elapsed before termination");
        } catch (InterruptedException e) {
            System.out.println("Error: Concurrent processing interrupted.");
        }
    }

    public void printTopN(int n) {
        var wordCount = wordCounter.getWordCount(n);
        printTopN(wordCount);
    }

    private static void printTopN(List<Map.Entry<String, Integer>> wordCounts) {
        if (wordCounts.isEmpty()) {
            System.out.println("Empty Top Word list returned");
        } else {
            for (var word : wordCounts) {
                // ensure format is <word 1> occurred <x> times
                System.out.println(word.getKey() + " occurred " + word.getValue() + " times");
            }
        }
    }

}

