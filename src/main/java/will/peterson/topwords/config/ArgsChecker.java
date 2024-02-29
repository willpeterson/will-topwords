package will.peterson.topwords.config;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Getter
public class ArgsChecker {

    private final int n;
    private final Path[] paths;

    public ArgsChecker(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java topwords <N> <file/directory1> <file/directory2> ...");
            System.out.println("   <N> - Number of top count desired");
            System.out.println("   <file/directory> ... - List of files or directories to look for words");
            System.exit(1);
        }
        var argsList = new ArrayList<>(Arrays.asList(args));
        int n = 0;
        try {
            n = Integer.parseInt(argsList.get(0));
            argsList.remove(0);
        } catch (Exception e) {
            System.out.println("Invalid value for N. Please provide an integer for the first argument.");
            System.exit(1);
        }
        this.n = n;
        this.paths = convertToPathArray(argsList);
    }

    private static Path[] convertToPathArray(List<String> filenames) {
        var paths = new Path[filenames.size()];
        for (int i = 0; i < filenames.size(); i++) {
            paths[i] = Paths.get(filenames.get(i));
        }
        return paths;
    }
}
