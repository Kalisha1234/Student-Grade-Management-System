package org.example.models;

/**
 * Represents an honors student with higher passing grade requirements.
 * Honors students must achieve 60% to pass and 85% for honors eligibility.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class HonorsStudent extends Student {
    private double passingGrade = 60.0;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public void displayStudentDetails() {
        System.out.println("Student ID: " + getStudentId());
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getStudentType());
        System.out.println("Age: " + getAge());
        System.out.println("Email: " + getEmail());
        System.out.println("Passing Grade: " + getPassingGrade() + "%");
        System.out.println("Honors Eligible: " + (checkHonorsEligibility() ? "Yes" : "No"));
        System.out.println("Status: " + getStatus());
    }

    @Override
    public String getStudentType() {
        return "Honors";
    }

    @Override
    public double getPassingGrade() {
        return passingGrade;
    }

    /**
     * Checks if the student qualifies for honors recognition.
     * 
     * @return true if average grade is 85% or higher
     */
    public boolean checkHonorsEligibility() {
        return calculateAverageGrade() >= 85.0;
    }
}
