package will.peterson.topwords;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import will.peterson.topwords.counter.WordCountExecutor;

@SpringBootApplication
@ComponentScan("will.peterson.topwords")
public class MainSpring {

	public static void main(String[] args) {
		var context = SpringApplication.run(MainSpring.class, args);

		int N = validateArgs(args);
		if (N < 0) {
			context.close(); // Close the context if validation fails
			System.exit(1); // Exit the application
		}

		// Retrieve the WordCountExecutor bean from the Spring context
		WordCountExecutor wordCountExecutor = context.getBean(WordCountExecutor.class);

		// start
		long startTime = System.nanoTime();

		wordCountExecutor.processPaths(args[1]);
		wordCountExecutor.shutdown();

		// done
		long endTime = System.nanoTime();
		long elapsedTimeMillis = (endTime - startTime) / 1_000_000;

		// Print top N words
		wordCountExecutor.printTopN(N);
		System.out.println("(Elapsed Time: " + elapsedTimeMillis + " milliseconds)");

		// Close the Spring context
		context.close();
	}

	private static int validateArgs(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java topwords <N> <file/directory1> <file/directory2> ...");
			System.out.println("   <N> - Number of top count desired");
			System.out.println("   <file/directory> ... - List of files or directories to look for words");
			return -1;
		}
		int N;
		try {
			N = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("Invalid value for N. Please provide an integer for the first argument.");
			return -1;
		}
		return N;
	}

}
