package will.peterson.topwords;

import will.peterson.topwords.config.ArgsChecker;
import will.peterson.topwords.counter.GrepWordCounterImpl;
import will.peterson.topwords.counter.HashMapWordCounterImpl;
import will.peterson.topwords.counter.WordCountExecutor;
import will.peterson.topwords.counter.WordCounter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Main class of the top words program, does not use spring framework
 */
public class MainNoSpring {

    public static void main(String[] args) {

        var parsedArgs = new ArgsChecker(args);
        var activeProfile = readSpringActiveProfile();

        // start
        long startTime = System.currentTimeMillis();
        WordCounter wordCounter = null;
        if (activeProfile.contains("grep"))
            wordCounter = new GrepWordCounterImpl();
        else if (activeProfile.contains("hash"))
            wordCounter = new HashMapWordCounterImpl();
        else {
            System.out.println("Profile " + activeProfile + " is not allowed");
            return;
        }
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(parsedArgs.getPaths());

        // done
        long endTime = System.currentTimeMillis();
        long elapsedTimeMillis = (endTime - startTime);

        // Print top N words
        executor.printTopN(parsedArgs.getN());
        System.out.println("(Elapsed Time: " + elapsedTimeMillis + " ms)");
    }

    public static String readSpringActiveProfile() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            // Read YAML and directly get the value associated with the key
            var stream = MainNoSpring.class.getResourceAsStream("/application.yaml");
            Map<String, Object> yamlMap = objectMapper.readValue(stream, Map.class);
            Map<String, Object> spring = (Map<String, Object>) yamlMap.get("spring");
            if (spring != null) {
                Map<String, Object> profiles = (Map<String, Object>) spring.get("profiles");
                if (profiles != null) {
                    return profiles.get("active").toString();
                }
            }
        } catch (IOException e) {
        }
        System.out.println("Could not get active profile, defaulting to hash");
        return "hash";
    }

}