package org.example.exceptions;

/**
 * Exception thrown when a student cannot be found in the system.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class StudentNotFoundException extends Exception {
    private String studentId;

    /**
     * Constructs exception with student ID that wasn't found.
     * 
     * @param studentId the ID that was searched for
     */
    public StudentNotFoundException(String studentId) {
        super("Student with ID '" + studentId + "' not found in the system.");
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}