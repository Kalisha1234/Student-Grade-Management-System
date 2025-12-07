package org.example.service;

import org.example.exceptions.StudentNotFoundException;
import org.example.interfaces.Exportable;
import org.example.models.Student;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileExporter implements Exportable {
    private static final String REPORTS_DIR = "./reports/";
    private static final String LOGS_DIR = "./logs/";
    private static final String IMPORTS_DIR = "./imports/";

    private ReportGenerator reportGenerator;
    private GPACalculator gpaCalculator;

    public FileExporter(ReportGenerator reportGenerator, GPACalculator gpaCalculator) {
        this.reportGenerator = reportGenerator;
        this.gpaCalculator = gpaCalculator;
        createDirectories();
    }

    private void createDirectories() {
        new File(REPORTS_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
        new File(IMPORTS_DIR).mkdirs();
    }

    @Override
    public void exportSummaryReport(String studentId, String filename)
            throws IOException, StudentNotFoundException {
        // In a complete implementation, this would get student from StudentManager
        // For now, we'll create a dummy implementation
        String report = "Summary report for student ID: " + studentId + "\n";
        report += "Generated on: " + new Date() + "\n";
        writeToFile(REPORTS_DIR + filename + "_summary.txt", report);
    }

    @Override
    public void exportDetailedReport(String studentId, String filename)
            throws IOException, StudentNotFoundException {
        String report = "Detailed report for student ID: " + studentId + "\n";
        report += "Generated on: " + new Date() + "\n";
        report += "This would contain all grade details, averages, and analysis.\n";
        writeToFile(REPORTS_DIR + filename + "_detailed.txt", report);
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
