package org.example.models;

import java.util.Comparator;

// Custom comparator for sorting students by multiple criteria
public class StudentComparator implements Comparator<Student> {
    
    public enum SortCriteria {
        GPA_DESC, GPA_ASC, NAME_ASC, NAME_DESC, TYPE, ID
    }

    private final SortCriteria primaryCriteria;
    private final SortCriteria secondaryCriteria;

    public StudentComparator(SortCriteria primaryCriteria) {
        this(primaryCriteria, null);
    }

    public StudentComparator(SortCriteria primaryCriteria, SortCriteria secondaryCriteria) {
        this.primaryCriteria = primaryCriteria;
        this.secondaryCriteria = secondaryCriteria;
    }

    // O(1) - comparison operation
    @Override
    public int compare(Student s1, Student s2) {
        int result = compareBy(s1, s2, primaryCriteria);
        if (result == 0 && secondaryCriteria != null) {
            result = compareBy(s1, s2, secondaryCriteria);
        }
        return result;
    }

    private int compareBy(Student s1, Student s2, SortCriteria criteria) {
        switch (criteria) {
            case GPA_DESC:
                return Double.compare(s2.calculateAverageGrade(), s1.calculateAverageGrade());
            case GPA_ASC:
                return Double.compare(s1.calculateAverageGrade(), s2.calculateAverageGrade());
            case NAME_ASC:
                return s1.getName().compareToIgnoreCase(s2.getName());
            case NAME_DESC:
                return s2.getName().compareToIgnoreCase(s1.getName());
            case TYPE:
                return s1.getStudentType().compareTo(s2.getStudentType());
            case ID:
                return s1.getStudentId().compareTo(s2.getStudentId());
            default:
                return 0;
        }
    }

    // Static factory methods for common sorting patterns
    public static StudentComparator byGPADescending() {
        return new StudentComparator(SortCriteria.GPA_DESC, SortCriteria.NAME_ASC);
    }

    public static StudentComparator byNameAscending() {
        return new StudentComparator(SortCriteria.NAME_ASC);
    }

    public static StudentComparator byTypeAndGPA() {
        return new StudentComparator(SortCriteria.TYPE, SortCriteria.GPA_DESC);
    }
}
