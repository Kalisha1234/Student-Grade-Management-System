package org.example.interfaces;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.Student;

import java.util.List;
import java.io.IOException;

public interface Exportable {
    void exportSummaryReport(String studentId, String filename)
            throws IOException, StudentNotFoundException;
    void exportDetailedReport(String studentId, String filename)
            throws IOException, StudentNotFoundException;
    void exportSearchResults(List<Student> students, String filename)
            throws IOException;
}