package org.example.service;

import org.example.models.CacheEntry;
import org.example.models.Student;

import java.util.*;

public class CacheManager {
    private final LRUCache<String, Student> studentCache;
    private final LRUCache<String, String> reportCache;
    private final LRUCache<String, Map<String, Object>> statsCache;
    private final EnhancedStudentManager studentManager;

    public CacheManager(EnhancedStudentManager studentManager) {
        this.studentCache = new LRUCache<>(150);
        this.reportCache = new LRUCache<>(150);
        this.statsCache = new LRUCache<>(150);
        this.studentManager = studentManager;
        
        warmCache();
        startAutoRefresh();
    }

    private void warmCache() {
        System.out.println("Warming cache...");
        List<Student> students = studentManager.getAllStudents();
        for (Student student : students) {
            studentCache.put(student.getStudentId(), student);
        }
        System.out.println("✓ Cache warmed with " + students.size() + " students");
    }

    private void startAutoRefresh() {
        studentCache.startAutoRefresh(() -> {
            System.out.println("[CACHE REFRESH] Refreshing stale entries...");
            List<Student> students = studentManager.getAllStudents();
            for (Student student : students) {
                studentCache.put(student.getStudentId(), student);
            }
        }, 300); // Refresh every 5 minutes
    }

    public Student getStudent(String studentId) {
        Student cached = studentCache.get(studentId);
        if (cached == null) {
            cached = studentManager.findStudent(studentId);
            if (cached != null) {
                studentCache.put(studentId, cached);
            }
        }
        return cached;
    }

    public void invalidateStudent(String studentId) {
        studentCache.invalidate(studentId);
        reportCache.invalidate(studentId + "_report");
    }
    
    public void putStudent(String studentId, Student student) {
        studentCache.put(studentId, student);
    }

    public void putReport(String studentId, String report) {
        reportCache.put(studentId + "_report", report);
    }

    public String getReport(String studentId) {
        return reportCache.get(studentId + "_report");
    }

    public void putStats(String key, Map<String, Object> stats) {
        statsCache.put(key, stats);
    }

    public Map<String, Object> getStats(String key) {
        return statsCache.get(key);
    }

    public void clearAll() {
        studentCache.clear();
        reportCache.clear();
        statsCache.clear();
        System.out.println("✓ All caches cleared");
    }

    public void displayStatistics() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CACHE STATISTICS DASHBOARD                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        displayCacheStats("STUDENT CACHE", studentCache);
        System.out.println();
        displayCacheStats("REPORT CACHE", reportCache);
        System.out.println();
        displayCacheStats("STATISTICS CACHE", statsCache);
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("OVERALL CACHE SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        int totalEntries = studentCache.size() + reportCache.size() + statsCache.size();
        long totalMemory = studentCache.getMemoryUsage() + reportCache.getMemoryUsage() + statsCache.getMemoryUsage();
        
        System.out.println("Total Entries: " + totalEntries + " / 450");
        System.out.println("Total Memory: " + formatBytes(totalMemory));
        System.out.println("Cache Utilization: " + String.format("%.1f%%", (totalEntries * 100.0) / 450));
    }

    private void displayCacheStats(String name, LRUCache<?, ?> cache) {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println(name);
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Entries: " + cache.size() + " / 150");
        System.out.println("Hit Rate: " + String.format("%.2f%%", cache.getHitRate()));
        System.out.println("Miss Rate: " + String.format("%.2f%%", cache.getMissRate()));
        System.out.println("Hits: " + cache.getHits());
        System.out.println("Misses: " + cache.getMisses());
        System.out.println("Evictions: " + cache.getEvictionCount());
        System.out.println("Avg Hit Time: " + cache.getAverageHitTime() + " ns");
        System.out.println("Avg Miss Time: " + cache.getAverageMissTime() + " ns");
        System.out.println("Memory Usage: " + formatBytes(cache.getMemoryUsage()));
    }

    public void displayCacheContents() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CACHE CONTENTS                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        System.out.println("STUDENT CACHE (" + studentCache.size() + " entries)");
        System.out.println("─────────────────────────────────────────────────────────────");
        List<CacheEntry<Student>> studentEntries = studentCache.getEntries();
        studentEntries.sort((a, b) -> b.getLastAccessed().compareTo(a.getLastAccessed()));
        
        for (int i = 0; i < Math.min(10, studentEntries.size()); i++) {
            CacheEntry<Student> entry = studentEntries.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue().getName());
            System.out.println("  Last Accessed: " + entry.getLastAccessed());
            System.out.println("  Access Count: " + entry.getAccessCount());
        }
        
        if (studentEntries.size() > 10) {
            System.out.println("... and " + (studentEntries.size() - 10) + " more entries");
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    public void shutdown() {
        studentCache.shutdown();
        reportCache.shutdown();
        statsCache.shutdown();
    }
}
