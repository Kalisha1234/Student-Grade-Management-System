package org.example.service;

import org.example.exceptions.CSVProcessingException;
import org.example.exceptions.InvalidFileFormatException;
import org.example.models.GradeRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public List<GradeRecord> parseCSV(String filepath) throws IOException, InvalidFileFormatException {
        List<GradeRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            int rowNumber = 0;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                // Skip header row and empty lines
                if (rowNumber == 1 || line.trim().isEmpty()) continue;

                try {
                    GradeRecord record = parseLine(line, rowNumber);
                    records.add(record);
                } catch (CSVProcessingException e) {
                    System.err.println("Warning: " + e.getMessage() + " - Skipping row");
                }
            }
        }

        if (records.isEmpty()) {
            throw new InvalidFileFormatException(filepath, "File is empty or contains no valid data");
        }

        return records;
    }

    private GradeRecord parseLine(String line, int rowNumber) throws CSVProcessingException {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            throw new CSVProcessingException(rowNumber,
                    "Expected 4 columns but found " + parts.length);
        }

        String studentId = parts[0].trim();
        String subjectName = parts[1].trim();
        String subjectType = parts[2].trim();

        if (!subjectType.equalsIgnoreCase("Core") && !subjectType.equalsIgnoreCase("Elective")) {
            throw new CSVProcessingException(rowNumber,
                    "Subject type must be 'Core' or 'Elective', found: " + subjectType);
        }

        double grade;
        try {
            grade = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new CSVProcessingException(rowNumber,
                    "Grade must be a number, found: " + parts[3]);
        }

        return new GradeRecord(studentId, subjectName, subjectType, grade, rowNumber);
    }
}
