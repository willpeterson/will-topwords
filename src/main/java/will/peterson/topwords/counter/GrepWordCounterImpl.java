package will.peterson.topwords.counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GrepWordCounterImpl implements WordCounter{

    private final Map<String, Integer> wordCount = new ConcurrentHashMap<>();

    public GrepWordCounterImpl() {
    }

    @Override
    public void countWords(Path file) {
        System.out.println("... grep counting words for file " + file.getFileName());
        var result = executeGrepAndGetWordCounts(file);
        for (var word : result.entrySet()) {
                wordCount.merge(word.getKey(), word.getValue(), Integer::sum);
        }
    }

    @Override
    public List<Map.Entry<String, Integer>> getWordCount(int n) {
        return HashMapWordCounterImpl.getTopNEntriesSortedByValueDescending(wordCount, n);
    }

    private Map<String, Integer> executeGrepAndGetWordCounts(Path file) {
        Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();

        // Execute the command
        try {
            Process process = new ProcessBuilder()
                    .command("bash", "-c", "sed 's/[^a-zA-Z0-9 ]//g' " + file + " | grep -oE '\\b[a-zA-Z0-9]+\\b' | tr '[:upper:]' '[:lower:]' | sort | uniq -c | sort -nr")  // | head -n 1
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
