package org.example.exceptions;

/**
 * Exception thrown when file format is invalid or unsupported.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class InvalidFileFormatException extends Exception {
    private String filename;

    /**
     * Constructs exception with filename and error message.
     * 
     * @param filename the file that has invalid format
     * @param message description of the format error
     */
    public InvalidFileFormatException(String filename, String message) {
        super("Invalid file format for '" + filename + "': " + message);
        this.filename = filename;
    }
    
    /**
     * Constructs exception with error message only.
     * 
     * @param message description of the format error
     */
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public String getFilename() {
        return filename;
    }
}