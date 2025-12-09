package org.example.models;

public class StudentManager {
    private Student[] students;
    private int studentCount;
    private static final int MAX_STUDENTS = 50;
    private GradeManager gradeManager;

    public StudentManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        gradeManager = new GradeManager();
        initializeSampleData();
    }


    private void initializeSampleData() {
         //Add 5 sample students (3 Regular, 2 Honors)
        addStudent(new RegularStudent("Alice Johnson", 16, "alice.johnson@school.edu", "+1-555-1001"));
        addStudent(new HonorsStudent("Bob Smith", 17, "bob.smith@school.edu", "+1-555-1002"));
        addStudent(new RegularStudent("Carol Martinez", 16, "carol.martinez@school.edu", "+1-555-1003"));
        addStudent(new HonorsStudent("David Chen", 17, "david.chen@school.edu", "+1-555-1004"));
        addStudent(new RegularStudent("Emma Wilson", 16, "emma.wilson@school.edu", "+1-555-1005"));
//
        // Initialize grades for Alice Johnson (STU001) - Average should be 78.5%
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Mathematics", "MATH101"), 85.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("English", "ENG101"), 78.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Science", "SCI101"), 92.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Art", "ART101"), 65.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Music", "MUS101"), 73.0));

        // Initialize grades for Bob Smith (STU002) - Average should be 85.2%
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Mathematics", "MATH101"), 88.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("English", "ENG101"), 92.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Science", "SCI101"), 90.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Music", "MUS101"), 82.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Physical Education", "PE101"), 75.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Art", "ART101"), 84.0));

        // Initialize grades for Carol Martinez (STU003) - Average should be 45.0%
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Mathematics", "MATH101"), 45.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("English", "ENG101"), 48.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Science", "SCI101"), 42.0));
        gradeManager.addGrade(new Grade("STU003", new ElectiveSubject("Art", "ART101"), 47.0));

        // Initialize grades for David Chen (STU004) - Average should be 92.8%
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Mathematics", "MATH101"), 95.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("English", "ENG101"), 93.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Science", "SCI101"), 94.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Music", "MUS101"), 91.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Physical Education", "PE101"), 88.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Art", "ART101"), 96.0));

        // Initialize grades for Emma Wilson (STU005) - Average should be 67.0%
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Mathematics", "MATH101"), 65.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("English", "ENG101"), 70.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Science", "SCI101"), 68.0));
        gradeManager.addGrade(new Grade("STU005", new ElectiveSubject("Art", "ART101"), 72.0));
        gradeManager.addGrade(new Grade("STU005", new ElectiveSubject("Music", "MUS101"), 60.0));

        // Sync grades with students
     syncGradesWithStudents();
    }

    private void syncGradesWithStudents() {
        Grade[] allGrades = gradeManager.getGrades();
        for (int i = 0; i < gradeManager.getGradeCount(); i++) {
            if (allGrades[i] != null) {
                Student student = findStudent(allGrades[i].getStudentId());
                if (student != null) {
                    student.addGrade(allGrades[i]);
                }
            }
        }
    }

    public void addStudent(Student student) {
        if (studentCount < MAX_STUDENTS) {
            students[studentCount++] = student;
        }
    }

    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null;
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

            // Use GradeManager to compute enrolled subjects and average to avoid stale student-internal lists
            int enrolledSubjects = gradeManager.getGradeCountForStudent(student.getStudentId());
            double average = gradeManager.calculateOverallAverage(student.getStudentId());

            String status = (average >= student.getPassingGrade()) ? "Passing" : "Failing";
            String honorsInfo = "";
            
            if (student instanceof HonorsStudent) {
                // Determine honors eligibility from the calculated average (Honors rule: >= 85)
                boolean honorsEligible = average >= 85.0;
                honorsInfo = honorsEligible ? " | Honors Eligible" : "";
            }

            System.out.printf("%-10s | %-20s | %-12s | %6.1f%%     | %s%s\n",
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    average,
                    status,
                    honorsInfo);

            System.out.printf("           | Enrolled Subjects: %d | Passing Grade: %.0f%%%s\n",
                    enrolledSubjects,
                    student.getPassingGrade(),
                    student instanceof HonorsStudent ? honorsInfo : "");
            System.out.println("________________________________________________________________________________");
            System.out.println();
        }

        System.out.println("Total Students: " + studentCount);

        // Compute class average using GradeManager (average of student averages)
        double classSum = 0.0;
        for (int i = 0; i < studentCount; i++) {
            classSum += gradeManager.calculateOverallAverage(students[i].getStudentId());
        }
        double classAvg = studentCount > 0 ? classSum / studentCount : 0.0;
        System.out.printf("Average Class Grade: %.1f%%\n", classAvg);
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

    public GradeManager getGradeManager() {
        return gradeManager;
    }

    // Helper method to view a specific student's grades
    public void viewStudentGrades(String studentId) {
        Student student = findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("\nGRADE REPORT FOR: " + student.getName() + " (" + studentId + ")");
        System.out.println("Type: " + student.getStudentType() + " Student");
        System.out.println("Passing Grade: " + student.getPassingGrade() + "%");
        System.out.println("Current Average: " + String.format("%.1f", student.calculateAverageGrade()) + "%");
        System.out.println("Status: " + (student.isPassing() ? "PASSING" : "FAILING"));

        if (student.getGrades().isEmpty()) {
            System.out.println("No grades recorded.");
        } else {
            System.out.println("\nGRADE HISTORY:");
            System.out.println("Subject               | Type     | Grade");
            System.out.println("----------------------------------------");
            for (Grade grade : student.getGrades()) {
                System.out.printf("%-20s | %-8s | %.1f%%\n",
                        grade.getSubject().getSubjectName(),
                        grade.getSubject().getSubjectType(),
                        grade.getGrade());
            }
        }
    }
}