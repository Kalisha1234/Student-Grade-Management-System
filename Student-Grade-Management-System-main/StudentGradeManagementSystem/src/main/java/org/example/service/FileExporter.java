package org.example.service;

import org.example.exceptions.StudentNotFoundException;
import org.example.interfaces.Exportable;
import org.example.models.Grade;
import org.example.models.HonorsStudent;
import org.example.models.Student;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Exports student data and reports to files.
 * Implements Exportable interface supporting multiple report formats.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class FileExporter implements Exportable {
    private static final String REPORTS_DIR = "./reports/";
    private static final String LOGS_DIR = "./logs/";
    private static final String IMPORTS_DIR = "./imports/";

    private ReportGenerator reportGenerator;
    private GPACalculator gpaCalculator;
    private EnhancedStudentManager studentManager;

    public FileExporter(ReportGenerator reportGenerator, GPACalculator gpaCalculator) {
        this.reportGenerator = reportGenerator;
        this.gpaCalculator = gpaCalculator;
        createDirectories();
    }

    public void setStudentManager(EnhancedStudentManager studentManager) {
        this.studentManager = studentManager;
    }

    private void createDirectories() {
        new File(REPORTS_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
        new File(IMPORTS_DIR).mkdirs();
    }

    /**
     * Exports summary report with student overview and performance metrics.
     * 
     * @param studentId the student's ID
     * @param filename output filename (without extension)
     * @throws IOException if file operations fail
     * @throws StudentNotFoundException if student doesn't exist
     */
    @Override
    public void exportSummaryReport(String studentId, String filename)
            throws IOException, StudentNotFoundException {
        if (studentManager == null) {
            throw new IllegalStateException("StudentManager not set. Call setStudentManager() first.");
        }
        
        Student student = studentManager.searchById(studentId);
        StringBuilder report = new StringBuilder();
        
        report.append("==========================================\n");
        report.append("       STUDENT SUMMARY REPORT\n");
        report.append("==========================================\n\n");
        report.append("Generated on: ").append(new Date()).append("\n\n");
        
        report.append("STUDENT: ").append(student.getName()).append("\n");
        report.append("ID: ").append(student.getStudentId()).append("\n");
        report.append("Type: ").append(student.getStudentType()).append(" Student\n\n");
        
        report.append("PERFORMANCE OVERVIEW\n");
        report.append("-------------------\n");
        report.append(String.format("Total Subjects: %d\n", student.getGrades().size()));
        report.append(String.format("Average Grade: %.1f%%\n", student.calculateAverageGrade()));
        report.append(String.format("Letter Grade: %s\n", 
            gpaCalculator.convertPercentageToLetterGrade(student.calculateAverageGrade())));
        report.append("Status: ").append(student.isPassing() ? "Passing" : "Failing").append("\n");
        
        if (student instanceof HonorsStudent) {
            HonorsStudent honorsStudent = (HonorsStudent) student;
            report.append("Honors Eligible: ")
                .append(honorsStudent.checkHonorsEligibility() ? "Yes" : "No")
                .append("\n");
        }
        
        report.append("\n==========================================\n");
        
        writeToFile(REPORTS_DIR + filename + "_summary.txt", report.toString());
    }

    /**
     * Exports detailed report with all grades and comprehensive analysis.
     * 
     * @param studentId the student's ID
     * @param filename output filename (without extension)
     * @throws IOException if file operations fail
     * @throws StudentNotFoundException if student doesn't exist
     */
    @Override
    public void exportDetailedReport(String studentId, String filename)
            throws IOException, StudentNotFoundException {
        if (studentManager == null) {
            throw new IllegalStateException("StudentManager not set. Call setStudentManager() first.");
        }
        
        Student student = studentManager.searchById(studentId);
        StringBuilder report = new StringBuilder();
        
        report.append("==========================================\n");
        report.append("       DETAILED GRADE REPORT\n");
        report.append("==========================================\n\n");
        report.append("Generated on: ").append(new Date()).append("\n\n");
        
        report.append("STUDENT INFORMATION\n");
        report.append("-------------------\n");
        report.append("Student ID: ").append(student.getStudentId()).append("\n");
        report.append("Name: ").append(student.getName()).append("\n");
        report.append("Age: ").append(student.getAge()).append("\n");
        report.append("Email: ").append(student.getEmail()).append("\n");
        report.append("Phone: ").append(student.getPhone()).append("\n");
        report.append("Type: ").append(student.getStudentType()).append(" Student\n");
        report.append("Passing Grade: ").append(student.getPassingGrade()).append("%\n\n");
        
        report.append("GRADE DETAILS\n");
        report.append("-------------------\n");
        report.append(String.format("%-20s | %-12s | %-8s | %s\n", 
            "Subject", "Type", "Grade", "Letter"));
        report.append("------------------------------------------------------------\n");
        
        List<Grade> grades = student.getGrades();
        if (grades.isEmpty()) {
            report.append("No grades recorded yet.\n\n");
        } else {
            for (Grade grade : grades) {
                String letterGrade = gpaCalculator.convertPercentageToLetterGrade(grade.getGrade());
                report.append(String.format("%-20s | %-12s | %6.1f%% | %s\n",
                    grade.getSubject().getSubjectName(),
                    grade.getSubject().getSubjectType(),
                    grade.getGrade(),
                    letterGrade));
            }
            report.append("\n");
        }
        
        report.append("PERFORMANCE SUMMARY\n");
        report.append("-------------------\n");
        report.append(String.format("Total Subjects: %d\n", grades.size()));
        report.append(String.format("Average Grade: %.1f%%\n", student.calculateAverageGrade()));
        report.append(String.format("Overall Letter Grade: %s\n", 
            gpaCalculator.convertPercentageToLetterGrade(student.calculateAverageGrade())));
        report.append(String.format("GPA (4.0 Scale): %.2f\n", 
            gpaCalculator.calculateCumulativeGPA(grades.stream()
                .map(Grade::getGrade)
                .collect(java.util.stream.Collectors.toList()))));
        report.append("Status: ").append(student.isPassing() ? "Passing" : "Failing").append("\n");
        
        if (student instanceof HonorsStudent) {
            HonorsStudent honorsStudent = (HonorsStudent) student;
            report.append("Honors Eligibility: ")
                .append(honorsStudent.checkHonorsEligibility() ? "Yes" : "No")
                .append("\n");
        }
        
        report.append("\n==========================================\n");
        
        writeToFile(REPORTS_DIR + filename + "_detailed.txt", report.toString());
    }

    @Override
    public void exportSearchResults(List<Student> students, String filename) throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("=== SEARCH RESULTS REPORT ===\n\n");
        report.append("Search Date: ").append(new Date()).append("\n");
        report.append("Students Found: ").append(students.size()).append("\n\n");

        report.append("STU ID | NAME           | TYPE    | AVG GRADE | STATUS\n");
        report.append("----------------------------------------------------------\n");

        for (Student student : students) {
            report.append(String.format("%-6s | %-14s | %-7s | %-9.1f%% | %s\n",
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    student.calculateAverageGrade(),
                    student.isPassing() ? "Passing" : "Failing"));
        }

        writeToFile(REPORTS_DIR + filename + "_search.txt", report.toString());
    }

    public void writeToFile(String filepath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(content);
            writer.flush();
        }
    }

    public void logImport(String filename, int totalRows, int successful, int failed, List<String> errors)
            throws IOException {
        StringBuilder log = new StringBuilder();
        log.append("=== IMPORT LOG ===\n");
        log.append("Date: ").append(new Date()).append("\n");
        log.append("File: ").append(filename).append("\n");
        log.append("Total Rows: ").append(totalRows).append("\n");
        log.append("Successfully Imported: ").append(successful).append("\n");
        log.append("Failed: ").append(failed).append("\n\n");

        if (errors != null && !errors.isEmpty()) {
            log.append("ERROR DETAILS:\n");
            for (String error : errors) {
                log.append(error).append("\n");
            }
        }

        String logFilename = LOGS_DIR + "import_log_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
        writeToFile(logFilename, log.toString());
    }

    public String getImportsDirectory() {
        return IMPORTS_DIR;
    }
}
