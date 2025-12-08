package org.example;
//import java.util.ArrayList;
//import java.util.List;

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
        addStudent(new RegularStudent("Alice Johnson", 16, "alice.johnson@school.edu", "+1-555-1001"));
        addStudent(new HonorsStudent("Bob Smith", 17, "bob.smith@school.edu", "+1-555-1002"));
        addStudent(new RegularStudent("Carol Martinez", 16, "carol.martinez@school.edu", "+1-555-1003"));
        addStudent(new HonorsStudent("David Chen", 17, "david.chen@school.edu", "+1-555-1004"));
        addStudent(new RegularStudent("Emma Wilson", 16, "emma.wilson@school.edu", "+1-555-1005"));

        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Mathematics", "MATH101"), 85.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("English", "ENG101"), 78.0));
        gradeManager.addGrade(new Grade("STU001", new CoreSubject("Science", "SCI101"), 92.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Art", "ART101"), 65.0));
        gradeManager.addGrade(new Grade("STU001", new ElectiveSubject("Music", "MUS101"), 73.0));

        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Mathematics", "MATH101"), 88.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("English", "ENG101"), 92.0));
        gradeManager.addGrade(new Grade("STU002", new CoreSubject("Science", "SCI101"), 90.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Music", "MUS101"), 82.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Physical Education", "PE101"), 75.0));
        gradeManager.addGrade(new Grade("STU002", new ElectiveSubject("Art", "ART101"), 84.0));

        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Mathematics", "MATH101"), 45.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("English", "ENG101"), 48.0));
        gradeManager.addGrade(new Grade("STU003", new CoreSubject("Science", "SCI101"), 42.0));
        gradeManager.addGrade(new Grade("STU003", new ElectiveSubject("Art", "ART101"), 47.0));

        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Mathematics", "MATH101"), 95.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("English", "ENG101"), 93.0));
        gradeManager.addGrade(new Grade("STU004", new CoreSubject("Science", "SCI101"), 94.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Music", "MUS101"), 91.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Physical Education", "PE101"), 88.0));
        gradeManager.addGrade(new Grade("STU004", new ElectiveSubject("Art", "ART101"), 96.0));

        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Mathematics", "MATH101"), 65.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("English", "ENG101"), 70.0));
        gradeManager.addGrade(new Grade("STU005", new CoreSubject("Science", "SCI101"), 68.0));
        gradeManager.addGrade(new Grade("STU005", new ElectiveSubject("Art", "ART101"), 72.0));
        gradeManager.addGrade(new Grade("STU005", new ElectiveSubject("Music", "MUS101"), 60.0));

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

    public GradeManager getGradeManager() {
        return gradeManager;
    }
}