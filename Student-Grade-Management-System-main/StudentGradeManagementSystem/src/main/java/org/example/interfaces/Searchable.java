package org.example.interfaces;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.Student;
import java.util.List;

public interface Searchable {
    Student searchById(String studentId) throws StudentNotFoundException;
    List<Student> searchByName(String name);
    List<Student> searchByGradeRange(double min, double max);
    List<Student> searchByType(String studentType);
}