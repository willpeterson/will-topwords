package will.peterson.topwords.counter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the WordCounter interface using a Postgres DB
 *
 *  Make sure postgres is running and has the specific DB, wth the schema below:
 *     > psql -h localhost -U postgres -d postgres
 *     >
 *     > CREATE DATABASE topwords;
 *     >
 *     > \c topwords;
 *     >
 *     > CREATE TABLE word_count (
 *     >
 *     > word VARCHAR(255) PRIMARY KEY,
 *     >
 *     > count INTEGER
 *     >
 *     > );
 *     >
 *     > SELECT * FROM word_count; // to view
 *
 */
public class SqlWordCounterImpl implements WordCounter {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public SqlWordCounterImpl(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        clearTable();
    }

    @Override
    public void countWords(Path file) {
        System.out.println("... using SQL DB");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int wordCount = 0;
                    word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    if (!word.isEmpty()) {
                        wordCount++;
                    }
                    updateWordCount(connection, word, wordCount);
                }
            }
        } catch (IOException | SQLException e) {
            System.out.println("Error processing file or database: " + e.getMessage());
        }
    }

    private void updateWordCount(Connection connection, String word, int wordCount) throws SQLException {
        String query = "INSERT INTO word_count (word, count) VALUES (?, ?) ON CONFLICT (word) DO UPDATE SET count = word_count.count + ?" ;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, word);
            statement.setInt(2, wordCount);
            statement.setInt(3, wordCount);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Map.Entry<String, Integer>> getWordCount(int n) {
        List<Map.Entry<String, Integer>> wordCount = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String query = "SELECT word, count FROM word_count ORDER BY count DESC LIMIT " + n;
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String word = resultSet.getString("word");
                    int count = resultSet.getInt("count");
                    wordCount.add(new AbstractMap.SimpleEntry<>(word, count));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving word count from database: " + e.getMessage());
        }
        return wordCount;
    }

    private void clearTable() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String query = "DELETE FROM word_count";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error clearing table data: " + e.getMessage());
        }
    }
}

