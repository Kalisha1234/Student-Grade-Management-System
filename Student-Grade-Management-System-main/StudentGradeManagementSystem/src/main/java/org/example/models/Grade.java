package org.example.models;

import org.example.interfaces.Gradable;
import org.example.utils.ValidationUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Grade implements Gradable, Serializable {
    private static final long serialVersionUID = 1L;
    private final String gradeId;
    private final String studentId;
    private final Subject subject;
    private double grade;
    private final String date;
    private static int gradeCounter = 1;

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

    @Override
    public boolean recordGrade(double grade) {
        if (validateGrade(grade)) {
            ValidationUtils.validateGrade(grade);
            this.grade = grade;
            return true;
        }
        return false;
    }

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

    public String getLetterGrade() {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else return "F";
    }
}