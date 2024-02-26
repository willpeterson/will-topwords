package will.peterson.topwords;

import will.peterson.topwords.counter.HashMapWordCounterImpl;
import will.peterson.topwords.counter.WordCountExecutor;
import will.peterson.topwords.counter.WordCounter;

/**
 * Main class of the top words program
 */
public class MainNoSpring {

    public static void main(String[] args) {
        // handle command line arguments
        if (args.length < 2) {
            System.out.println("Usage: java topwords <N> <file/directory1> <file/directory2> ...");
            System.out.println("   <N> - Number of top count desired");
            System.out.println("   <file/directory> ... - List of files or directories to look for words");
            return;
        }
        int N;
        try {
            N = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid value for N. Please provide an integer for the first argument.");
            return;
        }

        // start
        long startTime = System.nanoTime();

        WordCounter wordCounter = new HashMapWordCounterImpl();
        var executor = new WordCountExecutor(wordCounter);
        executor.processPaths(args[1]);
        executor.shutdown();

        // done
        long endTime = System.nanoTime();
        long elapsedTimeMillis = (endTime - startTime) / 1_000_000;

        // Print top N words
        executor.printTopN(N);
        System.out.println("(Elapsed Time: " + elapsedTimeMillis + " milliseconds)");
    }
}