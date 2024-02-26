package will.peterson.topwords.counter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
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

    public void processPaths(String paths) {
        var pathArray = convertStringToPathArray(paths);
        processPaths(pathArray);
    }

    public void processPaths(Path... paths) {
        for (Path path : paths) {
            if (Files.isDirectory(path)) {
                processDirectory(path);
            } else {
                executor.submit(() -> wordCounter.countWords(path));
            }
        }
    }

    private void processDirectory(Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.filter(Files::isRegularFile)
                    .forEach(file -> executor.submit(() -> wordCounter.countWords(file)));
        } catch (Exception e) {
            System.out.println("Error processing directory: " + e.getMessage());
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            var completed = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (!completed)
                System.out.println("Warning: Timeout elapsed before termination");
        } catch (InterruptedException e) {
            System.out.println("Error: Concurrent processing interrupted.");
        }
    }

    public static Path[] convertStringToPathArray(String pathVal) {
        var strPaths = pathVal.split(" ");
        var paths = new Path[strPaths.length];
        for (int i = 0; i < strPaths.length; i++) {
            paths[i] = Paths.get(strPaths[i]);
        }
        return paths;
    }

    public void printTopN(int N) {
        Map<String, Integer> wordCount = wordCounter.getWordCount();
        var fetched = fetchSortedWords(wordCount);
        printTopN(fetched, N);
    }

    public PriorityQueue<Map.Entry<String, Integer>> fetchSortedWords(Map<String, Integer> wordCount) {
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                // sort biggest count first, then by key name alphabetically
                Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)));

        pq.addAll(wordCount.entrySet());
        return pq;
    }

    private void printTopN(PriorityQueue<Map.Entry<String, Integer>> pq, int N) {
        if (pq.isEmpty()) {
            System.out.println("Empty Top Word list returned");
        } else {
            for (int i = 0; i < N; i++) {
                Map.Entry<String, Integer> entry = pq.poll();
                // ensure format is <word 1> occurred <x> times
                if (entry != null)
                    System.out.println(entry.getKey() + " occurred " + entry.getValue() + " times");
            }
        }
    }

}

