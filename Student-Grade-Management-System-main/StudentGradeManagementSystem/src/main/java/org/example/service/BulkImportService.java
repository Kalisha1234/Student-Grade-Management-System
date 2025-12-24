package org.example.service;

import org.example.exceptions.InvalidGradeException;
import org.example.exceptions.StudentNotFoundException;
import org.example.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for bulk importing grades from CSV files.
 * Handles validation, error tracking, and logging of import operations.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class BulkImportService {
    private EnhancedStudentManager studentManager;
    private CSVParser csvParser;
    private FileExporter fileExporter;

    public BulkImportService(EnhancedStudentManager studentManager,
                             CSVParser csvParser,
                             FileExporter fileExporter) {
        this.studentManager = studentManager;
        this.csvParser = csvParser;
        this.fileExporter = fileExporter;
    }

    /**
     * Imports grades from CSV file with validation and error tracking.
     * 
     * @param filename the CSV filename (without path or extension)
     * @return ImportResult containing success/failure counts and errors
     */
    public ImportResult importResult(String filename) {
        ImportResult result = new ImportResult();
        List<String> errors = new ArrayList<>();

        try {
            List<GradeRecord> records = csvParser.parseCSV("./imports/" + filename + ".csv");

            for (GradeRecord record : records) {
                try {
                    processGradeRecord(record);
                    result.incrementSuccessful();
                } catch (Exception e) {
                    errors.add("Row " + record.getRowNumber() + ": " + e.getMessage());
                    result.incrementFailed();
                }
            }

            // Log the import
            try {
                fileExporter.logImport(filename, records.size(),
                        result.getSuccessful(), result.getFailed(), errors);
            } catch (Exception e) {
                System.err.println("Warning: Could not create import log: " + e.getMessage());
            }

        } catch (Exception e) {
            errors.add("File error: " + e.getMessage());
            result.setFailed(result.getFailed() + 1);
        }

        result.setErrors(errors);
        return result;
    }

    private void processGradeRecord(GradeRecord record)
            throws StudentNotFoundException, InvalidGradeException {

        // Validate student exists
        try {
            studentManager.searchById(record.getStudentId());
        } catch (StudentNotFoundException e) {
            throw new StudentNotFoundException(record.getStudentId());
        }

        // Validate grade range
        if (record.getGrade() < 0 || record.getGrade() > 100) {
            throw new InvalidGradeException(record.getGrade());
        }

        // Create subject based on type
        Subject subject;
        if (record.getSubjectType().equalsIgnoreCase("Core")) {
            subject = new CoreSubject(record.getSubjectName(),
                    generateSubjectCode(record.getSubjectName()));
        } else {
            subject = new ElectiveSubject(record.getSubjectName(),
                    generateSubjectCode(record.getSubjectName()));
        }

        // Create and add grade
        Grade grade = new Grade(record.getStudentId(), subject, record.getGrade());
        studentManager.addGradeToStudent(record.getStudentId(), grade);
    }

    private String generateSubjectCode(String subjectName) {
        if (subjectName.length() >= 3) {
            return subjectName.substring(0, 3).toUpperCase() + "101";
        }
        return subjectName.toUpperCase() + "101";
    }
}