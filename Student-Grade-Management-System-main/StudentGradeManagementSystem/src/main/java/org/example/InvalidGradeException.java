package org.example;

public class InvalidGradeException extends Exception {
    private double grade;

    public InvalidGradeException(double grade) {
        super("Grade must be between 0 and 100. You entered: " + grade);
        this.grade = grade;
    }

    public double getGrade() {
        return grade;
    }
}