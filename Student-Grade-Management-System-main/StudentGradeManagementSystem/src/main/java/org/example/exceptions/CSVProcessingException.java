package org.example.exceptions;

public class CSVProcessingException extends Exception {
    private int rowNumber;

    public CSVProcessingException(int rowNumber, String message) {
        super("Error processing CSV row " + rowNumber + ": " + message);
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}