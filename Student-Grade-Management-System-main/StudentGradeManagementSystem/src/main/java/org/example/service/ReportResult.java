package org.example.service;

public class ReportResult {
    private final String studentId;
    private final boolean success;
    private final long durationMs;
    private final String errorMessage;
    
    public ReportResult(String studentId, boolean success, long durationMs, String errorMessage) {
        this.studentId = studentId;
        this.success = success;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public long getDurationMs() {
        return durationMs;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}