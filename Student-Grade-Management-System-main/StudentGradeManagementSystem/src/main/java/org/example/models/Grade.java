package org.example.models;

import org.example.interfaces.Gradable;
import org.example.utils.ValidationUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a grade record for a student in a specific subject.
 * Implements Gradable interface for grade validation and recording.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class Grade implements Gradable, Serializable {
    private static final long serialVersionUID = 1L;
    private final String gradeId;
    private final String studentId;
    private final Subject subject;
    private double grade;
    private final String date;
    private static int gradeCounter = 1;

    /**
     * Constructs a new Grade with validation.
     * 
     * @param studentId the student's ID
     * @param subject the subject for this grade
     * @param grade the numeric grade (0-100)
     * @throws IllegalArgumentException if validation fails
     */
    public Grade(String studentId, Subject subject, double grade) {
        ValidationUtils.validateStudentId(studentId);
        ValidationUtils.validateGrade(grade);
        
        this.gradeId = "GRD" + String.format("%03d", gradeCounter++);
        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        this.date = formatter.format(new Date());
    }

    // Getters
    public String getGradeId() { return gradeId; }
    public String getStudentId() { return studentId; }
    public Subject getSubject() { return subject; }
    public double getGrade() { return grade; }
    public String getDate() { return date; }

    /**
     * Records a new grade value after validation.
     * 
     * @param grade the grade to record (0-100)
     * @return true if grade was recorded successfully
     */
    @Override
    public boolean recordGrade(double grade) {
        if (validateGrade(grade)) {
            ValidationUtils.validateGrade(grade);
            this.grade = grade;
            return true;
        }
        return false;
    }

    /**
     * Validates that a grade is within acceptable range.
     * 
     * @param grade the grade to validate
     * @return true if grade is between 0 and 100
     */
    @Override
    public boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    public void displayGradeDetails() {
        System.out.println("Grade ID: " + gradeId);
        System.out.println("Student: " + studentId);
        System.out.println("Subject: " + subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        System.out.println("Grade: " + grade + "%");
        System.out.println("Date: " + date);
    }

    /**
     * Converts numeric grade to letter grade.
     * 
     * @return letter grade (A, B, C, D, or F)
     */
    public String getLetterGrade() {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else return "F";
    }
}