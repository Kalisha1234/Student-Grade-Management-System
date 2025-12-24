package org.example.service;

import org.example.exceptions.StudentNotFoundException;
import org.example.interfaces.Searchable;
import org.example.models.*;
import org.example.utils.ValidationUtils;

import java.util.*;

/**
 * Enhanced student manager with advanced search, statistics, and GPA reporting.
 * Uses HashMap for O(1) lookups, TreeMap for sorted rankings, and PriorityQueue for task scheduling.
 * Implements Searchable interface for flexible student queries.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class EnhancedStudentManager implements Searchable {
    // HashMap for O(1) student lookup by ID
    private HashMap<String, Student> students;
    // TreeMap for O(log n) sorted GPA rankings (descending order)
    private TreeMap<Double, List<Student>> gpaRankings;
    // PriorityQueue for O(log n) task scheduling by priority
    private PriorityQueue<Task> taskQueue;
    // ArrayList for maintaining insertion order of student IDs
    private ArrayList<String> studentInsertionOrder;

    private StatisticsCalculator statisticsCalculator;
    private GPACalculator gpaCalculator;
    private ReportGenerator reportGenerator;

    public EnhancedStudentManager() {
        students = new HashMap<>();
        gpaRankings = new TreeMap<>(Collections.reverseOrder());
        taskQueue = new PriorityQueue<>();
        studentInsertionOrder = new ArrayList<>();
        statisticsCalculator = new StatisticsCalculator();
        gpaCalculator = new GPACalculator();
        reportGenerator = new ReportGenerator();
        initializeSampleData();
    }

    /**
     * Searches for a student by ID.
     * Time Complexity: O(1) using HashMap.
     * 
     * @param studentId the student ID to search for
     * @return the student if found
     * @throws StudentNotFoundException if student doesn't exist
     */
    @Override
    public Student searchById(String studentId) throws StudentNotFoundException {
        ValidationUtils.validateStudentId(studentId);
        Student student = students.get(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    /**
     * Searches for students by name (partial match).
     * Time Complexity: O(n) iteration + O(n log n) sorting.
     * 
     * @param name name or partial name to search for
     * @return list of matching students sorted by name
     */
    @Override
    public List<Student> searchByName(String name) {
        List<Student> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();

        for (Student student : students.values()) {
            if (student.getName().toLowerCase().contains(searchTerm)) {
                results.add(student);
            }
        }
        // Sort results by name using custom comparator
        results.sort(StudentComparator.byNameAscending());
        return results;
    }

    /**
     * Searches for students within a grade range.
     * Time Complexity: O(n) iteration + O(n log n) sorting.
     * 
     * @param min minimum grade percentage
     * @param max maximum grade percentage
     * @return list of matching students sorted by GPA descending
     */
    @Override
    public List<Student> searchByGradeRange(double min, double max) {
        ValidationUtils.validateGrade(min);
        ValidationUtils.validateGrade(max);
        
        List<Student> results = new ArrayList<>();

        for (Student student : students.values()) {
            double average = student.calculateAverageGrade();
            if (average >= min && average <= max) {
                results.add(student);
            }
        }
        results.sort(StudentComparator.byGPADescending());
        return results;
    }

    // O(n) iteration through HashMap values + O(n log n) sorting
    @Override
    public List<Student> searchByType(String studentType) {
        List<Student> results = new ArrayList<>();

        for (Student student : students.values()) {
            if (student.getStudentType().equalsIgnoreCase(studentType)) {
                results.add(student);
            }
        }
        // Sort by type then GPA using custom comparator
        results.sort(StudentComparator.byTypeAndGPA());
        return results;
    }

    /**
     * Displays comprehensive class statistics including distribution and analysis.
     */
    public void calculateAndDisplayStatistics() {
        List<Double> allGrades = getAllGrades();

        if (allGrades.isEmpty()) {
            System.out.println("No grades available for statistics.");
            return;
        }

        System.out.println("\n=== CLASS STATISTICS ===\n");
        System.out.println("Total Students: " + students.size());
        System.out.println("Total Grades Recorded: " + allGrades.size());

        System.out.println("\nGRADE DISTRIBUTION");
        System.out.println("================================================================================\n");
        
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(allGrades);
        int totalGrades = allGrades.size();
        
        // Define the order of grade categories
        String[] gradeOrder = {"A (90-100%)", "B (80-89%)", "C (70-79%)", "D (60-69%)", "F (0-59%)"};
        
        for (String gradeCategory : gradeOrder) {
            Integer count = distribution.getOrDefault(gradeCategory, 0);
            double percentage = totalGrades > 0 ? (count * 100.0) / totalGrades : 0;
            
            // Create visual bar (each block represents ~2%)
            int barLength = (int) Math.round(percentage / 2.0);
            StringBuilder bar = new StringBuilder();
            
            // Solid part of bar
            for (int i = 0; i < barLength; i++) {
                bar.append("█");
            }
            
            // Dotted background (up to 50 characters total)
            for (int i = barLength; i < 50; i++) {
                bar.append("░");
            }
            
            System.out.printf("%-15s %s  %5.1f%% (%d grades)\n",
                    gradeCategory + ":", bar.toString(), percentage, count);
        }

        System.out.println("\nSTATISTICAL ANALYSIS:");
        System.out.printf("Mean (Average):    %.1f%%\n", statisticsCalculator.calculateMean(allGrades));
        System.out.printf("Median:            %.1f%%\n", statisticsCalculator.calculateMedian(allGrades));
        System.out.printf("Mode:              %.1f%%\n", statisticsCalculator.calculateMode(allGrades));
        System.out.printf("Standard Deviation: %.1f%%\n", statisticsCalculator.calculateStandardDeviation(allGrades));

        if (!allGrades.isEmpty()) {
            double min = Collections.min(allGrades);
            double max = Collections.max(allGrades);
            System.out.printf("Range:             %.1f%% (%.0f%% - %.0f%%)\n",
                    max - min, min, max);
        }
    }

    /**
     * Displays detailed GPA report for a student including rankings.
     * 
     * @param studentId the student's ID
     * @throws StudentNotFoundException if student doesn't exist
     */
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

        int rank = calculateClassRank(student);
        System.out.println("Class Rank: " + rank + " of " + students.size());

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

    // O(n) - uses pre-sorted TreeMap for ranking
    private int calculateClassRank(Student targetStudent) {
        int rank = 1;
        for (Map.Entry<Double, List<Student>> entry : gpaRankings.entrySet()) {
            for (Student student : entry.getValue()) {
                if (student.getStudentId().equals(targetStudent.getStudentId())) {
                    return rank;
                }
                rank++;
            }
        }
        return rank;
    }

    // O(n*m) where n is students and m is grades per student
    private List<Double> getAllGrades() {
        List<Double> allGrades = new ArrayList<>();
        for (Student student : students.values()) {
            for (Grade grade : student.getGrades()) {
                allGrades.add(grade.getGrade());
            }
        }
        return allGrades;
    }

    // O(1) lookup using HashMap.get()
    public Student findStudent(String studentId) {
        return students.get(studentId);
    }

    private void initializeSampleData() {
        addStudent(new RegularStudent("Alice Johnson", 16, "alice.johnson@school.edu", "+1-555-1001"));
        addStudent(new HonorsStudent("Bob Smith", 17, "bob.smith@university.edu", "+1-555-1002"));
        addStudent(new RegularStudent("Carol Martinez", 16, "carol.martinez@college.edu", "+1-555-1003"));
        addStudent(new HonorsStudent("David Chen", 17, "david.chen@school.edu", "+1-555-1004"));
        addStudent(new RegularStudent("Emma Wilson", 16, "emma.wilson@academy.edu", "+1-555-1005"));
        addStudent(new HonorsStudent("Banks Mill", 19, "banksmill@university.edu", "+1-555-1223"));
        addStudent(new RegularStudent("Nece Kalisha", 18, "necekalisha@school.edu", "+1-203-1071"));
        addStudent(new HonorsStudent("Bright Tank", 16, "brighttank@college.edu", "+1-550-1122"));
        addStudent(new RegularStudent("Nece Alisha", 19, "necealisha@academy.edu", "+1-567-1233"));
        addStudent(new HonorsStudent("Brooke Melendez", 17, "brookemelendez@university.edu", "+1-324-1434"));
        addStudent(new RegularStudent("Beauty Bri", 18, "beautybri@school.edu", "+1-666-6666"));
        addStudent(new HonorsStudent("Banny Banv", 19, "bannybanv@college.edu", "+1-555-1009"));
        addStudent(new RegularStudent("Frank Thompson", 17, "frank.thompson@institute.edu", "+1-444-2001"));
        addStudent(new HonorsStudent("Grace Anderson", 18, "grace.anderson@university.edu", "+1-444-2002"));
        addStudent(new RegularStudent("Henry Jackson", 16, "henry.jackson@school.edu", "+1-444-2003"));
        addStudent(new HonorsStudent("Iris Peterson", 19, "iris.peterson@academy.edu", "+1-444-2004"));
        addStudent(new RegularStudent("Jack Robinson", 17, "jack.robinson@college.edu", "+1-444-2005"));
        addStudent(new HonorsStudent("Kelly White", 18, "kelly.white@institute.edu", "+1-444-2006"));
        addStudent(new RegularStudent("Leo Harris", 16, "leo.harris@university.edu", "+1-444-2007"));
        addStudent(new HonorsStudent("Mia Clark", 19, "mia.clark@school.edu", "+1-444-2008"));

        try {
            addGradeToStudent("STU001", new Grade("STU001", new CoreSubject("Mathematics", "MAT101"), 85.0));
            addGradeToStudent("STU001", new Grade("STU001", new CoreSubject("English", "ENG101"), 78.0));
            addGradeToStudent("STU001", new Grade("STU001", new CoreSubject("Science", "SCI101"), 92.0));
            addGradeToStudent("STU001", new Grade("STU001", new ElectiveSubject("Art", "ART101"), 65.0));
            addGradeToStudent("STU001", new Grade("STU001", new ElectiveSubject("Music", "MUS101"), 73.0));
            
            addGradeToStudent("STU002", new Grade("STU002", new CoreSubject("Mathematics", "MAT101"), 88.0));
            addGradeToStudent("STU002", new Grade("STU002", new CoreSubject("English", "ENG101"), 92.0));
            addGradeToStudent("STU002", new Grade("STU002", new CoreSubject("Science", "SCI101"), 90.0));
            addGradeToStudent("STU002", new Grade("STU002", new ElectiveSubject("Music", "MUS101"), 82.0));
            addGradeToStudent("STU002", new Grade("STU002", new ElectiveSubject("Physical Education", "PHY101"), 75.0));
            addGradeToStudent("STU002", new Grade("STU002", new ElectiveSubject("Art", "ART101"), 84.0));
            
            addGradeToStudent("STU003", new Grade("STU003", new CoreSubject("Mathematics", "MAT101"), 45.0));
            addGradeToStudent("STU003", new Grade("STU003", new CoreSubject("English", "ENG101"), 48.0));
            addGradeToStudent("STU003", new Grade("STU003", new CoreSubject("Science", "SCI101"), 42.0));
            addGradeToStudent("STU003", new Grade("STU003", new ElectiveSubject("Art", "ART101"), 47.0));
            
            addGradeToStudent("STU004", new Grade("STU004", new CoreSubject("Mathematics", "MAT101"), 95.0));
            addGradeToStudent("STU004", new Grade("STU004", new CoreSubject("English", "ENG101"), 93.0));
            addGradeToStudent("STU004", new Grade("STU004", new CoreSubject("Science", "SCI101"), 94.0));
            addGradeToStudent("STU004", new Grade("STU004", new ElectiveSubject("Music", "MUS101"), 91.0));
            addGradeToStudent("STU004", new Grade("STU004", new ElectiveSubject("Physical Education", "PHY101"), 88.0));
            addGradeToStudent("STU004", new Grade("STU004", new ElectiveSubject("Art", "ART101"), 96.0));
            
            addGradeToStudent("STU005", new Grade("STU005", new CoreSubject("Mathematics", "MAT101"), 65.0));
            addGradeToStudent("STU005", new Grade("STU005", new CoreSubject("English", "ENG101"), 70.0));
            addGradeToStudent("STU005", new Grade("STU005", new CoreSubject("Science", "SCI101"), 68.0));
            addGradeToStudent("STU005", new Grade("STU005", new ElectiveSubject("Art", "ART101"), 72.0));
            addGradeToStudent("STU005", new Grade("STU005", new ElectiveSubject("Music", "MUS101"), 60.0));

            addGradeToStudent("STU006", new Grade("STU006", new CoreSubject("Mathematics", "MAT101"), 95.0));
            addGradeToStudent("STU006", new Grade("STU006", new CoreSubject("English", "ENG101"), 93.0));
            addGradeToStudent("STU006", new Grade("STU006", new CoreSubject("Science", "SCI101"), 94.0));
            addGradeToStudent("STU006", new Grade("STU006", new ElectiveSubject("Music", "MUS101"), 91.0));
            addGradeToStudent("STU006", new Grade("STU006", new ElectiveSubject("Art", "ART101"), 96.0));

            addGradeToStudent("STU007", new Grade("STU007", new CoreSubject("Mathematics", "MAT101"), 88.0));
            addGradeToStudent("STU007", new Grade("STU007", new CoreSubject("English", "ENG101"), 85.0));
            addGradeToStudent("STU007", new Grade("STU007", new CoreSubject("Science", "SCI101"), 87.0));
            addGradeToStudent("STU007", new Grade("STU007", new ElectiveSubject("Art", "ART101"), 86.0));

            addGradeToStudent("STU008", new Grade("STU008", new CoreSubject("Mathematics", "MAT101"), 93.0));
            addGradeToStudent("STU008", new Grade("STU008", new CoreSubject("English", "ENG101"), 90.0));
            addGradeToStudent("STU008", new Grade("STU008", new CoreSubject("Science", "SCI101"), 92.0));
            addGradeToStudent("STU008", new Grade("STU008", new ElectiveSubject("Music", "MUS101"), 89.0));
            addGradeToStudent("STU008", new Grade("STU008", new ElectiveSubject("Art", "ART101"), 92.0));

            addGradeToStudent("STU009", new Grade("STU009", new CoreSubject("Mathematics", "MAT101"), 75.0));
            addGradeToStudent("STU009", new Grade("STU009", new CoreSubject("English", "ENG101"), 72.0));
            addGradeToStudent("STU009", new Grade("STU009", new CoreSubject("Science", "SCI101"), 74.0));
            addGradeToStudent("STU009", new Grade("STU009", new ElectiveSubject("Art", "ART101"), 73.0));

            addGradeToStudent("STU010", new Grade("STU010", new CoreSubject("Mathematics", "MAT101"), 90.0));
            addGradeToStudent("STU010", new Grade("STU010", new CoreSubject("English", "ENG101"), 88.0));
            addGradeToStudent("STU010", new Grade("STU010", new CoreSubject("Science", "SCI101"), 89.0));
            addGradeToStudent("STU010", new Grade("STU010", new ElectiveSubject("Music", "MUS101"), 89.0));

            addGradeToStudent("STU011", new Grade("STU011", new CoreSubject("Mathematics", "MAT101"), 80.0));
            addGradeToStudent("STU011", new Grade("STU011", new CoreSubject("English", "ENG101"), 84.0));
            addGradeToStudent("STU011", new Grade("STU011", new CoreSubject("Science", "SCI101"), 82.0));

            addGradeToStudent("STU012", new Grade("STU012", new CoreSubject("Mathematics", "MAT101"), 88.0));
            addGradeToStudent("STU012", new Grade("STU012", new CoreSubject("English", "ENG101"), 87.0));
            addGradeToStudent("STU012", new Grade("STU012", new CoreSubject("Science", "SCI101"), 87.0));
            addGradeToStudent("STU012", new Grade("STU012", new ElectiveSubject("Music", "MUS101"), 88.0));

            addGradeToStudent("STU013", new Grade("STU013", new CoreSubject("Mathematics", "MAT101"), 77.0));
            addGradeToStudent("STU013", new Grade("STU013", new CoreSubject("English", "ENG101"), 79.0));
            addGradeToStudent("STU013", new Grade("STU013", new CoreSubject("Science", "SCI101"), 81.0));
            addGradeToStudent("STU013", new Grade("STU013", new ElectiveSubject("Art", "ART101"), 76.0));

            addGradeToStudent("STU014", new Grade("STU014", new CoreSubject("Mathematics", "MAT101"), 91.0));
            addGradeToStudent("STU014", new Grade("STU014", new CoreSubject("English", "ENG101"), 94.0));
            addGradeToStudent("STU014", new Grade("STU014", new CoreSubject("Science", "SCI101"), 92.0));
            addGradeToStudent("STU014", new Grade("STU014", new ElectiveSubject("Music", "MUS101"), 90.0));

            addGradeToStudent("STU015", new Grade("STU015", new CoreSubject("Mathematics", "MAT101"), 83.0));
            addGradeToStudent("STU015", new Grade("STU015", new CoreSubject("English", "ENG101"), 85.0));
            addGradeToStudent("STU015", new Grade("STU015", new CoreSubject("Science", "SCI101"), 84.0));

            addGradeToStudent("STU016", new Grade("STU016", new CoreSubject("Mathematics", "MAT101"), 89.0));
            addGradeToStudent("STU016", new Grade("STU016", new CoreSubject("English", "ENG101"), 87.0));
            addGradeToStudent("STU016", new Grade("STU016", new CoreSubject("Science", "SCI101"), 90.0));
            addGradeToStudent("STU016", new Grade("STU016", new ElectiveSubject("Art", "ART101"), 88.0));

            addGradeToStudent("STU017", new Grade("STU017", new CoreSubject("Mathematics", "MAT101"), 72.0));
            addGradeToStudent("STU017", new Grade("STU017", new CoreSubject("English", "ENG101"), 74.0));
            addGradeToStudent("STU017", new Grade("STU017", new CoreSubject("Science", "SCI101"), 73.0));

            addGradeToStudent("STU018", new Grade("STU018", new CoreSubject("Mathematics", "MAT101"), 96.0));
            addGradeToStudent("STU018", new Grade("STU018", new CoreSubject("English", "ENG101"), 95.0));
            addGradeToStudent("STU018", new Grade("STU018", new CoreSubject("Science", "SCI101"), 97.0));
            addGradeToStudent("STU018", new Grade("STU018", new ElectiveSubject("Music", "MUS101"), 94.0));

            addGradeToStudent("STU019", new Grade("STU019", new CoreSubject("Mathematics", "MAT101"), 81.0));
            addGradeToStudent("STU019", new Grade("STU019", new CoreSubject("English", "ENG101"), 83.0));
            addGradeToStudent("STU019", new Grade("STU019", new CoreSubject("Science", "SCI101"), 82.0));

            addGradeToStudent("STU020", new Grade("STU020", new CoreSubject("Mathematics", "MAT101"), 92.0));
            addGradeToStudent("STU020", new Grade("STU020", new CoreSubject("English", "ENG101"), 91.0));
            addGradeToStudent("STU020", new Grade("STU020", new CoreSubject("Science", "SCI101"), 93.0));
            addGradeToStudent("STU020", new Grade("STU020", new ElectiveSubject("Art", "ART101"), 90.0));

        } catch (Exception e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a student to the system and updates rankings.
     * Time Complexity: O(1) HashMap + O(log n) TreeMap + O(1) ArrayList.
     * 
     * @param student the student to add
     */
    public void addStudent(Student student) {
        students.put(student.getStudentId(), student);
        studentInsertionOrder.add(student.getStudentId());
        updateGPARankings(student);
        
        scheduleTask(new Task("Review enrollment for " + student.getName(), 
            Task.TaskPriority.MEDIUM, student.getStudentId()));
    }

    // O(log n) - updates TreeMap with student's GPA
    private void updateGPARankings(Student student) {
        double gpa = student.calculateAverageGrade();
        gpaRankings.computeIfAbsent(gpa, k -> new ArrayList<>()).add(student);
    }

    /**
     * Adds a grade to a student and updates their ranking.
     * 
     * @param studentId the student's ID
     * @param grade the grade to add
     * @throws StudentNotFoundException if student doesn't exist
     */
    public void addGradeToStudent(String studentId, Grade grade) throws StudentNotFoundException {
        Student student = searchById(studentId);
        removeFromGPARankings(student);
        student.addGrade(grade);
        updateGPARankings(student);
        
        if (!student.isPassing()) {
            scheduleTask(new Task("Student failing - intervention needed: " + student.getName(),
                Task.TaskPriority.HIGH, studentId));
        }
    }

    // O(n) - removes student from old GPA ranking
    private void removeFromGPARankings(Student student) {
        double oldGpa = student.calculateAverageGrade();
        List<Student> studentsAtGpa = gpaRankings.get(oldGpa);
        if (studentsAtGpa != null) {
            studentsAtGpa.remove(student);
            if (studentsAtGpa.isEmpty()) {
                gpaRankings.remove(oldGpa);
            }
        }
    }

    // O(n) iteration through HashMap values
    public void viewAllStudents() {
        System.out.println("\nSTUDENT LISTING");
        System.out.println("________________________________________________________________________________");
        System.out.println();
        System.out.println("STU ID     | NAME                 | TYPE         | AVG GRADE    | STATUS");
        System.out.println("________________________________________________________________________________");
        System.out.println();

        // Sort students by ID for consistent display
        List<Student> sortedStudents = new ArrayList<>(students.values());
        sortedStudents.sort((s1, s2) -> s1.getStudentId().compareTo(s2.getStudentId()));

        for (Student student : sortedStudents) {
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

        System.out.println("Total Students: " + students.size());
        System.out.printf("Average Class Grade: %.1f%%\n", getAverageClassGrade());
    }

    // O(n) iteration through HashMap values
    public double getAverageClassGrade() {
        if (students.isEmpty()) return 0.0;

        double sum = 0;
        for (Student student : students.values()) {
            sum += student.calculateAverageGrade();
        }
        return sum / students.size();
    }

    public int getStudentCount() {
        return students.size();
    }

    // Returns collection of students for iteration
    public Collection<Student> getStudents() {
        return students.values();
    }

    // O(n) - creates list from HashMap values
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    // O(n) - creates list from HashMap keys
    public List<String> getAllStudentIds() {
        return new ArrayList<>(students.keySet());
    }
    
    // O(1) - returns ArrayList maintaining insertion order
    public List<String> getStudentIdsByInsertionOrder() {
        return new ArrayList<>(studentInsertionOrder);
    }

    // O(1) - returns sorted GPA rankings
    public TreeMap<Double, List<Student>> getGPARankings() {
        return gpaRankings;
    }
    
    // O(log n) - adds task to priority queue
    public void scheduleTask(Task task) {
        taskQueue.offer(task);
    }
    
    // O(log n) - retrieves highest priority task
    public Task getNextTask() {
        return taskQueue.poll();
    }
    
    // O(1) - peeks at highest priority task without removing
    public Task peekNextTask() {
        return taskQueue.peek();
    }
    
    // O(n) - returns all pending tasks
    public List<Task> getAllPendingTasks() {
        return new ArrayList<>(taskQueue);
    }
    
    // O(n log n) - sorts students by custom criteria
    public List<Student> getSortedStudents(StudentComparator comparator) {
        List<Student> sortedList = new ArrayList<>(students.values());
        sortedList.sort(comparator);
        return sortedList;
    }
}
