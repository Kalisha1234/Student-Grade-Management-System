package org.example.exceptions;

/**
 * Exception thrown when an invalid grade value is provided.
 * Valid grades must be between 0 and 100.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class InvalidGradeException extends Exception {
        private double grade;

        /**
         * Constructs exception with the invalid grade value.
         * 
         * @param grade the invalid grade that was provided
         */
        public InvalidGradeException(double grade) {
            super("Grade must be between 0 and 100. You entered: " + grade);
            this.grade = grade;
        }

        public double getGrade() {
            return grade;
        }
    }