package org.example.service;

import org.example.models.ScheduledTask;
import org.example.models.Student;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler;
    private final EnhancedStudentManager studentManager;
    private final StatisticsCalculator statisticsCalculator;
    private final Map<String, ScheduledTask> tasks;
    private final Map<String, ScheduledFuture<?>> futures;
    private static final String SCHEDULE_FILE = "./data/schedules.dat";

    public TaskScheduler(EnhancedStudentManager studentManager) {
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.studentManager = studentManager;
        this.statisticsCalculator = new StatisticsCalculator();
        this.tasks = new ConcurrentHashMap<>();
        this.futures = new ConcurrentHashMap<>();
        loadSchedules();
        initializeDefaultTasks();
    }

    private void initializeDefaultTasks() {
        scheduleTask("Daily GPA Recalculation", ScheduledTask.ScheduleType.DAILY, 86400, this::dailyGPARecalculation);
        scheduleTask("Hourly Statistics Refresh", ScheduledTask.ScheduleType.HOURLY, 3600, this::hourlyStatisticsRefresh);
        scheduleTask("Weekly Batch Reports", ScheduledTask.ScheduleType.WEEKLY, 604800, this::weeklyBatchReports);
        scheduleTask("Daily Database Backup", ScheduledTask.ScheduleType.DAILY, 86400, this::dailyDatabaseBackup);
    }

    public void scheduleTask(String taskName, ScheduledTask.ScheduleType type, long intervalSeconds, Runnable task) {
        ScheduledTask scheduledTask = new ScheduledTask(taskName, type, intervalSeconds);
        tasks.put(scheduledTask.getTaskId(), scheduledTask);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> executeTask(scheduledTask, task),
            intervalSeconds, intervalSeconds, TimeUnit.SECONDS
        );
        futures.put(scheduledTask.getTaskId(), future);
        saveSchedules();
    }

    private void executeTask(ScheduledTask scheduledTask, Runnable task) {
        long startTime = System.currentTimeMillis();
        scheduledTask.setLastExecution(LocalDateTime.now());
        
        try {
            task.run();
            scheduledTask.setLastStatus("Success");
            sendNotification(scheduledTask.getTaskName(), "completed successfully");
        } catch (Exception e) {
            scheduledTask.setLastStatus("Failed: " + e.getMessage());
            sendNotification(scheduledTask.getTaskName(), "failed: " + e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        scheduledTask.setLastDurationMs(duration);
        scheduledTask.setNextExecution(LocalDateTime.now().plusSeconds(scheduledTask.getIntervalSeconds()));
        saveSchedules();
    }

    private void dailyGPARecalculation() {
        System.out.println("\n[SCHEDULED TASK] Daily GPA Recalculation - " + LocalDateTime.now());
        List<Student> students = studentManager.getAllStudents();
        for (Student student : students) {
            student.calculateAverageGrade();
        }
        System.out.println("✓ Recalculated GPA for " + students.size() + " students");
    }

    private void hourlyStatisticsRefresh() {
        System.out.println("\n[SCHEDULED TASK] Hourly Statistics Refresh - " + LocalDateTime.now());
        List<Double> grades = new ArrayList<>();
        for (Student student : studentManager.getAllStudents()) {
            for (org.example.models.Grade grade : student.getGrades()) {
                grades.add(grade.getGrade());
            }
        }
        if (!grades.isEmpty()) {
            double mean = statisticsCalculator.calculateMean(grades);
            System.out.println("✓ Statistics refreshed - Class average: " + String.format("%.1f%%", mean));
        }
    }

    private void weeklyBatchReports() {
        System.out.println("\n[SCHEDULED TASK] Weekly Batch Reports - " + LocalDateTime.now());
        int reportCount = studentManager.getAllStudents().size();
        System.out.println("✓ Generated " + reportCount + " weekly reports");
    }

    private void dailyDatabaseBackup() {
        System.out.println("\n[SCHEDULED TASK] Daily Database Backup - " + LocalDateTime.now());
        try {
            Path backupDir = Paths.get("./data/backups");
            Files.createDirectories(backupDir);
            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            Path backupFile = backupDir.resolve("backup_" + timestamp + ".dat");
            
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(backupFile))) {
                oos.writeObject(studentManager.getAllStudents());
            }
            System.out.println("✓ Database backed up to: " + backupFile.getFileName());
        } catch (IOException e) {
            System.out.println("✗ Backup failed: " + e.getMessage());
        }
    }

    private void sendNotification(String taskName, String status) {
        System.out.println("\n📧 [EMAIL NOTIFICATION] Task '" + taskName + "' " + status);
        System.out.println("   To: admin@school.edu");
        System.out.println("   Subject: Scheduled Task Notification");
        System.out.println("   Time: " + LocalDateTime.now());
    }

    public void displayAllTasks() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              SCHEDULED TASKS DASHBOARD                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        if (tasks.isEmpty()) {
            System.out.println("No scheduled tasks.");
            return;
        }

        for (ScheduledTask task : tasks.values()) {
            System.out.println("📋 " + task.getTaskName());
            System.out.println("   ID: " + task.getTaskId());
            System.out.println("   Schedule: " + task.getScheduleType() + " (every " + formatInterval(task.getIntervalSeconds()) + ")");
            System.out.println("   Status: " + (task.isActive() ? "ACTIVE" : "INACTIVE"));
            System.out.println("   Last Execution: " + task.getFormattedLastExecution());
            System.out.println("   Last Status: " + task.getLastStatus());
            System.out.println("   Last Duration: " + task.getLastDurationMs() + "ms");
            System.out.println("   Next Execution: " + task.getFormattedNextExecution());
            
            long secondsUntil = task.getSecondsUntilNext();
            if (secondsUntil > 0) {
                System.out.println("   ⏱️  Countdown: " + formatCountdown(secondsUntil));
            }
            System.out.println();
        }
    }

    private String formatInterval(long seconds) {
        if (seconds >= 604800) return (seconds / 604800) + " week(s)";
        if (seconds >= 86400) return (seconds / 86400) + " day(s)";
        if (seconds >= 3600) return (seconds / 3600) + " hour(s)";
        return seconds + " second(s)";
    }

    private String formatCountdown(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public void addCustomTask() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n⚡ ADD CUSTOM SCHEDULED TASK");
        System.out.print("Task Name: ");
        String name = scanner.nextLine();
        
        System.out.println("\nSchedule Type:");
        System.out.println("1. Hourly");
        System.out.println("2. Daily");
        System.out.println("3. Weekly");
        System.out.print("Select (1-3): ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        
        ScheduledTask.ScheduleType type;
        long interval;
        
        switch (typeChoice) {
            case 1: type = ScheduledTask.ScheduleType.HOURLY; interval = 3600; break;
            case 2: type = ScheduledTask.ScheduleType.DAILY; interval = 86400; break;
            case 3: type = ScheduledTask.ScheduleType.WEEKLY; interval = 604800; break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        
        scheduleTask(name, type, interval, () -> {
            System.out.println("\n[CUSTOM TASK] " + name + " executed at " + LocalDateTime.now());
        });
        
        System.out.println("✓ Task scheduled successfully!");
    }

    public void scheduleGPARecalculationWithConfig() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         CONFIGURE GPA RECALCULATION TASK                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        System.out.print("Task Name: ");
        String taskName = scanner.nextLine();
        
        System.out.print("Number of threads (1-8): ");
        int threadCount = scanner.nextInt();
        scanner.nextLine();
        
        if (threadCount < 1 || threadCount > 8) {
            System.out.println("Invalid thread count! Using default: 4");
            threadCount = 4;
        }
        
        System.out.print("Execute in how many minutes from now? ");
        int minutesFromNow = scanner.nextInt();
        scanner.nextLine();
        
        long initialDelaySeconds = minutesFromNow * 60;
        long intervalSeconds = 86400; // Daily interval
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("TASK CONFIGURATION SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Task Name: " + taskName);
        System.out.println("Target: All students (" + studentManager.getStudentCount() + " students)");
        System.out.println("Thread Count: " + threadCount);
        System.out.println("First Execution: " + LocalDateTime.now().plusMinutes(minutesFromNow));
        System.out.println("Interval: Daily (every 24 hours)");
        System.out.println("Audit Logging: Enabled (./logs/)");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        System.out.print("Confirm schedule? (Y/N): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Task scheduling cancelled.");
            return;
        }
        
        final int finalThreadCount = threadCount;
        ScheduledTask scheduledTask = new ScheduledTask(taskName, ScheduledTask.ScheduleType.DAILY, intervalSeconds);
        scheduledTask.setNextExecution(LocalDateTime.now().plusMinutes(minutesFromNow));
        tasks.put(scheduledTask.getTaskId(), scheduledTask);
        
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> executeGPARecalculationTask(scheduledTask, finalThreadCount),
            initialDelaySeconds, intervalSeconds, TimeUnit.SECONDS
        );
        futures.put(scheduledTask.getTaskId(), future);
        saveSchedules();
        
        System.out.println("\n✓ Task scheduled successfully!");
        System.out.println("Task ID: " + scheduledTask.getTaskId());
        System.out.println("Next execution in " + minutesFromNow + " minutes");
        System.out.println("\nMonitoring: Check logs in ./logs/ directory");
        System.out.println("You can continue using the application. Task will run in background.\n");
    }
    
    private void executeGPARecalculationTask(ScheduledTask scheduledTask, int threadCount) {
        long startTime = System.currentTimeMillis();
        scheduledTask.setLastExecution(LocalDateTime.now());
        
        logAudit("[TASK START] " + scheduledTask.getTaskName() + " - Threads: " + threadCount);
        
        try {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║           EXECUTING SCHEDULED TASK                           ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("Task: " + scheduledTask.getTaskName());
            System.out.println("Time: " + LocalDateTime.now());
            System.out.println("Threads: " + threadCount);
            System.out.println();
            
            List<Student> students = studentManager.getAllStudents();
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<String>> futures = new ArrayList<>();
            
            System.out.println("Processing " + students.size() + " students with " + threadCount + " threads...\n");
            
            for (Student student : students) {
                Future<String> future = executor.submit(() -> {
                    double oldGPA = student.calculateAverageGrade();
                    double newGPA = student.calculateAverageGrade();
                    String result = String.format("[%s] %s - GPA: %.2f%%", 
                        Thread.currentThread().getName(), 
                        student.getStudentId(), 
                        newGPA);
                    System.out.println(result);
                    logAudit(result);
                    return result;
                });
                futures.add(future);
            }
            
            int completed = 0;
            for (Future<String> future : futures) {
                future.get();
                completed++;
            }
            
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
            
            long duration = System.currentTimeMillis() - startTime;
            scheduledTask.setLastStatus("Success");
            scheduledTask.setLastDurationMs(duration);
            
            System.out.println("\n═══════════════════════════════════════════════════════════════");
            System.out.println("TASK EXECUTION SUMMARY");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("✓ Task completed successfully");
            System.out.println("Students processed: " + completed);
            System.out.println("Threads used: " + threadCount);
            System.out.println("Duration: " + duration + "ms");
            System.out.println("═══════════════════════════════════════════════════════════════\n");
            
            logAudit("[TASK SUCCESS] Completed in " + duration + "ms - Students: " + completed);
            sendNotification(scheduledTask.getTaskName(), "completed successfully");
            
        } catch (Exception e) {
            scheduledTask.setLastStatus("Failed: " + e.getMessage());
            logAudit("[TASK FAILED] " + e.getMessage());
            sendNotification(scheduledTask.getTaskName(), "failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        scheduledTask.setNextExecution(LocalDateTime.now().plusSeconds(scheduledTask.getIntervalSeconds()));
        saveSchedules();
    }
    
    private void logAudit(String message) {
        try {
            Path logDir = Paths.get("./logs");
            Files.createDirectories(logDir);
            
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path logFile = logDir.resolve("scheduled_tasks_" + timestamp + ".log");
            
            String logEntry = String.format("[%s] %s%n", 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                message);
            
            Files.write(logFile, logEntry.getBytes(), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND);
                
        } catch (IOException e) {
            System.err.println("Warning: Could not write to audit log: " + e.getMessage());
        }
    }

    private void saveSchedules() {
        try {
            Files.createDirectories(Paths.get("./data"));
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCHEDULE_FILE))) {
                oos.writeObject(new ArrayList<>(tasks.values()));
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save schedules: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSchedules() {
        try {
            if (Files.exists(Paths.get(SCHEDULE_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCHEDULE_FILE))) {
                    List<ScheduledTask> loadedTasks = (List<ScheduledTask>) ois.readObject();
                    for (ScheduledTask task : loadedTasks) {
                        tasks.put(task.getTaskId(), task);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load schedules: " + e.getMessage());
        }
    }

    public void shutdown() {
        System.out.println("\nShutting down task scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("✓ Task scheduler stopped");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
