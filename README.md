# Read Me

## Problem
Write a Java program that reads a collection of files and prints the top N words ordered by their occurrences across all files.
### Input
Reads the following from command line arguments:
- An integer specifying the value of N.
- A list of paths that represent the directories and/or files to traverse. Both directories as well as files can be provided simultaneously.
- A small collection of files exists here which can be used to test the program.
#### Input errors
At minimum, the program must print a descriptive usage upon receiving the wrong number of parameters or badly formatted parameters.
#### Output
The program prints the N words that have the most occurrences in descending occurrence order in the following format:
- <word 1> occurred <x> times
- <word 2> occurred <y> times
#### Deliverable
Please provide the code, with any testing examples you run. The submitted code will be assumed to be a representation of what you consider to be production ready.
### Requirements
#### Functional
- If a specified path is a directory, that directory should be scanned for files, and subdirectories are also scanned recursively.
- Words are separated with whitespace characters. Other characters cannot be considered as delimiters. "re-design" is a single word, not two.
- Punctuation is not included in a word, and word comparison is case insensitive. These words are the same: “Bank!”, “BANK”, “bank”, “baNk”.
- When two words share the same count, they are sub-sorted in alphabetical order, ignoring case.
#### Performance
The program should be able to produce the result quickly, even when faced with many files of varying sizes across many directories.
#### Assumptions
- All files are valid UTF-8 text files, and could be of widely varying sizes.
- Systems upon which this program will run will have a minimum of 8 CPU cores
- All other assumptions made should be documented with your reasoning.

## Solution

### General Notes and Possible Enhancements

- Testing covers the use cases above. Tests also verify that the counts are correct for all the words in a file.  More tests could be added to increase code coverage.
  - The program is provided with multiple directories containing text files. It should recursively traverse each directory and count words across all files.
  - Two versions of the bible text are included
  - All original files are include
  - Unit test use all the sample files
- WordCounter class was built as interface to handle different methods of counting
  - HashMapCounter - Does an in memory version of determining the topwords.  I found that this method was fast and efficient and uses little memory.  I was setup on my home machine, community version of IntelliJ, using some performance tools would be more ideal I used times and OS memoery tools to monitor.
    - Took only 373ms to process a bible sized file
    - Running 10x the similar files at the same time only takes about three as long and memory impact was not noticed
  - PostgresCounter - After doing the in memory version, was hesitant to implement this at all but wanted to compare.  Did this in part because I discussed it on the call.  Was much slower and requires an external Postgres database for this test
    - Took 266,368ms (4.4 min) to process a bible sized file, note this was using docker and 10 cores but I did not optimize. If multiple nodes were used and a lot more data this could help.  I might be better to onlyu update once per file instead of on every word.
    - The sorting does not occur in the DB but could, however the loading alone seems to be a huge bottleneck right now and should be addressed first (if at all)
- Error handling could provide more detailed errors and messages when errors occur

#### A Note on testing
- Some testing compares files using grep, you can count words in single files using commands like (checking the file 'kjv.txt' in this example) to get data and compare:
  > sed 's/[^a-zA-Z ]//g' kjv.txt | grep -oE '\\b[a-zA-Z]+\\b' | tr '[:upper:]' '[:lower:]' | sort | uniq -c | sort -nr

#### Assumption: Word Counting Method
- Reasoning: The solution assumes that hyphenated words, such as "re-design", should be counted as a single word rather than separate words. I'm assuming that words like design and re-design are actually counted the same
- Justification: While the instructions point this case out as a single word, they do not state how hyphenated words should be counted, treating them as single words simplifies the counting process and aligns with common text processing conventions.

## Setup notes:

- Step 1: Install Java Development Kit (JDK) version 17 or later. You can download JDK from the official Oracle website or use a package manager like Homebrew on macOS or apt-get on Ubuntu.
- Step 2: Install Apache Maven by downloading the binary archive from the Apache Maven website and extracting it to a desired location.
- Step 3: Set up environment variables JAVA_HOME and MAVEN_HOME to point to the installation directories of JDK and Maven, respectively.
- Step 4: PostgresSQL (optional)
  - To use postgres, you need to enable and update DB configuration in the application.properties
    - by default useDatabase flag is false (It will use the in memory solution otherwise)
  - Make sure postgres is running at the location specified in application.properties

### To run

- Several easy ways to run:
    - Run the main class using maven and specify arguments
      - To run with spring:
        > mvn install exec:java -Dexec.mainClass="will.peterson.topwords.MainSpring" -Dexec.args="3 src/main/resources/test-files-2/sample.txt" -DskipTests
      - To run without spring:
        > mvn install exec:java -Dexec.mainClass="will.peterson.topwords.MainNoSpring" -Dexec.args="3 src/main/resources/test-files-2/sample.txt" -DskipTests
      - Or, run unit tests under test class 'MainNoSpring' class
        - Some tests don't have asserts, and point to local files to simulate running the program
        - Some tests include asserts to test the output
      - Command is:
        > mvn test -Dtest=will.peterson.topwords.MainNoSpringTest
    - Run unit tests under test class 'WordCountExecutorTest'
      - Command is (also shown with specific test specified):
        > mvn test -Dtest=will.peterson.topwords.counter.WordCountExecutorTest
        > 
        > mvn test -Dtest=will.peterson.topwords.counter.WordCountExecutorTest#fetchSortedWordsTest__kjv_grepCompare

  #### Sample output
        > mvn install exec:java -Dexec.mainClass="will.peterson.topwords.MainNoSpring" -Dexec.args="3 src/main/resources/test-files-2/kjv.txt" -DskipTests
        >
        > ...counting words for file kjv.txt
        > 
        > the occurred 64016 times
        >
        > and occurred 51709 times
        > 
        > of occurred 34865 times
        > 
        > (Elapsed Time: 508 milliseconds)
