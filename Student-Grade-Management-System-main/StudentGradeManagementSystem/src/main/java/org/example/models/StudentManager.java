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
        // Initialize grades for Alice Johnson (STU001)
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Mathematics", "MATH101"), 85.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("English", "ENG101"), 78.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Science", "SCI101"), 92.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Music", "MUS101"), 88.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Art", "ART101"), 91.0));
//
//        // Initialize grades for Bob Smith (STU002)
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Mathematics", "MATH101"), 92.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("English", "ENG101"), 88.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Science", "SCI101"), 95.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Music", "MUS101"), 90.0));
//
//        // Initialize grades for Carol Martinez (STU003)
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Mathematics", "MATH101"), 45.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("English", "ENG101"), 50.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Science", "SCI101"), 55.0));
        gradeManager.addGrade(new Grade("STU003", new ElectiveSubject("Physical Education", "PE101"), 60.0));

//        // Initialize grades for David Chen (STU004)
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Mathematics", "MATH101"), 95.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("English", "ENG101"), 92.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Science", "SCI101"), 98.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Art", "ART101"), 96.0));

//        // Initialize grades for Emma Wilson (STU005)
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Mathematics", "MATH101"), 67.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("English", "EN" +
                "G101"), 72.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Science", "SCI101"), 70.0));
        gradeManager.addGrade(new Grade("STU005", new ElectiveSubject("Music", "MUS101"), 75.0));

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
        System.out.println("\nSTU ID     | NAME                         | TYPE         | AVG GRADE    | STATUS    | ENROLLED | PASSING | HONORS");
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < studentCount; i++) {
            Student student = students[i];

            // Use GradeManager to compute enrolled subjects and average to avoid stale student-internal lists
            int enrolledSubjects = gradeManager.getGradeCountForStudent(student.getStudentId());
            double average = gradeManager.calculateOverallAverage(student.getStudentId());

            String status = (average >= student.getPassingGrade()) ? "Passing" : "Failing";
            String honorsFlag = "";
            if (student instanceof HonorsStudent) {
                // Determine honors eligibility from the calculated average (Honors rule: >= 85)
                boolean honorsEligible = average >= 85.0;
                honorsFlag = honorsEligible ? "Yes" : "No";
            } else {
                honorsFlag = "-";
            }

            System.out.printf("%-10s | %-25s | %-12s | %-9.1f%% | %-8s | %-7d | %-7.0f%% | %-6s\n",
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    average,
                    status,
                    enrolledSubjects,
                    student.getPassingGrade(),
                    honorsFlag);
        }

        System.out.println("\nSUMMARY:");
        System.out.println("Total Students: " + studentCount);

        // Compute class average using GradeManager (average of student averages)
        double classSum = 0.0;
        for (int i = 0; i < studentCount; i++) {
            classSum += gradeManager.calculateOverallAverage(students[i].getStudentId());
        }
        double classAvg = studentCount > 0 ? classSum / studentCount : 0.0;
        System.out.printf("Average Class Grade: %.1f%%\n", classAvg);

        // Show grade distribution
        System.out.println("\nGRADE DISTRIBUTION BY STUDENT:");
        for (int i = 0; i < studentCount; i++) {
            Student student = students[i];
            int enrolled = gradeManager.getGradeCountForStudent(student.getStudentId());
            double avg = gradeManager.calculateOverallAverage(student.getStudentId());
            String honors = "";
            if (student instanceof HonorsStudent) {
                honors = (avg >= 85.0) ? " | Honors Eligible" : "";
            }
            System.out.printf("%s (%s): %d grades, Average: %.1f%%, Passing Grade: %.0f%%%s\n",
                    student.getName(),
                    student.getStudentId(),
                    enrolled,
                    avg,
                    student.getPassingGrade(),
                    honors);
        }
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