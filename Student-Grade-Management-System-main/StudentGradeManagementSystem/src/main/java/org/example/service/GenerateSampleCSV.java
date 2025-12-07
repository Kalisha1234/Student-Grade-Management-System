package org.example.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GenerateSampleCSV {

    public static void main(String[] args) {
        String csvContent = generateCSVContent();
        String filename = "sample_grades1.csv";

        try {
            writeToCSV(filename, csvContent);
            System.out.println("✓ CSV file generated: " + filename);
            System.out.println("File content:");
            System.out.println(csvContent);
        } catch (IOException e) {
            System.out.println("✗ Error creating CSV file: " + e.getMessage());
        }
    }

    private static String generateCSVContent() {
        StringBuilder csv = new StringBuilder();

        // Add header
        csv.append("StudentID,SubjectName,SubjectType,Grade\n");

        // Add sample data
        List<String[]> gradeData = Arrays.asList(
                new String[]{"STU001", "Mathematics", "Core", "85"},
                new String[]{"STU001", "English", "Core", "78"},
                new String[]{"STU001", "Science", "Core", "92"},
                new String[]{"STU001", "Music", "Elective", "88"},
                new String[]{"STU001", "Art", "Elective", "75"},
                new String[]{"STU002", "Mathematics", "Core", "92"},
                new String[]{"STU002", "English", "Core", "88"},
                new String[]{"STU002", "Science", "Core", "95"},
                new String[]{"STU002", "Music", "Elective", "90"},
                new String[]{"STU002", "Physical Education", "Elective", "85"},
                new String[]{"STU003", "Mathematics", "Core", "45"},
                new String[]{"STU003", "English", "Core", "50"},
                new String[]{"STU003", "Science", "Core", "55"},
                new String[]{"STU003", "Art", "Elective", "60"},
                new String[]{"STU003", "Physical Education", "Elective", "65"},
                new String[]{"STU004", "Mathematics", "Core", "95"},
                new String[]{"STU004", "English", "Core", "92"},
                new String[]{"STU004", "Science", "Core", "98"},
                new String[]{"STU004", "Music", "Elective", "96"},
                new String[]{"STU004", "Art", "Elective", "94"},
                new String[]{"STU005", "Mathematics", "Core", "67"},
                new String[]{"STU005", "English", "Core", "72"},
                new String[]{"STU005", "Science", "Core", "70"},
                new String[]{"STU005", "Music", "Elective", "75"},
                new String[]{"STU005", "Physical Education", "Elective", "68"}
        );

        for (String[] row : gradeData) {
            csv.append(String.join(",", row)).append("\n");
        }

        return csv.toString();
    }

    private static void writeToCSV(String filename, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }
}
