package org.example.service;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.Student;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentReportGenerator {
    private final FileExporter fileExporter;
    private final EnhancedStudentManager studentManager;
    private final int maxThreads;
    
    public ConcurrentReportGenerator(FileExporter fileExporter, EnhancedStudentManager studentManager) {
        this.fileExporter = fileExporter;
        this.studentManager = studentManager;
        this.maxThreads = Math.min(8, Math.max(2, Runtime.getRuntime().availableProcessors()));
    }
    
    public BatchReportResult generateBatchReports(List<String> studentIds, String reportType, int threadCount) {
        threadCount = Math.min(maxThreads, Math.max(2, threadCount));
        
        System.out.println("\nInitializing thread pool...");
        System.out.println("✓ Fixed Thread Pool created: " + threadCount + " threads");
        System.out.println("\nProcessing " + studentIds.size() + " student reports...");
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<ReportTask> completionService = new ExecutorCompletionService<>(executor);
        
        long startTime = System.currentTimeMillis();
        AtomicInteger completedReports = new AtomicInteger(0);
        List<ReportResult> results = Collections.synchronizedList(new ArrayList<>());
        
        // Thread status tracking
        Map<Integer, String> threadStatus = new ConcurrentHashMap<>();
        Map<Integer, Long> threadStartTimes = new ConcurrentHashMap<>();
        
        // Initialize thread status
        for (int i = 1; i <= threadCount; i++) {
            threadStatus.put(i, "Idle");
        }
        
        // Submit tasks
        for (int i = 0; i < studentIds.size(); i++) {
            String studentId = studentIds.get(i);
            ReportTask task = new ReportTask(studentId, reportType, startTime, threadStatus, threadStartTimes, i % threadCount + 1);
            completionService.submit(task);
        }
        
        // Monitor progress with thread visualization
        ThreadProgressMonitor monitor = new ThreadProgressMonitor(studentIds.size(), completedReports, threadStatus, threadStartTimes, threadCount, startTime);
        Thread progressThread = new Thread(monitor);
        progressThread.start();
        
        // Collect results
        for (int i = 0; i < studentIds.size(); i++) {
            try {
                Future<ReportTask> future = completionService.take();
                ReportTask completedTask = future.get();
                results.add(completedTask.getResult());
                completedReports.incrementAndGet();
            } catch (Exception e) {
                results.add(new ReportResult("UNKNOWN", false, 0, e.getMessage()));
                completedReports.incrementAndGet();
            }
        }
        
        monitor.stop();
        progressThread.interrupt();
        
        // Shutdown executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Final statistics
        System.out.println("\n✓ BATCH GENERATION COMPLETED!");
        System.out.println("\nTime Statistics:");
        System.out.println("  Elapsed: " + totalTime + "ms");
        System.out.println("  Avg Report Time: " + (totalTime / studentIds.size()) + "ms");
        System.out.println("  Throughput: " + String.format("%.1f", (studentIds.size() * 1000.0) / totalTime) + " reports/sec");
        
        return new BatchReportResult(results, totalTime, threadCount, studentIds.size());
    }
    
    private class ReportTask implements Callable<ReportTask> {
        private final String studentId;
        private final String reportType;
        private final long batchStartTime;
        private final Map<Integer, String> threadStatus;
        private final Map<Integer, Long> threadStartTimes;
        private final int threadId;
        private ReportResult result;
        
        public ReportTask(String studentId, String reportType, long batchStartTime, 
                         Map<Integer, String> threadStatus, Map<Integer, Long> threadStartTimes, int threadId) {
            this.studentId = studentId;
            this.reportType = reportType;
            this.batchStartTime = batchStartTime;
            this.threadStatus = threadStatus;
            this.threadStartTimes = threadStartTimes;
            this.threadId = threadId;
        }
        
        @Override
        public ReportTask call() {
            long taskStart = System.currentTimeMillis();
            threadStatus.put(threadId, studentId + " (in progress)");
            threadStartTimes.put(threadId, taskStart);
            
            try {
                String filename = String.format("%s_%s_%d", studentId, reportType, taskStart);
                
                if ("summary".equals(reportType)) {
                    fileExporter.exportSummaryReport(studentId, filename);
                } else {
                    fileExporter.exportDetailedReport(studentId, filename);
                }
                
                long duration = System.currentTimeMillis() - taskStart;
                threadStatus.put(threadId, studentId + " ✓ (" + duration + "ms)");
                result = new ReportResult(studentId, true, duration, null);
                
            } catch (StudentNotFoundException | IOException e) {
                long duration = System.currentTimeMillis() - taskStart;
                threadStatus.put(threadId, studentId + " ✗ (" + duration + "ms)");
                result = new ReportResult(studentId, false, duration, e.getMessage());
            }
            return this;
        }
        
        public ReportResult getResult() {
            return result;
        }
    }
    
    private static class ThreadProgressMonitor implements Runnable {
        private final int totalReports;
        private final AtomicInteger completed;
        private final Map<Integer, String> threadStatus;
        private final Map<Integer, Long> threadStartTimes;
        private final int threadCount;
        private final long batchStartTime;
        private volatile boolean running = true;
        
        public ThreadProgressMonitor(int totalReports, AtomicInteger completed, 
                                   Map<Integer, String> threadStatus, Map<Integer, Long> threadStartTimes,
                                   int threadCount, long batchStartTime) {
            this.totalReports = totalReports;
            this.completed = completed;
            this.threadStatus = threadStatus;
            this.threadStartTimes = threadStartTimes;
            this.threadCount = threadCount;
            this.batchStartTime = batchStartTime;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(100); // Initial delay
                
                while (running && completed.get() < totalReports) {
                    clearScreen();
                    
                    System.out.println("\nBATCH PROCESSING STATUS");
                    System.out.println(createUnderline(50));
                    
                    // Display thread status with progress bars
                    for (int i = 1; i <= threadCount; i++) {
                        String status = threadStatus.get(i);
                        String progressBar = createThreadProgressBar(status);
                        System.out.printf("Thread-%d: %s %s\n", i, progressBar, status);
                    }
                    
                    System.out.println();
                    
                    // Overall progress
                    int current = completed.get();
                    double percentage = (current * 100.0) / totalReports;
                    String overallBar = createProgressBar((int)(percentage / 5));
                    System.out.printf("Progress: [%-20s] %.0f%% (%d/%d completed)\n", 
                        overallBar, percentage, current, totalReports);
                    
                    // Time statistics
                    long elapsed = System.currentTimeMillis() - batchStartTime;
                    System.out.println("\nTime Statistics:");
                    System.out.println("  Elapsed: " + elapsed + "ms");
                    if (current > 0) {
                        long avgTime = elapsed / current;
                        long remaining = (totalReports - current) * avgTime;
                        System.out.println("  Estimated Remaining: " + remaining + "ms");
                        System.out.println("  Avg Report Time: " + avgTime + "ms");
                        System.out.println("  Throughput: " + String.format("%.1f", (current * 1000.0) / elapsed) + " reports/sec");
                    }
                    
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                // Thread interrupted
            }
        }
        
        private void clearScreen() {
            // Clear screen for better visualization
            for (int i = 0; i < 20; i++) {
                System.out.println();
            }
        }
        
        private String createThreadProgressBar(String status) {
            if (status.contains("in progress")) {
                return "[██████████░░░░░░░░░░]"; // Working
            } else if (status.contains("✓")) {
                return "[████████████████████]"; // Completed
            } else if (status.contains("✗")) {
                return "[░░░░░░░░░░░░░░░░░░░░]"; // Failed
            } else {
                return "[                    ]"; // Idle
            }
        }
        
        private String createProgressBar(int length) {
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < length; i++) {
                bar.append("█");
            }
            for (int i = length; i < 20; i++) {
                bar.append("░");
            }
            return bar.toString();
        }
        
        private String createUnderline(int length) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < length; i++) {
                line.append("_");
            }
            return line.toString();
        }
        
        public void stop() {
            running = false;
        }
    }
    
    public int getMaxThreads() {
        return maxThreads;
    }
}