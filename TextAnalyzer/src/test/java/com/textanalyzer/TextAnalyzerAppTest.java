package com.textanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



public class TextAnalyzerAppTest {

        @Test
        public void whenValidArguments_thenConfigIsValid() {
            // Arrange
            String[] args = new String[]{"-file", "path/to/file.txt", "-top", "5", "-phraseSize", "3"};

            // Act
            TextAnalyzerApp.Config config = TextAnalyzerApp.parseArguments(args);

            // Assert
            assertTrue(config.isValid(), "Config should be valid with correct arguments.");
            assertEquals("path/to/file.txt", config.filePath, "File path should be set correctly.");
            assertEquals(5, config.top, "Top should be set correctly.");
            assertEquals(3, config.phraseSize, "Phrase size should be set correctly.");
        }

    @Test
    public void whenTopIsInvalid_thenConfigIsInvalid() {
        // Arrange
        String[] args = new String[]{"-file", "path/to/file.txt", "-top", "abc", "-phraseSize", "3"};

        // Act
        TextAnalyzerApp.Config config = TextAnalyzerApp.parseArguments(args);

        // Assert
        assertTrue(!config.isValid(), "Config should be invalid if top is not a positive integer.");
        assertEquals("Top must be a positive integer.", config.getErrorMessage(), "Error message should indicate invalid top argument.");
    }

    @Test
    public void whenPhraseSizeIsInvalid_thenConfigIsInvalid() {
        // Arrange
        String[] args = new String[]{"-file", "path/to/file.txt", "-top", "5", "-phraseSize", "0"};

        // Act
        TextAnalyzerApp.Config config = TextAnalyzerApp.parseArguments(args);

        // Assert
        assertTrue(!config.isValid(), "Config should be invalid if phraseSize is less than 2.");
        assertEquals("Phrase size must be greater than 1.", config.getErrorMessage(), "Error message should indicate invalid phraseSize argument.");
    }
    @Test
    public void whenGivenContent_thenCorrectPhraseFrequency() {
        // Arrange
        String content = "one two three two three two three two three";
        int phraseSize = 2;
        Map<String, Integer> expectedFrequency = new HashMap<>();
        expectedFrequency.put("one two", 1);
        expectedFrequency.put("two three", 4);
        expectedFrequency.put("three two", 3);

        // Act
        Map<String, Integer> actualFrequency = TextAnalyzerApp.getPhraseFrequency(content, phraseSize);

        // Assert
        assertEquals(expectedFrequency, actualFrequency, "The phrase frequency is not calculated correctly.");
    }

    @Test
    public void whenGivenText_thenCorrectWordAndSentenceCount() {
        // Arrange
        String content = "Hello world. This is a new test. This is only a test.";
        Map<String, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put("words", 12); // Total number of words
        expectedCounts.put("sentences", 3); // Total number of sentences

        // Act
        Map<String, Integer> actualCounts = TextAnalyzerApp.countWordsAndSentences(content);

        // Assert
        assertEquals(expectedCounts, actualCounts, "The counts of words and sentences are not correct.");
    }

    @Test
    public void whenGivenPhraseFrequency_thenCorrectTopPhrases() {
        // Arrange
        Map<String, Integer> phraseCounts = new HashMap<>();
        phraseCounts.put("hello world", 3);
        phraseCounts.put("world hello", 2);
        phraseCounts.put("goodbye world", 1);
        int top = 2;
        List<Map.Entry<String, Integer>> expectedTopPhrases = new ArrayList<>();
        expectedTopPhrases.add(new AbstractMap.SimpleEntry<>("hello world", 3));
        expectedTopPhrases.add(new AbstractMap.SimpleEntry<>("world hello", 2));

        // Act
        List<Map.Entry<String, Integer>> actualTopPhrases = TextAnalyzerApp.getTopPhrases(phraseCounts, top);

        // Assert
        assertEquals(expectedTopPhrases.size(), actualTopPhrases.size(), "The number of top phrases returned is incorrect.");
        assertTrue(actualTopPhrases.containsAll(expectedTopPhrases), "The top phrases returned are not correct.");
    }

