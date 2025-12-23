package org.example.service;

import org.example.models.AuditEntry;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AuditLogger {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String LOG_DIR = "./logs/audit/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final ConcurrentLinkedQueue<AuditEntry> logQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService logWriter = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;
    
    public AuditLogger() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create audit log directory: " + e.getMessage());
        }
        startAsyncWriter();
    }
    
    public void log(String operationType, String userAction, long executionTimeMs, boolean success, String details) {
        AuditEntry entry = new AuditEntry(operationType, userAction, executionTimeMs, success, details);
        logQueue.offer(entry);
    }
    
    private void startAsyncWriter() {
        logWriter.submit(() -> {
            while (running || !logQueue.isEmpty()) {
                try {
                    AuditEntry entry = logQueue.poll();
                    if (entry != null) {
                        writeToFile(entry);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error writing audit log: " + e.getMessage());
                }
            }
        });
    }
    
    private synchronized void writeToFile(AuditEntry entry) throws IOException {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path logFile = Paths.get(LOG_DIR, "audit_" + currentDate + ".log");
        
        if (Files.exists(logFile) && Files.size(logFile) > MAX_FILE_SIZE) {
            rotateLog(logFile);
        }
        
        Files.write(logFile, (entry.toLogString() + "\n").getBytes(), 
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    private void rotateLog(Path logFile) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        Path rotatedFile = Paths.get(logFile.toString().replace(".log", "_" + timestamp + ".log"));
        Files.move(logFile, rotatedFile);
    }
    
    public List<AuditEntry> getRecentEntries(int limit) {
        try {
            List<AuditEntry> entries = new ArrayList<>();
            String currentDate = LocalDate.now().format(DATE_FORMATTER);
            Path logFile = Paths.get(LOG_DIR, "audit_" + currentDate + ".log");
            
            if (Files.exists(logFile)) {
                List<String> lines = Files.readAllLines(logFile);
                for (int i = Math.max(0, lines.size() - limit); i < lines.size(); i++) {
                    entries.add(parseLogLine(lines.get(i)));
                }
            }
            return entries;
        } catch (IOException e) {
            System.err.println("Error reading audit log: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<AuditEntry> searchByDateRange(LocalDateTime start, LocalDateTime end) {
        List<AuditEntry> results = new ArrayList<>();
        try {
            Files.list(Paths.get(LOG_DIR))
                .filter(p -> p.toString().endsWith(".log"))
                .forEach(logFile -> {
                    try {
                        Files.readAllLines(logFile).stream()
                            .map(this::parseLogLine)
                            .filter(e -> {
                                LocalDateTime entryTime = e.getTimestampAsDateTime();
                                return !entryTime.isBefore(start) && !entryTime.isAfter(end);
                            })
                            .forEach(results::add);
                    } catch (IOException e) {
                        // Skip file
                    }
                });
        } catch (IOException e) {
            System.err.println("Error searching audit logs: " + e.getMessage());
        }
        return results;
    }
    
    public List<AuditEntry> searchByOperationType(String operationType) {
        return getRecentEntries(1000).stream()
            .filter(e -> e.getOperationType().equalsIgnoreCase(operationType))
            .collect(Collectors.toList());
    }
    
    public List<AuditEntry> searchByThreadId(long threadId) {
        return getRecentEntries(1000).stream()
            .filter(e -> e.getThreadId() == threadId)
            .collect(Collectors.toList());
    }
    
    public Map<String, Object> getStatistics() {
        List<AuditEntry> entries = getRecentEntries(1000);
        Map<String, Object> stats = new HashMap<>();
        
        if (entries.isEmpty()) {
            stats.put("totalOperations", 0);
            stats.put("averageExecutionTime", 0.0);
            stats.put("successRate", 0.0);
            return stats;
        }
        
        long totalTime = entries.stream().mapToLong(AuditEntry::getExecutionTimeMs).sum();
        long successCount = entries.stream().filter(AuditEntry::isSuccess).count();
        
        stats.put("totalOperations", entries.size());
        stats.put("averageExecutionTime", (double) totalTime / entries.size());
        stats.put("successRate", (double) successCount * 100 / entries.size());
        
        Map<String, Long> operationCounts = entries.stream()
            .collect(Collectors.groupingBy(AuditEntry::getOperationType, Collectors.counting()));
        stats.put("operationBreakdown", operationCounts);
        
        return stats;
    }
    
    private AuditEntry parseLogLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length >= 7) {
            String timestamp = parts[0];
            String threadInfo = parts[1];
            long threadId = Long.parseLong(threadInfo.split("-")[1].split(" ")[0]);
            String operationType = parts[2];
            String userAction = parts[3];
            long executionTime = Long.parseLong(parts[4].replace("ms", ""));
            boolean success = parts[5].equals("SUCCESS");
            String details = parts.length > 6 ? parts[6] : "";
            
            return new AuditEntry(operationType, userAction, executionTime, success, details);
        }
        return new AuditEntry("UNKNOWN", "Parse Error", 0, false, line);
    }
    
    public void shutdown() {
        running = false;
        logWriter.shutdown();
        try {
            if (!logWriter.awaitTermination(5, TimeUnit.SECONDS)) {
                logWriter.shutdownNow();
            }
        } catch (InterruptedException e) {
            logWriter.shutdownNow();
        }
    }
}
