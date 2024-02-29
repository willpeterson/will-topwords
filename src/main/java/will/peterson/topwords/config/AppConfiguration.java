package will.peterson.topwords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import will.peterson.topwords.counter.*;

import javax.naming.ConfigurationException;

@Configuration
public class AppConfiguration {

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Value("${spring.datasource.url:}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:}")
    private String springDatasourceUsername;

    @Value("${spring.datasource.password:}")
    private String springDatasourcePassword;

    @Bean
    public WordCounter wordCounter() throws ConfigurationException {

        if (springProfilesActive.toLowerCase().contains("hash")) {
            return new HashMapWordCounterImpl();
        } else if (springProfilesActive.toLowerCase().contains("sql")) {
            return new SqlWordCounterImpl(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword);
        } else if (springProfilesActive.toLowerCase().contains("grep")) {
            return new GrepWordCounterImpl();
        } else {
            throw new ConfigurationException("Unknown counter profile " + springProfilesActive);
        }
    }

    @Bean
    public WordCountExecutor wordCountExecutor(WordCounter wordCounter) {
        return new WordCountExecutor(wordCounter);
    }
}

