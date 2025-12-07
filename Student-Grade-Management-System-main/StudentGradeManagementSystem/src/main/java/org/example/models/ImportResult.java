package org.example.models;


import java.util.List;

public class ImportResult {
    private int successful = 0;
    private int failed = 0;
    private List<String> errors;

    public void incrementSuccessful() { successful++; }
    public void incrementFailed() { failed++; }

    // Getters and setters
    public int getSuccessful() { return successful; }
    public int getFailed() { return failed; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public void setFailed(int failed) { this.failed = failed; }
}
