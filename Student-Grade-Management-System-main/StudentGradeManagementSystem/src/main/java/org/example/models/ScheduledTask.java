package org.example.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduledTask implements Serializable {
    private String taskId;
    private String taskName;
    private ScheduleType scheduleType;
    private long intervalSeconds;
    private LocalDateTime lastExecution;
    private LocalDateTime nextExecution;
    private String lastStatus;
    private long lastDurationMs;
    private boolean active;

    public enum ScheduleType {
        HOURLY, DAILY, WEEKLY
    }

    public ScheduledTask(String taskName, ScheduleType scheduleType, long intervalSeconds) {
        this.taskId = "TASK" + System.currentTimeMillis();
        this.taskName = taskName;
        this.scheduleType = scheduleType;
        this.intervalSeconds = intervalSeconds;
        this.active = true;
        this.lastStatus = "Pending";
        this.nextExecution = LocalDateTime.now().plusSeconds(intervalSeconds);
    }

    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public ScheduleType getScheduleType() { return scheduleType; }
    public long getIntervalSeconds() { return intervalSeconds; }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public LocalDateTime getNextExecution() { return nextExecution; }
    public String getLastStatus() { return lastStatus; }
    public long getLastDurationMs() { return lastDurationMs; }
    public boolean isActive() { return active; }

    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    public void setLastStatus(String lastStatus) { this.lastStatus = lastStatus; }
    public void setLastDurationMs(long lastDurationMs) { this.lastDurationMs = lastDurationMs; }
    public void setActive(boolean active) { this.active = active; }

    public String getFormattedNextExecution() {
        return nextExecution != null ? nextExecution.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";
    }

    public String getFormattedLastExecution() {
        return lastExecution != null ? lastExecution.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "Never";
    }

    public long getSecondsUntilNext() {
        if (nextExecution == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), nextExecution).getSeconds();
    }
}
