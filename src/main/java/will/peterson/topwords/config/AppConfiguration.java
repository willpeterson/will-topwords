package will.peterson.topwords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import will.peterson.topwords.counter.HashMapWordCounterImpl;
import will.peterson.topwords.counter.PostgresWordCounterImpl;
import will.peterson.topwords.counter.WordCountExecutor;
import will.peterson.topwords.counter.WordCounter;
import will.peterson.topwords.repo.WordCountRepository;

@Configuration
public class AppConfiguration {

    @Value("${useDatabase}")
    private boolean useDatabase;

    @Bean
    public WordCounter wordCounter() {

        if (useDatabase) {
            return new PostgresWordCounterImpl();
        } else {
            return new HashMapWordCounterImpl();
        }
    }

    @Bean
    public WordCountExecutor wordCountExecutor(WordCounter wordCounter) {
        return new WordCountExecutor(wordCounter);
    }
}

