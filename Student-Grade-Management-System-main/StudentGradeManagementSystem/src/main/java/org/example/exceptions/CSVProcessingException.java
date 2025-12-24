package org.example.exceptions;

/**
 * Exception thrown when CSV file processing encounters errors.
 * Includes row number information for debugging.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class CSVProcessingException extends Exception {
    private int rowNumber;

    /**
     * Constructs exception with row number and error message.
     * 
     * @param rowNumber the CSV row where error occurred
     * @param message description of the error
     */
    public CSVProcessingException(int rowNumber, String message) {
        super("Error processing CSV row " + rowNumber + ": " + message);
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}