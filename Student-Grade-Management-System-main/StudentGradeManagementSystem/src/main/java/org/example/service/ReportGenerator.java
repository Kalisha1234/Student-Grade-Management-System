package org.example.service;

import org.example.models.CoreSubject;
import org.example.models.ElectiveSubject;
import org.example.models.Grade;
import org.example.models.Student;

import java.util.stream.Collectors;

public class ReportGenerator {

    public String generateSummaryReport(Student student) {
        StringBuilder report = new StringBuilder();
        report.append("=== GRADE SUMMARY REPORT ===\n\n");
        report.append("Student: ").append(student.getStudentId()).append(" - ").append(student.getName()).append("\n");
        report.append("Type: ").append(student.getStudentType()).append(" Student\n");
        report.append("Overall Average: ").append(String.format("%.1f%%", student.calculateAverageGrade())).append("\n");
        report.append("Status: ").append(student.isPassing() ? "PASSING ✓" : "FAILING ✗").append("\n");
        report.append("Total Grades: ").append(student.getGrades().size()).append("\n");
        report.append("\n================================\n");

        return report.toString();
    }

    public String generateDetailedReport(Student student, GPACalculator gpaCalculator) {
        StringBuilder report = new StringBuilder();
        report.append(generateSummaryReport(student));
        report.append("\n=== DETAILED GRADE HISTORY ===\n\n");

        report.append("GRD ID | DATE       | SUBJECT     | TYPE    | GRADE | LETTER\n");
        report.append("------------------------------------------------------------\n");

        for (Grade grade : student.getGrades()) {
            report.append(String.format("%-6s | %-10s | %-11s | %-7s | %-5.1f | %s\n",
                    grade.getGradeId(),
                    grade.getDate(),
                    grade.getSubject().getSubjectName(),
                    grade.getSubject().getSubjectType(),
                    grade.getGrade(),
                    gpaCalculator.convertPercentageToLetterGrade(grade.getGrade())));
        }

        // Calculate averages by subject type
        double coreAvg = calculateCoreAverage(student);
        double electiveAvg = calculateElectiveAverage(student);

        report.append("\n=== PERFORMANCE ANALYSIS ===\n");
        report.append(String.format("Core Subjects Average: %.1f%%\n", coreAvg));
        report.append(String.format("Elective Subjects Average: %.1f%%\n", electiveAvg));

        double gpa = gpaCalculator.calculateCumulativeGPA(
                student.getGrades().stream().map(Grade::getGrade).collect(Collectors.toList()));
        report.append(String.format("Cumulative GPA: %.2f / 4.0\n", gpa));
        report.append("Letter Grade: ").append(gpaCalculator.convertPercentageToLetterGrade(student.calculateAverageGrade())).append("\n");

        return report.toString();
    }

    private double calculateCoreAverage(Student student) {
        double sum = 0;
        int count = 0;
        for (Grade grade : student.getGrades()) {
            if (grade.getSubject() instanceof CoreSubject) {
                sum += grade.getGrade();
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    private double calculateElectiveAverage(Student student) {
        double sum = 0;
        int count = 0;
        for (Grade grade : student.getGrades()) {
            if (grade.getSubject() instanceof ElectiveSubject) {
                sum += grade.getGrade();
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }
}