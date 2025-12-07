package org.example.exceptions;

public class InvalidFileFormatException extends Exception {
    private String filename;

    public InvalidFileFormatException(String filename, String message) {
        super("Invalid file format for '" + filename + "': " + message);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}