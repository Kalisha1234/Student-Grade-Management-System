package org.example.interfaces;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.Student;
import java.util.List;

/**
 * Interface for student search operations.
 * Provides multiple search strategies for flexible student queries.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public interface Searchable {
    /**
     * Searches for a student by their unique ID.
     * 
     * @param studentId the student ID
     * @return the student if found
     * @throws StudentNotFoundException if student doesn't exist
     */
    Student searchById(String studentId) throws StudentNotFoundException;
    
    /**
     * Searches for students by name (supports partial matching).
     * 
     * @param name full or partial name
     * @return list of matching students
     */
    List<Student> searchByName(String name);
    
    /**
     * Searches for students within a grade range.
     * 
     * @param min minimum grade percentage
     * @param max maximum grade percentage
     * @return list of matching students
     */
    List<Student> searchByGradeRange(double min, double max);
    
    /**
     * Searches for students by type (Regular or Honors).
     * 
     * @param studentType the type of student
     * @return list of matching students
     */
    List<Student> searchByType(String studentType);
}