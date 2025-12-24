package org.example.interfaces;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.Student;

import java.util.List;
import java.io.IOException;

/**
 * Interface for exporting student data and reports to files.
 * Supports multiple export formats and report types.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public interface Exportable {
    /**
     * Exports a summary report for a student.
     * 
     * @param studentId the student's ID
     * @param filename the output filename (without extension)
     * @throws IOException if file operations fail
     * @throws StudentNotFoundException if student doesn't exist
     */
    void exportSummaryReport(String studentId, String filename)
            throws IOException, StudentNotFoundException;
    
    /**
     * Exports a detailed report with all grades for a student.
     * 
     * @param studentId the student's ID
     * @param filename the output filename (without extension)
     * @throws IOException if file operations fail
     * @throws StudentNotFoundException if student doesn't exist
     */
    void exportDetailedReport(String studentId, String filename)
            throws IOException, StudentNotFoundException;
    
    /**
     * Exports search results to a file.
     * 
     * @param students list of students to export
     * @param filename the output filename (without extension)
     * @throws IOException if file operations fail
     */
    void exportSearchResults(List<Student> students, String filename)
            throws IOException;
}