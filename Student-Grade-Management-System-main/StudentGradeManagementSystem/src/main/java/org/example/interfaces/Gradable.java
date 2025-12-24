package org.example.interfaces;

/**
 * Interface for grade validation and recording operations.
 * Ensures consistent grade handling across the system.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public interface Gradable {
    /**
     * Records a grade after validation.
     * 
     * @param grade the grade to record (0-100)
     * @return true if successfully recorded
     */
    boolean recordGrade(double grade);
    
    /**
     * Validates that a grade is within acceptable range.
     * 
     * @param grade the grade to validate
     * @return true if grade is valid (0-100)
     */
    boolean validateGrade(double grade);
}