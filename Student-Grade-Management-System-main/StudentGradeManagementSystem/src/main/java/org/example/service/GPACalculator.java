package org.example.service;


import java.util.List;

public class GPACalculator {

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

    public double calculateCumulativeGPA(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        double totalGPA = 0;
        for (Double grade : grades) {
            totalGPA += convertPercentageToGPA(grade);
        }
        return totalGPA / grades.size();
    }
}
