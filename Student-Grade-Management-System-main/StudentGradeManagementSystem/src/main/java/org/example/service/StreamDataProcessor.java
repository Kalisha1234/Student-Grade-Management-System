package org.example.service;

import org.example.models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDataProcessor {
    private final EnhancedStudentManager studentManager;
    private final GPACalculator gpaCalculator;

    public StreamDataProcessor(EnhancedStudentManager studentManager, GPACalculator gpaCalculator) {
        this.studentManager = studentManager;
        this.gpaCalculator = gpaCalculator;
    }

    // Filter: Find honors students with GPA > 3.5
    public List<Student> findHonorsStudentsAboveGPA(double minGPA) {
        long startTime = System.nanoTime();
        
        List<Student> result = studentManager.getAllStudents().stream()
            .filter(s -> s instanceof HonorsStudent)
            .filter(s -> gpaCalculator.convertPercentageToGPA(s.calculateAverageGrade()) > minGPA)
            .collect(Collectors.toList());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return result;
    }

    // Map: Extract all student emails
    public List<String> extractAllEmails() {
        long startTime = System.nanoTime();
        
        List<String> emails = studentManager.getAllStudents().stream()
            .map(Student::getEmail)
            .collect(Collectors.toList());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return emails;
    }

    // Reduce: Calculate total of all grades
    public double calculateTotalGrades() {
        long startTime = System.nanoTime();
        
        double total = studentManager.getAllStudents().stream()
            .flatMap(s -> s.getGrades().stream())
            .map(Grade::getGrade)
            .reduce(0.0, Double::sum);
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return total;
    }

    // Collect with grouping: Group students by grade range
    public Map<String, List<Student>> groupStudentsByGradeRange() {
        long startTime = System.nanoTime();
        
        Map<String, List<Student>> grouped = studentManager.getAllStudents().stream()
            .collect(Collectors.groupingBy(s -> {
                double avg = s.calculateAverageGrade();
                if (avg >= 90) return "A (90-100)";
                if (avg >= 80) return "B (80-89)";
                if (avg >= 70) return "C (70-79)";
                if (avg >= 60) return "D (60-69)";
                return "F (0-59)";
            }));
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return grouped;
    }

    // Collect with partitioning: Partition students by passing status
    public Map<Boolean, List<Student>> partitionByPassingStatus() {
        long startTime = System.nanoTime();
        
        Map<Boolean, List<Student>> partitioned = studentManager.getAllStudents().stream()
            .collect(Collectors.partitioningBy(Student::isPassing));
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return partitioned;
    }

    // Calculate average grade per subject using streams
    public Map<String, Double> calculateAverageGradePerSubject() {
        long startTime = System.nanoTime();
        
        Map<String, Double> avgBySubject = studentManager.getAllStudents().stream()
            .flatMap(s -> s.getGrades().stream())
            .collect(Collectors.groupingBy(
                g -> g.getSubject().getSubjectName(),
                Collectors.averagingDouble(Grade::getGrade)
            ));
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return avgBySubject;
    }

    // Extract unique course codes
    public Set<String> extractUniqueCourses() {
        long startTime = System.nanoTime();
        
        Set<String> courses = studentManager.getAllStudents().stream()
            .flatMap(s -> s.getGrades().stream())
            .map(g -> g.getSubject().getSubjectCode())
            .collect(Collectors.toSet());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return courses;
    }

    // Chain operations: Find top 5 students by average grade
    public List<Student> findTop5Students() {
        long startTime = System.nanoTime();
        
        List<Student> top5 = studentManager.getAllStudents().stream()
            .filter(s -> !s.getGrades().isEmpty())
            .sorted((s1, s2) -> Double.compare(s2.calculateAverageGrade(), s1.calculateAverageGrade()))
            .limit(5)
            .collect(Collectors.toList());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return top5;
    }

    // findFirst: Find first student with perfect score
    public Optional<Student> findFirstPerfectScore() {
        long startTime = System.nanoTime();
        
        Optional<Student> result = studentManager.getAllStudents().stream()
            .filter(s -> s.getGrades().stream().anyMatch(g -> g.getGrade() == 100.0))
            .findFirst();
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return result;
    }

    // anyMatch: Check if any student is failing
    public boolean hasFailingStudents() {
        long startTime = System.nanoTime();
        
        boolean result = studentManager.getAllStudents().stream()
            .anyMatch(s -> !s.isPassing());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return result;
    }

    // allMatch: Check if all students have at least one grade
    public boolean allStudentsHaveGrades() {
        long startTime = System.nanoTime();
        
        boolean result = studentManager.getAllStudents().stream()
            .allMatch(s -> !s.getGrades().isEmpty());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return result;
    }

    // noneMatch: Check if no student has GPA below 1.0
    public boolean noStudentsBelowMinGPA() {
        long startTime = System.nanoTime();
        
        boolean result = studentManager.getAllStudents().stream()
            .noneMatch(s -> gpaCalculator.convertPercentageToGPA(s.calculateAverageGrade()) < 1.0);
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return result;
    }

    // Parallel stream processing
    public Map<String, Double> calculateAverageGradePerSubjectParallel() {
        long startTime = System.nanoTime();
        
        Map<String, Double> avgBySubject = studentManager.getAllStudents().parallelStream()
            .flatMap(s -> s.getGrades().stream())
            .collect(Collectors.groupingByConcurrent(
                g -> g.getSubject().getSubjectName(),
                Collectors.averagingDouble(Grade::getGrade)
            ));
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Parallel stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return avgBySubject;
    }

    // Process CSV file line-by-line using Files.lines()
    public long processCSVFileWithStreams(String filepath) throws IOException {
        long startTime = System.nanoTime();
        
        long lineCount = Files.lines(Paths.get(filepath))
            .skip(1) // Skip header
            .filter(line -> !line.trim().isEmpty())
            .count();
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("CSV stream processing time: %.2f ms\n", duration / 1_000_000.0);
        
        return lineCount;
    }

    // Compare sequential vs parallel performance
    public void compareSequentialVsParallel() {
        System.out.println("\n=== SEQUENTIAL VS PARALLEL STREAM COMPARISON ===\n");
        
        // Sequential
        System.out.println("Sequential Stream:");
        long seqStart = System.nanoTime();
        Map<String, Double> seqResult = calculateAverageGradePerSubject();
        long seqDuration = System.nanoTime() - seqStart;
        
        // Parallel
        System.out.println("\nParallel Stream:");
        long parStart = System.nanoTime();
        Map<String, Double> parResult = calculateAverageGradePerSubjectParallel();
        long parDuration = System.nanoTime() - parStart;
        
        System.out.println("\n--- Performance Comparison ---");
        System.out.printf("Sequential: %.2f ms\n", seqDuration / 1_000_000.0);
        System.out.printf("Parallel: %.2f ms\n", parDuration / 1_000_000.0);
        System.out.printf("Speedup: %.2fx\n", (double) seqDuration / parDuration);
    }

    // Complex chained operation example
    public List<String> getTop3HonorsStudentEmails() {
        long startTime = System.nanoTime();
        
        List<String> emails = studentManager.getAllStudents().stream()
            .filter(s -> s instanceof HonorsStudent)
            .filter(s -> !s.getGrades().isEmpty())
            .sorted((s1, s2) -> Double.compare(s2.calculateAverageGrade(), s1.calculateAverageGrade()))
            .limit(3)
            .map(Student::getEmail)
            .collect(Collectors.toList());
        
        long duration = System.nanoTime() - startTime;
        System.out.printf("Stream execution time: %.2f ms\n", duration / 1_000_000.0);
        
        return emails;
    }

    // Display comprehensive stream operations demo
    public void displayStreamOperationsDemo() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           STREAM-BASED DATA PROCESSING DEMO                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // 1. Filter
        System.out.println("1. FILTER: Honors students with GPA > 3.5");
        System.out.println("─────────────────────────────────────────────────────────────");
        List<Student> honorsStudents = findHonorsStudentsAboveGPA(3.5);
        honorsStudents.forEach(s -> System.out.printf("  %s - %s (GPA: %.2f)\n", 
            s.getStudentId(), s.getName(), 
            gpaCalculator.convertPercentageToGPA(s.calculateAverageGrade())));
        System.out.println();

        // 2. Map
        System.out.println("2. MAP: Extract all student emails");
        System.out.println("─────────────────────────────────────────────────────────────");
        List<String> emails = extractAllEmails();
        System.out.println("  Total emails extracted: " + emails.size());
        System.out.println();

        // 3. Reduce
        System.out.println("3. REDUCE: Calculate total of all grades");
        System.out.println("─────────────────────────────────────────────────────────────");
        double total = calculateTotalGrades();
        System.out.printf("  Total grades sum: %.2f\n", total);
        System.out.println();

        // 4. Grouping
        System.out.println("4. COLLECT (Grouping): Group students by grade range");
        System.out.println("─────────────────────────────────────────────────────────────");
        Map<String, List<Student>> grouped = groupStudentsByGradeRange();
        grouped.forEach((range, students) -> 
            System.out.printf("  %s: %d students\n", range, students.size()));
        System.out.println();

        // 5. Partitioning
        System.out.println("5. COLLECT (Partitioning): Partition by passing status");
        System.out.println("─────────────────────────────────────────────────────────────");
        Map<Boolean, List<Student>> partitioned = partitionByPassingStatus();
        System.out.printf("  Passing: %d students\n", partitioned.get(true).size());
        System.out.printf("  Failing: %d students\n", partitioned.get(false).size());
        System.out.println();

        // 6. Average per subject
        System.out.println("6. Average grade per subject");
        System.out.println("─────────────────────────────────────────────────────────────");
        Map<String, Double> avgBySubject = calculateAverageGradePerSubject();
        avgBySubject.forEach((subject, avg) -> 
            System.out.printf("  %s: %.2f%%\n", subject, avg));
        System.out.println();

        // 7. Unique courses
        System.out.println("7. Extract unique course codes");
        System.out.println("─────────────────────────────────────────────────────────────");
        Set<String> courses = extractUniqueCourses();
        System.out.println("  Courses: " + String.join(", ", courses));
        System.out.println();

        // 8. Top 5 students
        System.out.println("8. CHAIN: Top 5 students by average grade");
        System.out.println("─────────────────────────────────────────────────────────────");
        List<Student> top5 = findTop5Students();
        for (int i = 0; i < top5.size(); i++) {
            Student s = top5.get(i);
            System.out.printf("  %d. %s - %s (%.2f%%)\n", 
                i + 1, s.getStudentId(), s.getName(), s.calculateAverageGrade());
        }
        System.out.println();

        // 9. Match operations
        System.out.println("9. MATCH OPERATIONS");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  anyMatch - Has failing students: " + hasFailingStudents());
        System.out.println("  allMatch - All students have grades: " + allStudentsHaveGrades());
        System.out.println("  noneMatch - No students below min GPA: " + noStudentsBelowMinGPA());
        System.out.println();

        // 10. Performance comparison
        compareSequentialVsParallel();
    }
}
