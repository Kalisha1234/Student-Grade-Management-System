package org.example;

public class GradeManager {
    private Grade[] grades;
    private int gradeCount;
    private static final int MAX_GRADES = 200;

    public GradeManager() {
        grades = new Grade[MAX_GRADES];
        gradeCount = 0;
    }

    public void addGrade(Grade grade) {
        if (gradeCount < MAX_GRADES) {
            grades[gradeCount++] = grade;
        }
    }

    public void viewGradesByStudent(String studentId) {
        System.out.println("\nGRADE HISTORY");
        System.out.println("\nGRD ID    | DATE          | SUBJECT        | TYPE       | GRADE");
        System.out.println("-----------------------------------------------------");

        boolean found = false;
        for (int i = gradeCount - 1; i >= 0; i--) {
            if (grades[i] != null && grades[i].getStudentId().equals(studentId)) {
                Grade grade = grades[i];
                System.out.printf("%-6s    | %-10s    | %-11s    | %-7s    | %.1f%%\n",
                        grade.getGradeId(),
                        grade.getDate(),
                        grade.getSubject().getSubjectName(),
                        grade.getSubject().getSubjectType(),
                        grade.getGrade());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No grades recorded for this student.");
        }
    }

    public double calculateCoreAverage(String studentId) {
        return calculateSubjectTypeAverage(studentId, "Core");
    }

    public double calculateElectiveAverage(String studentId) {
        return calculateSubjectTypeAverage(studentId, "Elective");
    }

    public double calculateOverallAverage(String studentId) {
        double sum = 0;
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getStudentId().equals(studentId)) {
                sum += grades[i].getGrade();
                count++;
            }
        }

        return count > 0 ? sum / count : 0.0;
    }

    private double calculateSubjectTypeAverage(String studentId, String subjectType) {
        double sum = 0;
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null &&
                    grades[i].getStudentId().equals(studentId) &&
                    grades[i].getSubject().getSubjectType().equals(subjectType)) {
                sum += grades[i].getGrade();
                count++;
            }
        }

        return count > 0 ? sum / count : 0.0;
    }

    public int getGradeCount() {
        return gradeCount;
    }

    public Grade[] getGrades() {
        return grades;
    }
}