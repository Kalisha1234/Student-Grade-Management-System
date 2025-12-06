package org.example;

public class StudentNotFoundException extends Exception {
    private String studentId;

    public StudentNotFoundException(String studentId) {
        super("Student with ID '" + studentId + "' not found in the system.");
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}
