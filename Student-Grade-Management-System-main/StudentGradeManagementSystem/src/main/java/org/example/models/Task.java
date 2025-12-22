package org.example.models;

import java.time.LocalDateTime;

// Task class for priority-based scheduling
public class Task implements Comparable<Task> {
    private String taskId;
    private String description;
    private TaskPriority priority;
    private String studentId;
    private LocalDateTime createdAt;
    private static int taskCounter = 1;

    public enum TaskPriority {
        CRITICAL(1), HIGH(2), MEDIUM(3), LOW(4);
        private final int level;
        TaskPriority(int level) { this.level = level; }
        public int getLevel() { return level; }
    }

    public Task(String description, TaskPriority priority, String studentId) {
        this.taskId = "TSK" + String.format("%03d", taskCounter++);
        this.description = description;
        this.priority = priority;
        this.studentId = studentId;
        this.createdAt = LocalDateTime.now();
    }

    // O(1) - comparison based on priority level
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority.getLevel(), other.priority.getLevel());
    }

    public String getTaskId() { return taskId; }
    public String getDescription() { return description; }
    public TaskPriority getPriority() { return priority; }
    public String getStudentId() { return studentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Student: %s)", 
            priority, taskId, description, studentId);
    }
}