    @Test
    public void whenContentIsEmpty_thenPhraseFrequencyIsEmpty() {
        // Arrange
        String content = "";
        int phraseSize = 2;  // Any value is acceptable as there should be no phrases to count.

        // Act
        Map<String, Integer> actualFrequency = TextAnalyzerApp.getPhraseFrequency(content, phraseSize);

        // Assert
        assertTrue(actualFrequency.isEmpty(), "Phrase frequency should be empty for an empty content string.");
    }

    @Test
    public void whenContentIsEmpty_thenCountsAreZero() {
        // Arrange
        String content = "";

        // Act
        Map<String, Integer> actualCounts = TextAnalyzerApp.countWordsAndSentences(content);

        // Assert
        assertEquals(0, actualCounts.getOrDefault("words", 0), "Word count should be zero for empty content.");
        assertEquals(0, actualCounts.getOrDefault("sentences", 0), "Sentence count should be zero for empty content.");
    }

    @Test
    public void whenContentIsNull_thenPhraseFrequencyIsEmpty() {
        // Arrange
        String content = null;
        int phraseSize = 2;

        // Act
        Map<String, Integer> actualFrequency = TextAnalyzerApp.getPhraseFrequency(content, phraseSize);

        // Assert
        assertTrue(actualFrequency.isEmpty(), "Phrase frequency should be empty for null content.");
    }

    @Test
    public void whenContentIsNull_thenCountsAreZero() {
        // Arrange
        String content = null;

        // Act
        Map<String, Integer> actualCounts = TextAnalyzerApp.countWordsAndSentences(content);

        // Assert
        assertEquals(0, actualCounts.getOrDefault("words", 0), "Word count should be zero for null content.");
        assertEquals(0, actualCounts.getOrDefault("sentences", 0), "Sentence count should be zero for null content.");
    }

    @Test
    public void whenFileExists_thenContentIsReadCorrectly() throws IOException {
        // Arrange
        String expectedContent = "Test content";
        Path tempFile = Files.createTempFile("testFile", ".txt");
        Files.write(tempFile, expectedContent.getBytes());

        // Act
        String actualContent = TextAnalyzerApp.readFileContent(tempFile.toString());

        // Assert
        assertEquals(expectedContent, actualContent, "Content should match the content of the file.");

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void whenFileDoesNotExist_thenReturnsNull() {
        // Arrange
        String pathToNonExistentFile = "nonexistent.txt";

        // Act
        String content = TextAnalyzerApp.readFileContent(pathToNonExistentFile);

        // Assert
        assertNull(content, "Content should be null when reading a non-existent file.");
    }


    @Test
    public void formatAsTable_ShouldFormatCorrectly() {
        // Arrange
        List<Map.Entry<String, Integer>> topPhrases = new ArrayList<>();
        topPhrases.add(new AbstractMap.SimpleEntry<>("Test Phrase One", 1));
        topPhrases.add(new AbstractMap.SimpleEntry<>("Test Phrase Two", 2));

        // Act
        String result = TextAnalyzerApp.formatAsTable(topPhrases);

        // Assert
        String expected = "+--------------------------------+-------+\n" +
                "| Phrase                         | Count |\n" +
                "+--------------------------------+-------+\n" +
                "| Test Phrase One                |     1 |\n" +
                "| Test Phrase Two                |     2 |\n" +
                "+--------------------------------+-------+\n";
        assertEquals(expected, result);
    }

    @Test
    public void formatWordAndSentenceCounts_ShouldFormatCorrectly() {
        // Arrange
        Map<String, Integer> counts = new HashMap<>();
        counts.put("words", 10);
        counts.put("sentences", 2);

        // Act
        String result = TextAnalyzerApp.formatWordAndSentenceCounts(counts);

        // Assert
        String expected = "+----------------------+-------+\n" +
                "| Type                 | Count |\n" +
                "+----------------------+-------+\n" +
                "| Number of words      |    10 |\n" +
                "+----------------------+-------+\n" +
                "| Number of sentences  |     2 |\n" +
                "+----------------------+-------+\n";
        assertEquals(expected, result);
    }


    }














