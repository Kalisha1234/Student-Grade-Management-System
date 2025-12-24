package org.example.models;

import org.example.utils.ValidationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base class representing a student in the grade management system.
 * Provides common functionality for all student types with polymorphic behavior.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public abstract class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;
    private static int studentCounter = 1;
    // LinkedList for O(1) insertions at head/tail - efficient for grade history
    private LinkedList<Grade> grades;

    /**
     * Constructs a new Student with validated information.
     * 
     * @param name student's full name
     * @param age student's age
     * @param email student's email address
     * @param phone student's phone number
     * @throws IllegalArgumentException if validation fails
     */
    public Student(String name, int age, String email, String phone) {
        ValidationUtils.validateName(name);
        ValidationUtils.validateEmail(email);
        ValidationUtils.validatePhone(phone);
        
        this.studentId = "STU" + String.format("%03d", studentCounter++);
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.status = "Active";
        this.grades = new LinkedList<>();
    }

    // Getters and setters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public List<Grade> getGrades() { return grades; } // Getter for grades

    public void setStatus(String status) { this.status = status; }

    /**
     * Displays detailed information about the student.
     */
    public abstract void displayStudentDetails();
    
    /**
     * Returns the type of student (Regular or Honors).
     * 
     * @return student type as string
     */
    public abstract String getStudentType();
    
    /**
     * Returns the minimum passing grade for this student type.
     * 
     * @return passing grade percentage
     */
    public abstract double getPassingGrade();

    /**
     * Calculates the average of all recorded grades.
     * Time Complexity: O(n) where n is the number of grades.
     * 
     * @return average grade percentage, or 0.0 if no grades
     */
    public double calculateAverageGrade() {
        if (grades.isEmpty()) return 0.0;

        double sum = 0;
        for (Grade grade : grades) {
            sum += grade.getGrade();
        }
        return sum / grades.size();
    }

    /**
     * Determines if the student is passing based on their average grade.
     * 
     * @return true if average meets or exceeds passing grade
     */
    public boolean isPassing() {
        return calculateAverageGrade() >= getPassingGrade();
    }

    /**
     * Adds a grade to the student's record.
     * Time Complexity: O(1) - adds to end of LinkedList.
     * 
     * @param grade the grade to add
     * @throws IllegalArgumentException if grade is invalid
     */
    public void addGrade(Grade grade) {
        ValidationUtils.validateGrade(grade.getGrade());
        grades.add(grade);
    }

    public static int getStudentCounter() {
        return studentCounter;
    }
}