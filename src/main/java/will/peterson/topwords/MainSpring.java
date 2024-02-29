package will.peterson.topwords;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import will.peterson.topwords.config.ArgsChecker;
import will.peterson.topwords.counter.WordCountExecutor;

/**
 * Main class of the top words program, uses spring framework
 */
@SpringBootApplication
@ComponentScan("will.peterson.topwords")
public class MainSpring {

	public static void main(String[] args) {
		var parsedArgs = new ArgsChecker(args);
		var context = SpringApplication.run(MainSpring.class, args);

		// Retrieve the WordCountExecutor bean from the Spring context
		WordCountExecutor wordCountExecutor = context.getBean(WordCountExecutor.class);

		// start
		long startTime = System.currentTimeMillis();

		wordCountExecutor.processPaths(parsedArgs.getPaths());

		// done
		long endTime = System.currentTimeMillis();
		long elapsedTimeMillis = (endTime - startTime);

		// Print top N words
		wordCountExecutor.printTopN(parsedArgs.getN());
		System.out.println("(Elapsed Time: " + elapsedTimeMillis + " ms)");

		// Close the Spring context
		context.close();
	}

}
