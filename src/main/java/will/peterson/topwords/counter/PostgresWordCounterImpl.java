package will.peterson.topwords.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import will.peterson.topwords.entity.WordCount;
import will.peterson.topwords.repo.WordCountRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PostgresWordCounterImpl implements WordCounter {

    @Autowired
    public WordCountRepository wordCountRepository;

    public PostgresWordCounterImpl() {
    }

    @Override
    public void countWords(Path file) {
        clearTable();
        System.out.println("using postgres DB");
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (!word.isEmpty()) {
                        updateWordCount(word);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    private void updateWordCount(String word) {
        WordCount wordCount = wordCountRepository.findById(word).orElse(new WordCount());
        wordCount.setWord(word);
        wordCount.setCount(wordCount.getCount() + 1);
        wordCountRepository.save(wordCount);
    }

    @Override
    public Map<String, Integer> getWordCount() {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        wordCountRepository.findAll().forEach(wc -> wordCount.put(wc.getWord(), wc.getCount()));
        return wordCount;
    }

    private void clearTable() {
        wordCountRepository.deleteAll();
    }
}

