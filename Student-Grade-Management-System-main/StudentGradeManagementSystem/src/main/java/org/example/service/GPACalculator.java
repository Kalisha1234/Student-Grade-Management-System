package org.example.service;


import java.util.List;

/**
 * Converts percentage grades to GPA on a 4.0 scale and letter grades.
 * Uses standard academic grading scale.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class GPACalculator {

    /**
     * Converts percentage grade to GPA on 4.0 scale.
     * 
     * @param percentage grade percentage (0-100)
     * @return GPA value (0.0-4.0)
     */
    public double convertPercentageToGPA(double percentage) {
        if (percentage >= 93) return 4.0;
        else if (percentage >= 90) return 3.7;
        else if (percentage >= 87) return 3.3;
        else if (percentage >= 83) return 3.0;
        else if (percentage >= 80) return 2.7;
        else if (percentage >= 77) return 2.3;
        else if (percentage >= 73) return 2.0;
        else if (percentage >= 70) return 1.7;
        else if (percentage >= 67) return 1.3;
        else if (percentage >= 60) return 1.0;
        else return 0.0;
    }

    /**
     * Converts percentage grade to letter grade.
     * 
     * @param percentage grade percentage (0-100)
     * @return letter grade (A, A-, B+, B, B-, C+, C, C-, D+, D, F)
     */
    public String convertPercentageToLetterGrade(double percentage) {
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

    /**
     * Calculates cumulative GPA from list of percentage grades.
     * 
     * @param grades list of percentage grades
     * @return cumulative GPA on 4.0 scale, or 0.0 if list is empty/null
     */
    public double calculateCumulativeGPA(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        double totalGPA = 0;
        for (Double grade : grades) {
            totalGPA += convertPercentageToGPA(grade);
        }
        return totalGPA / grades.size();
    }
}
