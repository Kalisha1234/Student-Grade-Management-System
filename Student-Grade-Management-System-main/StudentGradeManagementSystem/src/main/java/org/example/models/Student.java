package org.example.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Student {
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;
    private static int studentCounter = 1;
    //private List<Grade> grades;
   //privateList<Grade> grades = new ArrayList<>(); // List to hold grade// s
    List<Grade> grades = new ArrayList<>();
    public Student(String name, int age, String email, String phone) {
        this.studentId = "STU" + String.format("%03d", studentCounter++);
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.status = "Active";
        this.grades = new ArrayList<>(); // Initialize the list
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

    // Abstract methods
    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    // Concrete methods
    public double calculateAverageGrade() {
        if (grades.isEmpty()) return 0.0;

        double sum = 0;
        for (Grade grade : grades) {
            sum += grade.getGrade();
        }
        return sum / grades.size();
    }

    public boolean isPassing() {
        return calculateAverageGrade() >= getPassingGrade();
    }

    // Add this method to add grades
    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public static int getStudentCounter() {
        return studentCounter;
    }
}