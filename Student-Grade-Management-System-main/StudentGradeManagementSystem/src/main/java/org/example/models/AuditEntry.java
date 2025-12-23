package org.example.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditEntry {
    private final String timestamp;
    private final long threadId;
    private final String threadName;
    private final String operationType;
    private final String userAction;
    private final long executionTimeMs;
    private final boolean success;
    private final String details;
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public AuditEntry(String operationType, String userAction, long executionTimeMs, boolean success, String details) {
        this.timestamp = LocalDateTime.now().format(ISO_FORMATTER);
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.operationType = operationType;
        this.userAction = userAction;
        this.executionTimeMs = executionTimeMs;
        this.success = success;
        this.details = details;
    }
    
    public String getTimestamp() { return timestamp; }
    public long getThreadId() { return threadId; }
    public String getThreadName() { return threadName; }
    public String getOperationType() { return operationType; }
    public String getUserAction() { return userAction; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public boolean isSuccess() { return success; }
    public String getDetails() { return details; }
    
    public String toLogString() {
        return String.format("%s | Thread-%d (%s) | %s | %s | %dms | %s | %s",
            timestamp, threadId, threadName, operationType, userAction, 
            executionTimeMs, success ? "SUCCESS" : "FAILURE", details);
    }
    
    public LocalDateTime getTimestampAsDateTime() {
        return LocalDateTime.parse(timestamp, ISO_FORMATTER);
    }
}
