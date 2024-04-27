package com.textanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * TextAnalyzerApp class used to analyze text data from a file.
 * It provides the count of words, sentences, and frequency of phrases.
 */
public class TextAnalyzerApp {

    /**
     * An inner class representing the configuration of the application.
     * It stores the file path, top phrases count, and size of the phrases to analyze.
     */
    public static class Config {
        String filePath;
        int top;
        int phraseSize;

        boolean valid = true;
        String errorMessage = "";

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public void setPhraseSize(int phraseSize) {
            this.phraseSize = phraseSize;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Parses command-line arguments to configure the application.
     * @param args The command-line arguments.
     * @return The configuration derived from the arguments.
     */
    public static Config parseArguments(String[] args) {
        Config config = new Config();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                // Validate and set the file path.
                case "-file":
                    if (i + 1 < args.length) {
                        config.setFilePath(args[++i]);
                    } else {
                        config.setValid(false);
                        config.setErrorMessage("File path not specified.");
                    }
                    break;
                // Validate and set the number for the top phrases to be listed.
                case "-top":
                    if (i + 1 < args.length) {
                        try {
                            config.setTop(Integer.parseInt(args[++i]));
                        } catch (NumberFormatException e) {
                            config.setValid(false);
                            config.setErrorMessage("Invalid number for -top.");
                        }
                    } else {
                        config.setValid(false);
                        config.setErrorMessage("Top count not specified.");
                    }
                    break;
                // Validate and set the size for the phrases to analyze.
                case "-phraseSize":
                    if (i + 1 < args.length) {
                        try {
                            config.setPhraseSize(Integer.parseInt(args[++i]));
                        } catch (NumberFormatException e) {
                            config.setValid(false);
                            config.setErrorMessage("Invalid number for -phraseSize.");
                        }
                    } else {
                        config.setValid(false);
                        config.setErrorMessage("Phrase size not specified.");
                    }
                    break;
                // Handle unknown arguments.
                default:
                    config.setValid(false);
                    config.setErrorMessage("Unknown argument: " + args[i]);
                    break;
            }
        }
        return config;
    }

    /**
     * Reads the entire content of a text file into a String.
     * @param filePath The path to the text file.
     * @return The content of the file.
     */
    public static String readFileContent(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null; // Return null to signify read error.
        }
    }

    /**
     * Counts the number of words and sentences in the text content.
     * @param content The text content to analyze.
     * @return A map with counts for words and sentences.
     */
    public static Map<String, Integer> countWordsAndSentences(String content) {
        Map<String, Integer> counts = new HashMap<>();
        if (content != null && !content.isEmpty()) {
            String[] words = content.split("\\s+");
            counts.put("words", words.length);

            String[] sentences = content.split("[.!?]");
            counts.put("sentences", sentences.length);
        } else {
            counts.put("words", 0);
            counts.put("sentences", 0);
        }
        return counts;
    }

    /**
     * Identifies and counts the frequency of each phrase of a given size in the text.
     * @param content The text content to analyze.
     * @param phraseSize The number of words in each phrase.
     * @return A map of phrases to their frequency count.
     */
    public static Map<String, Integer> getPhraseFrequency(String content, int phraseSize) {
        Map<String, Integer> phraseCounts = new HashMap<>();
        if (content != null && phraseSize > 0) {
            String[] words = content.split("\\s+");

            for (int i = 0; i <= words.length - phraseSize; i++) {
                StringBuilder phrase = new StringBuilder();
                for (int j = 0; j < phraseSize; j++) {
                    phrase.append(words[i + j]).append(' ');
                }
                String trimmedPhrase = phrase.toString().trim();
                phraseCounts.put(trimmedPhrase, phraseCounts.getOrDefault(trimmedPhrase, 0) + 1);
            }
        }
        return phraseCounts;
    }

    /**
     * Retrieves the top N phrases sorted by frequency.
     * @param phraseCounts A map of phrases to their frequency count.
     * @param top The number of top phrases to retrieve.
     * @return A list of the top N phrases and their counts.
     */
    public static List<Map.Entry<String, Integer>> getTopPhrases(Map<String, Integer> phraseCounts, int top) {
        List<Map.Entry<String, Integer>> sortedPhrases = new ArrayList<>(phraseCounts.entrySet());
        sortedPhrases.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        return sortedPhrases.subList(0, Math.min(top, sortedPhrases.size()));
    }

    public static void main(String[] args) {

        Config config = parseArguments(args);

        if (config.isValid()) {
            // Perform the text analysis using the provided configuration.
            String fileContent = readFileContent(config.filePath);
            if (fileContent != null) {
                // Display word and sentence counts.
                Map<String, Integer> counts = countWordsAndSentences(fileContent);
                System.out.println("Number of words: " + counts.get("words"));
                System.out.println("Number of sentences: " + counts.get("sentences"));

                // Perform phrase frequency analysis and display top phrases.
                Map<String, Integer> phraseCounts = getPhraseFrequency(fileContent, config.phraseSize);
                List<Map.Entry<String, Integer>> topPhrases = getTopPhrases(phraseCounts, config.top);

                System.out.println("Top phrases:");
                for (Map.Entry<String, Integer> entry : topPhrases) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else {
                // Handle file read error.
                System.out.println("Error: Unable to read the file content.");
            }
        } else {
            // Handle configuration errors.
            System.out.println("Configuration Error: " + config.getErrorMessage());
        }
    }
}
