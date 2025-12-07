package org.example.models;

import java.util.ArrayList;
import java.util.List;

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
            System.out.println("✓ Grade added: " + grade.getGradeId() + " for student " +
                    grade.getStudentId() + " in " + grade.getSubject().getSubjectName());
        } else {
            System.out.println("✗ Grade limit reached! Cannot add more grades.");
        }
    }

    public void viewGradesByStudent(String studentId) {
        System.out.println("\nGRADE HISTORY FOR STUDENT: " + studentId);
        System.out.println("\nGRD ID    | DATE          | SUBJECT        | TYPE       | GRADE  | LETTER");
        System.out.println("-----------------------------------------------------------------------");

        boolean found = false;
        int gradeCountForStudent = 0;
        double totalScore = 0;

        for (int i = gradeCount - 1; i >= 0; i--) {
            if (grades[i] != null && grades[i].getStudentId().equals(studentId)) {
                Grade grade = grades[i];
                String letterGrade = getLetterGrade(grade.getGrade());
                System.out.printf("%-6s    | %-10s    | %-11s    | %-7s    | %-5.1f%% | %s\n",
                        grade.getGradeId(),
                        grade.getDate(),
                        grade.getSubject().getSubjectName(),
                        grade.getSubject().getSubjectType(),
                        grade.getGrade(),
                        letterGrade);
                found = true;
                gradeCountForStudent++;
                totalScore += grade.getGrade();
            }
        }

        if (!found) {
            System.out.println("No grades recorded for this student.");
        } else {
            System.out.println("\nSUMMARY:");
            System.out.println("Total Grades: " + gradeCountForStudent);
            if (gradeCountForStudent > 0) {
                System.out.printf("Average Score: %.1f%%\n", totalScore / gradeCountForStudent);

                // Calculate averages by subject type
                double coreAvg = calculateCoreAverage(studentId);
                double electiveAvg = calculateElectiveAverage(studentId);
                System.out.printf("Core Subjects Average: %.1f%%\n", coreAvg);
                System.out.printf("Elective Subjects Average: %.1f%%\n", electiveAvg);

                // Performance analysis
                System.out.println("\nPERFORMANCE ANALYSIS:");
                double overallAvg = totalScore / gradeCountForStudent;
                if (overallAvg >= 90) {
                    System.out.println("✓ Excellent performance (A range)");
                } else if (overallAvg >= 80) {
                    System.out.println("✓ Good performance (B range)");
                } else if (overallAvg >= 70) {
                    System.out.println("✓ Satisfactory performance (C range)");
                } else if (overallAvg >= 60) {
                    System.out.println("⚠️ Needs improvement (D range)");
                } else {
                    System.out.println("✗ Failing performance (F range)");
                }
            }
        }
    }

    public List<Grade> getGradesForStudent(String studentId) {
        List<Grade> studentGrades = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getStudentId().equals(studentId)) {
                studentGrades.add(grades[i]);
            }
        }
        return studentGrades;
    }

    public int getGradeCountForStudent(String studentId) {
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getStudentId().equals(studentId)) {
                count++;
            }
        }
        return count;
    }

    public void viewAllGrades() {
        System.out.println("\nALL GRADES IN SYSTEM");
        System.out.println("\nGRD ID    | STUDENT ID | DATE       | SUBJECT     | TYPE    | GRADE");
        System.out.println("-------------------------------------------------------------------");

        if (gradeCount == 0) {
            System.out.println("No grades recorded in the system.");
            return;
        }

        for (int i = 0; i < gradeCount; i++) {
            Grade grade = grades[i];
            System.out.printf("%-6s    | %-9s    | %-10s    | %-11s    | %-7s    | %.1f%%\n",
                    grade.getGradeId(),
                    grade.getStudentId(),
                    grade.getDate(),
                    grade.getSubject().getSubjectName(),
                    grade.getSubject().getSubjectType(),
                    grade.getGrade());
        }

        System.out.println("\nSYSTEM SUMMARY:");
        System.out.println("Total Grades in System: " + gradeCount);
        System.out.println("Capacity: " + gradeCount + "/" + MAX_GRADES + " grades");
    }

    public boolean hasStudentTakenSubject(String studentId, String subjectName) {
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null &&
                    grades[i].getStudentId().equals(studentId) &&
                    grades[i].getSubject().getSubjectName().equalsIgnoreCase(subjectName)) {
                return true;
            }
        }
        return false;
    }

    public Grade findGradeById(String gradeId) {
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getGradeId().equals(gradeId)) {
                return grades[i];
            }
        }
        return null;
    }

    public boolean updateGrade(String gradeId, double newGrade) {
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getGradeId().equals(gradeId)) {
                if (newGrade >= 0 && newGrade <= 100) {
                    grades[i].recordGrade(newGrade);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteGrade(String gradeId) {
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getGradeId().equals(gradeId)) {
                // Shift all elements after the deleted grade
                for (int j = i; j < gradeCount - 1; j++) {
                    grades[j] = grades[j + 1];
                }
                grades[gradeCount - 1] = null;
                gradeCount--;
                return true;
            }
        }
        return false;
    }

    public void getGradeStatistics() {
        if (gradeCount == 0) {
            System.out.println("No grades available for statistics.");
            return;
        }

        double sum = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        int[] gradeDistribution = new int[5]; // A, B, C, D, F

        for (int i = 0; i < gradeCount; i++) {
            double grade = grades[i].getGrade();
            sum += grade;

            if (grade > highest) highest = grade;
            if (grade < lowest) lowest = grade;

            // Categorize grade
            if (grade >= 90) gradeDistribution[0]++; // A
            else if (grade >= 80) gradeDistribution[1]++; // B
            else if (grade >= 70) gradeDistribution[2]++; // C
            else if (grade >= 60) gradeDistribution[3]++; // D
            else gradeDistribution[4]++; // F
        }

        System.out.println("\nGRADE SYSTEM STATISTICS");
        System.out.println("========================");
        System.out.println("Total Grades: " + gradeCount);
        System.out.printf("Average Grade: %.1f%%\n", sum / gradeCount);
        System.out.printf("Highest Grade: %.1f%%\n", highest);
        System.out.printf("Lowest Grade: %.1f%%\n", lowest);

        System.out.println("\nGRADE DISTRIBUTION:");
        System.out.printf("A (90-100%%): %d grades (%.1f%%)\n",
                gradeDistribution[0], (gradeDistribution[0] * 100.0) / gradeCount);
        System.out.printf("B (80-89%%):  %d grades (%.1f%%)\n",
                gradeDistribution[1], (gradeDistribution[1] * 100.0) / gradeCount);
        System.out.printf("C (70-79%%):  %d grades (%.1f%%)\n",
                gradeDistribution[2], (gradeDistribution[2] * 100.0) / gradeCount);
        System.out.printf("D (60-69%%):  %d grades (%.1f%%)\n",
                gradeDistribution[3], (gradeDistribution[3] * 100.0) / gradeCount);
        System.out.printf("F (0-59%%):   %d grades (%.1f%%)\n",
                gradeDistribution[4], (gradeDistribution[4] * 100.0) / gradeCount);
    }

    public void getSubjectStatistics(String subjectName) {
        double sum = 0;
        int count = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        String highestStudent = "";
        String lowestStudent = "";

        for (int i = 0; i < gradeCount; i++) {
            if (grades[i] != null && grades[i].getSubject().getSubjectName().equalsIgnoreCase(subjectName)) {
                double grade = grades[i].getGrade();
                sum += grade;
                count++;

                if (grade > highest) {
                    highest = grade;
                    highestStudent = grades[i].getStudentId();
                }
                if (grade < lowest) {
                    lowest = grade;
                    lowestStudent = grades[i].getStudentId();
                }
            }
        }

        if (count == 0) {
            System.out.println("No grades found for subject: " + subjectName);
            return;
        }

        System.out.println("\nSTATISTICS FOR SUBJECT: " + subjectName);
        System.out.println("===========================");
        System.out.println("Total Grades: " + count);
        System.out.printf("Average Score: %.1f%%\n", sum / count);
        System.out.printf("Highest Score: %.1f%% (Student: %s)\n", highest, highestStudent);
        System.out.printf("Lowest Score:  %.1f%% (Student: %s)\n", lowest, lowestStudent);
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


    private String getLetterGrade(double percentage) {
        if (percentage >= 93) return "A";
        else if (percentage >= 90) return "A-";
        else if (percentage >= 87) return "B+";
        else if (percentage >= 83) return "B";
        else if (percentage >= 80) return "B-";
        else if (percentage >= 77) return "C+";
        else if (percentage >= 73) return "C";
        else if (percentage >= 70) return "C-";
        else if (percentage >= 67) return "D+";
        else if (percentage >= 60) return "D";
        else return "F";
    }
}