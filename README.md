# Text Analyzer App
A Java application designed to analyze text files to compute the frequency of words and phrases, and display statistics about the text.

## Installation
Clone this repository to your local machine using:
```bash
git clone https://github.com/yourusername/text-analyzer.git

Prerequisites
Java JDK 11 or above
Maven (for building and running the application)
Usage
Navigate to the project directory and use Maven to compile and run the application:

cd text-analyzer
mvn clean package
java -jar target/textanalyzer-1.0-SNAPSHOT.jar -file "path\to\some\textfile.txt" -top 5 -phraseSize 3

Replace path\to\some\textfile.txt with the path to the text file you want to analyze.

Features
Analyze text files to determine the frequency of words and phrases.
Output statistics such as the number of words and sentences.
Customizable phrase size for frequency analysis.
Results displayed in a formatted table for easy reading.

Running Tests
Execute the following command to run unit tests:
mvn test

License
Distributed under the MIT License. See LICENSE for more information.

Contact
Project Link: https://github.com/cs-vpopa/text-analyzer

Acknowledgements
JUnit
Maven
GitHub Pages
