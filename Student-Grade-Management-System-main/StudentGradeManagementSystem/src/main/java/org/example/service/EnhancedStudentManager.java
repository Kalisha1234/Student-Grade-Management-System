package org.example.service;


import org.example.exceptions.StudentNotFoundException;
import org.example.interfaces.Searchable;
import org.example.models.Grade;
import org.example.models.HonorsStudent;
import org.example.models.RegularStudent;
import org.example.models.Student;

import java.util.*;

public class EnhancedStudentManager implements Searchable {
    private Student[] students;
    private int studentCount;
    private static final int MAX_STUDENTS = 50;

    private StatisticsCalculator statisticsCalculator;
    private GPACalculator gpaCalculator;
    private ReportGenerator reportGenerator;

    public EnhancedStudentManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        statisticsCalculator = new StatisticsCalculator();
        gpaCalculator = new GPACalculator();
        reportGenerator = new ReportGenerator();
        initializeSampleData();
    }

    @Override
    public Student searchById(String studentId) throws StudentNotFoundException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    @Override
    public List<Student> searchByName(String name) {
        List<Student> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();

        for (int i = 0; i < studentCount; i++) {
            if (students[i].getName().toLowerCase().contains(searchTerm)) {
                results.add(students[i]);
            }
        }
        return results;
    }

    @Override
    public List<Student> searchByGradeRange(double min, double max) {
        List<Student> results = new ArrayList<>();

        for (int i = 0; i < studentCount; i++) {
            double average = students[i].calculateAverageGrade();
            if (average >= min && average <= max) {
                results.add(students[i]);
            }
        }
        return results;
    }

    @Override
    public List<Student> searchByType(String studentType) {
        List<Student> results = new ArrayList<>();

        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentType().equalsIgnoreCase(studentType)) {
                results.add(students[i]);
            }
        }
        return results;
    }

    public void calculateAndDisplayStatistics() {
        List<Double> allGrades = getAllGrades();

        if (allGrades.isEmpty()) {
            System.out.println("No grades available for statistics.");
            return;
        }

        System.out.println("\n=== CLASS STATISTICS ===\n");
        System.out.println("Total Students: " + studentCount);
        System.out.println("Total Grades Recorded: " + allGrades.size());

        System.out.println("\nGRADE DISTRIBUTION:");
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(allGrades);
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            double percentage = allGrades.size() > 0 ? (entry.getValue() * 100.0) / allGrades.size() : 0;
            System.out.printf("%-12s: %6.1f%% (%d grades)\n",
                    entry.getKey(), percentage, entry.getValue());
        }

        System.out.println("\nSTATISTICAL ANALYSIS:");
        System.out.printf("Mean (Average):    %.1f%%\n", statisticsCalculator.calculateMean(allGrades));
        System.out.printf("Median:            %.1f%%\n", statisticsCalculator.calculateMedian(allGrades));
        System.out.printf("Mode:              %.1f%%\n", statisticsCalculator.calculateMode(allGrades));
        System.out.printf("Standard Deviation: %.1f%%\n", statisticsCalculator.calculateStandardDeviation(allGrades));

        // Find highest and lowest grades
        if (!allGrades.isEmpty()) {
            double min = Collections.min(allGrades);
            double max = Collections.max(allGrades);
            System.out.printf("Range:             %.1f%% (%.0f%% - %.0f%%)\n",
                    max - min, min, max);
        }
    }

    public void displayGPAReport(String studentId) throws StudentNotFoundException {
        Student student = searchById(studentId);

        System.out.println("\n=== GPA CALCULATION REPORT ===\n");
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");
        System.out.printf("Overall Average: %.1f%%\n\n", student.calculateAverageGrade());

        System.out.println("GPA CALCULATION (4.0 Scale)");
        System.out.println("\nSubject      | Grade   | GPA Points");
        System.out.println("------------------------------------");

        List<Double> gradeValues = new ArrayList<>();
        for (Grade grade : student.getGrades()) {
            double gpaPoints = gpaCalculator.convertPercentageToGPA(grade.getGrade());
            String letterGrade = gpaCalculator.convertPercentageToLetterGrade(grade.getGrade());

            System.out.printf("%-12s | %-6.1f%% | %.1f (%s)\n",
                    grade.getSubject().getSubjectName(),
                    grade.getGrade(),
                    gpaPoints,
                    letterGrade);

            gradeValues.add(grade.getGrade());
        }

        double cumulativeGPA = gpaCalculator.calculateCumulativeGPA(gradeValues);
        System.out.printf("\nCumulative GPA: %.2f / 4.0\n", cumulativeGPA);
        System.out.println("Letter Grade: " +
                gpaCalculator.convertPercentageToLetterGrade(student.calculateAverageGrade()));

        // Calculate class rank
        int rank = calculateClassRank(student);
        System.out.println("Class Rank: " + rank + " of " + studentCount);

        System.out.println("\nPerformance Analysis:");
        if (cumulativeGPA >= 3.5) {
            System.out.println("✓ Excellent performance (3.5+ GPA)");
        } else if (cumulativeGPA >= 3.0) {
            System.out.println("✓ Good performance (3.0+ GPA)");
        }

        if (student instanceof HonorsStudent) {
            System.out.println(((HonorsStudent) student).checkHonorsEligibility() ?
                    "✓ Honors eligibility maintained" : "✗ Honors eligibility not met");
        }
    }

    private int calculateClassRank(Student targetStudent) {
        // Sort students by average grade descending
        List<Student> sortedStudents = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            sortedStudents.add(students[i]);
        }

        sortedStudents.sort((s1, s2) ->
                Double.compare(s2.calculateAverageGrade(), s1.calculateAverageGrade()));

        // Find rank
        for (int i = 0; i < sortedStudents.size(); i++) {
            if (sortedStudents.get(i).getStudentId().equals(targetStudent.getStudentId())) {
                return i + 1;
            }
        }
        return sortedStudents.size();
    }

    private List<Double> getAllGrades() {
        List<Double> allGrades = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            for (Grade grade : students[i].getGrades()) {
                allGrades.add(grade.getGrade());
            }
        }
        return allGrades;
    }

    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    private void initializeSampleData() {
        // Add 5 sample students (3 Regular, 2 Honors)
        addStudent(new RegularStudent("Alice Johnson", 16, "alice.johnson@school.edu", "+1-555-1001"));
        addStudent(new HonorsStudent("Bob Smith", 17, "bob.smith@school.edu", "+1-555-1002"));
        addStudent(new RegularStudent("Carol Martinez", 16, "carol.martinez@school.edu", "+1-555-1003"));
        addStudent(new HonorsStudent("David Chen", 17, "david.chen@school.edu", "+1-555-1004"));
        addStudent(new RegularStudent("Emma Wilson", 16, "emma.wilson@school.edu", "+1-555-1005"));
        addStudent(new HonorsStudent("Banks Mill", 19, "banksmill@school.edu", "+1-555-1223"));
        // Add 5 sample students (3 Regular, 2 Honors)
        addStudent(new RegularStudent("Nece Kalisha", 18, "necekalisha@school.edu", "+1-203-1071"));
        addStudent(new HonorsStudent("Bright Tank", 16, "brighttank@school.edu", "+1-550-1122"));
        addStudent(new RegularStudent("Nece Alisha", 19, "necealisha@school.edu", "+1-567-1233"));
        addStudent(new HonorsStudent(" Brooke Melendez", 17, "brookemelendez@school.edu", "+1-324-1434"));

        //students with incorrect ID and grade more than 100zl
        addStudent(new RegularStudent("Beauty Bri", 18, "beautybri@school.edu", "+1-666-6666"));
        addStudent(new HonorsStudent("Banny Banv", 19, "bannybanv@school.edu", "+1-555-1009"));



        // Add grades for each student to match expected averages
        try {
            // Alice Johnson (STU001) - Average: 78.5%
            addGradeToStudent("STU001", new org.example.models.Grade("STU001", new org.example.models.CoreSubject("Mathematics", "MATH101"), 85.0));
            addGradeToStudent("STU001", new org.example.models.Grade("STU001", new org.example.models.CoreSubject("English", "ENG101"), 78.0));
            addGradeToStudent("STU001", new org.example.models.Grade("STU001", new org.example.models.CoreSubject("Science", "SCI101"), 92.0));
            addGradeToStudent("STU001", new org.example.models.Grade("STU001", new org.example.models.ElectiveSubject("Art", "ART101"), 65.0));
            addGradeToStudent("STU001", new org.example.models.Grade("STU001", new org.example.models.ElectiveSubject("Music", "MUS101"), 73.0));
            
            // Bob Smith (STU002) - Average: 85.2%
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.CoreSubject("Mathematics", "MATH101"), 88.0));
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.CoreSubject("English", "ENG101"), 92.0));
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.CoreSubject("Science", "SCI101"), 90.0));
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.ElectiveSubject("Music", "MUS101"), 82.0));
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.ElectiveSubject("Physical Education", "PE101"), 75.0));
            addGradeToStudent("STU002", new org.example.models.Grade("STU002", new org.example.models.ElectiveSubject("Art", "ART101"), 84.0));
            
            // Carol Martinez (STU003) - Average: 45.5%
            addGradeToStudent("STU003", new org.example.models.Grade("STU003", new org.example.models.CoreSubject("Mathematics", "MATH101"), 45.0));
            addGradeToStudent("STU003", new org.example.models.Grade("STU003", new org.example.models.CoreSubject("English", "ENG101"), 48.0));
            addGradeToStudent("STU003", new org.example.models.Grade("STU003", new org.example.models.CoreSubject("Science", "SCI101"), 42.0));
            addGradeToStudent("STU003", new org.example.models.Grade("STU003", new org.example.models.ElectiveSubject("Art", "ART101"), 47.0));
            
            // David Chen (STU004) - Average: 92.8%
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.CoreSubject("Mathematics", "MATH101"), 95.0));
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.CoreSubject("English", "ENG101"), 93.0));
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.CoreSubject("Science", "SCI101"), 94.0));
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.ElectiveSubject("Music", "MUS101"), 91.0));
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.ElectiveSubject("Physical Education", "PE101"), 88.0));
            addGradeToStudent("STU004", new org.example.models.Grade("STU004", new org.example.models.ElectiveSubject("Art", "ART101"), 96.0));
            
            // Emma Wilson (STU005) - Average: 67.0%
            addGradeToStudent("STU005", new org.example.models.Grade("STU005", new org.example.models.CoreSubject("Mathematics", "MATH101"), 65.0));
            addGradeToStudent("STU005", new org.example.models.Grade("STU005", new org.example.models.CoreSubject("English", "ENG101"), 70.0));
            addGradeToStudent("STU005", new org.example.models.Grade("STU005", new org.example.models.CoreSubject("Science", "SCI101"), 68.0));
            addGradeToStudent("STU005", new org.example.models.Grade("STU005", new org.example.models.ElectiveSubject("Art", "ART101"), 72.0));
            addGradeToStudent("STU005", new org.example.models.Grade("STU005", new org.example.models.ElectiveSubject("Music", "MUS101"), 60.0));

            addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.CoreSubject("Mathematics", "MATH101"), 95.0));
            addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.CoreSubject("English", "ENG101"), 93.0));
            addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.CoreSubject("Science", "SCI101"), 94.0));
            addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.ElectiveSubject("Music", "MUS101"), 91.0));
           // addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.ElectiveSubject("Physical Education", "PE101"), 88.0));
            addGradeToStudent("STU006", new org.example.models.Grade("STU006", new org.example.models.ElectiveSubject("Art", "ART101"), 96.0));
        } catch (StudentNotFoundException e) {
            // This should not happen during initialization
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }

    public void addStudent(Student student) {
        if (studentCount < MAX_STUDENTS) {
            students[studentCount++] = student;
        }
    }

    public void addGradeToStudent(String studentId, Grade grade) throws StudentNotFoundException {
        Student student = searchById(studentId);
        student.addGrade(grade);
    }

    public void viewAllStudents() {
        System.out.println("\nSTUDENT LISTING");
        System.out.println("________________________________________________________________________________");
        System.out.println();
        System.out.println("STU ID     | NAME                 | TYPE         | AVG GRADE    | STATUS");
        System.out.println("________________________________________________________________________________");
        System.out.println();

        for (int i = 0; i < studentCount; i++) {
            Student student = students[i];
            String status = student.isPassing() ? "Passing" : "Failing";
            String honorsInfo = "";

            if (student instanceof HonorsStudent) {
                honorsInfo = ((HonorsStudent) student).checkHonorsEligibility() ? " | Honors Eligible" : "";
            }

            System.out.printf("%-10s | %-20s | %-12s | %6.1f%%     | %s%s\n",
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    student.calculateAverageGrade(),
                    status,
                    honorsInfo);

            System.out.printf("           | Enrolled Subjects: %d | Passing Grade: %.0f%%%s\n",
                    student.getGrades().size(),
                    student.getPassingGrade(),
                    student instanceof HonorsStudent ? honorsInfo : "");
            System.out.println("________________________________________________________________________________");
            System.out.println();
        }

        System.out.println("Total Students: " + studentCount);
        System.out.printf("Average Class Grade: %.1f%%\n", getAverageClassGrade());
    }

    public double getAverageClassGrade() {
        if (studentCount == 0) return 0.0;

        double sum = 0;
        for (int i = 0; i < studentCount; i++) {
            sum += students[i].calculateAverageGrade();
        }
        return sum / studentCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public Student[] getStudents() {
        return students;
    }

    public List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            ids.add(students[i].getStudentId());
        }
        return ids;
    }
}

