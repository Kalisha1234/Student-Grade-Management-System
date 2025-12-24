package org.example.utils;

import java.util.regex.Pattern;

/**
 * Utility class for validating student data using regex patterns.
 * Provides validation for IDs, emails, phone numbers, names, dates, course codes, and grades.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class ValidationUtils {
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\(\\d{3}\\) \\d{3}-\\d{4}|\\d{3}-\\d{3}-\\d{4}|\\+1-\\d{3}-\\d{4}|\\+1-\\d{3}-\\d{3}-\\d{4}|\\d{10})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+(['\\-\\s][a-zA-Z]+)*$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{3,4}\\d{3}$");
    private static final Pattern GRADE_PATTERN = Pattern.compile("^(100|[1-9]?\\d)$");

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }

    public static boolean isValidCourseCode(String courseCode) {
        return courseCode != null && COURSE_CODE_PATTERN.matcher(courseCode).matches();
    }

    public static boolean isValidGrade(String grade) {
        return grade != null && GRADE_PATTERN.matcher(grade).matches();
    }

    public static boolean isValidGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    /**
     * Validates student ID format (STU###).
     * 
     * @param studentId the student ID to validate
     * @throws IllegalArgumentException if format is invalid
     */
    public static void validateStudentId(String studentId) {
        if (!isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Invalid Student ID: '" + studentId + "'. Expected: STU### (e.g., STU001)");
        }
    }

    /**
     * Validates email format.
     * 
     * @param email the email to validate
     * @throws IllegalArgumentException if format is invalid
     */
    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid Email: '" + email + "'. Expected: user@domain.com");
        }
    }

    /**
     * Validates phone number format.
     * 
     * @param phone the phone number to validate
     * @throws IllegalArgumentException if format is invalid
     */
    public static void validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid Phone: '" + phone + "'. Valid: (123) 456-7890, 123-456-7890, +1-123-456-7890, 1234567890");
        }
    }

    /**
     * Validates name format (letters, spaces, hyphens, apostrophes).
     * 
     * @param name the name to validate
     * @throws IllegalArgumentException if format is invalid
     */
    public static void validateName(String name) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid Name: '" + name + "'. Use letters, spaces, hyphens, apostrophes (e.g., John O'Brien)");
        }
    }

    public static void validateDate(String date) {
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid Date: '" + date + "'. Expected: YYYY-MM-DD (e.g., 2024-01-15)");
        }
    }

    public static void validateCourseCode(String courseCode) {
        if (!isValidCourseCode(courseCode)) {
            throw new IllegalArgumentException("Invalid Course Code: '" + courseCode + "'. Expected: ABC### (e.g., MAT101)");
        }
    }

    /**
     * Validates grade is within 0-100 range.
     * 
     * @param grade the grade to validate
     * @throws IllegalArgumentException if grade is out of range
     */
    public static void validateGrade(double grade) {
        if (!isValidGrade(grade)) {
            throw new IllegalArgumentException("Invalid Grade: " + grade + ". Must be 0-100");
        }
    }
}
