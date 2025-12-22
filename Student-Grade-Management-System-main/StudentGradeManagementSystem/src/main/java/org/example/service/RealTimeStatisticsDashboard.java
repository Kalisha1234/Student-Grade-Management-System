package org.example.service;

import org.example.models.Student;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RealTimeStatisticsDashboard {
    private final EnhancedStudentManager studentManager;
    private final StatisticsCalculator statisticsCalculator;
    
    // Thread-safe collections and atomics
    private final ConcurrentHashMap<String, Object> cachedStats = new ConcurrentHashMap<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private final AtomicLong lastUpdateTime = new AtomicLong(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheRequests = new AtomicInteger(0);
    
    private Thread backgroundThread;
    private volatile boolean isCalculating = false;
    
    public RealTimeStatisticsDashboard(EnhancedStudentManager studentManager) {
        this.studentManager = studentManager;
        this.statisticsCalculator = new StatisticsCalculator();
    }
    
    public void startDashboard() {
        System.out.println("\n=== REAL-TIME STATISTICS DASHBOARD ===");
        System.out.println("Starting background daemon thread...");
        
        isRunning.set(true);
        backgroundThread = new Thread(this::backgroundCalculationLoop);
        backgroundThread.setDaemon(true);
        backgroundThread.setName("StatsDashboard-Thread");
        backgroundThread.start();
        
        displayDashboard();
    }
    
    private void backgroundCalculationLoop() {
        while (isRunning.get()) {
            try {
                if (!isPaused.get()) {
                    isCalculating = true;
                    calculateStatistics();
                    lastUpdateTime.set(System.currentTimeMillis());
                    isCalculating = false;
                }
                Thread.sleep(5000); // Update every 5 seconds
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void calculateStatistics() {
        List<Student> students = studentManager.getAllStudents();
        List<Double> allGrades = new ArrayList<>();
        
        // Collect all grades
        for (Student student : students) {
            for (org.example.models.Grade grade : student.getGrades()) {
                allGrades.add(grade.getGrade());
            }
        }
        
        // Calculate statistics
        if (!allGrades.isEmpty()) {
            cachedStats.put("totalStudents", students.size());
            cachedStats.put("totalGrades", allGrades.size());
            cachedStats.put("mean", statisticsCalculator.calculateMean(allGrades));
            cachedStats.put("median", statisticsCalculator.calculateMedian(allGrades));
            cachedStats.put("mode", statisticsCalculator.calculateMode(allGrades));
            cachedStats.put("stdDev", statisticsCalculator.calculateStandardDeviation(allGrades));
            cachedStats.put("gradeDistribution", statisticsCalculator.calculateGradeDistribution(allGrades));
            cachedStats.put("topPerformers", getTopPerformers(students));
        }
    }
    
    private List<String> getTopPerformers(List<Student> students) {
        return students.stream()
            .filter(s -> !s.getGrades().isEmpty())
            .sorted((s1, s2) -> Double.compare(s2.calculateAverageGrade(), s1.calculateAverageGrade()))
            .limit(3)
            .map(s -> s.getName() + " (" + String.format("%.1f", s.calculateAverageGrade()) + "%)")
            .collect(java.util.stream.Collectors.toList());
    }
    
    private void displayDashboard() {
        Scanner scanner = new Scanner(System.in);
        
        while (isRunning.get()) {
            clearScreen();
            displayHeader();
            displayStatistics();
            displayThreadStatus();
            displayControls();
            
            System.out.print("Command: ");
            String input = scanner.nextLine().toLowerCase();
            
            switch (input) {
                case "r":
                    // Manual refresh - force immediate update
                    break;
                case "p":
                    togglePause();
                    break;
                case "a":
                    addStudentQuick();
                    break;
                case "g":
                    recordGradeQuick();
                    break;
                case "q":
                    stopDashboard();
                    return;
                default:
                    // Auto-refresh every second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
            }
        }
    }
    
    private void displayHeader() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 REAL-TIME STATISTICS DASHBOARD              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private void displayStatistics() {
        cacheRequests.incrementAndGet();
        
        if (isCalculating) {
            System.out.println("📊 Loading... (Calculating statistics)");
            System.out.println();
            return;
        }
        
        if (cachedStats.isEmpty()) {
            System.out.println("📊 No data available yet. Waiting for first calculation...");
            System.out.println();
            return;
        }
        
        cacheHits.incrementAndGet();
        
        // Display cached statistics
        System.out.println("📊 CLASS OVERVIEW");
        System.out.println(createLine(50));
        System.out.printf("Total Students: %d\n", (Integer) cachedStats.get("totalStudents"));
        System.out.printf("Total Grades: %d\n", (Integer) cachedStats.get("totalGrades"));
        System.out.printf("Class Average: %.1f%%\n", (Double) cachedStats.get("mean"));
        System.out.printf("Median: %.1f%%\n", (Double) cachedStats.get("median"));
        System.out.printf("Standard Deviation: %.1f%%\n", (Double) cachedStats.get("stdDev"));
        System.out.println();
        
        // Grade Distribution
        System.out.println("📈 GRADE DISTRIBUTION");
        System.out.println(createLine(50));
        @SuppressWarnings("unchecked")
        Map<String, Integer> distribution = (Map<String, Integer>) cachedStats.get("gradeDistribution");
        if (distribution != null) {
            int totalGrades = (Integer) cachedStats.get("totalGrades");
            for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
                double percentage = totalGrades > 0 ? (entry.getValue() * 100.0) / totalGrades : 0;
                String bar = createProgressBar((int) (percentage / 5));
                System.out.printf("%-12s: [%-20s] %3d (%4.1f%%)\n", 
                    entry.getKey(), bar, entry.getValue(), percentage);
            }
        }
        System.out.println();
        
        // Top Performers
        System.out.println("🏆 TOP PERFORMERS");
        System.out.println(createLine(50));
        @SuppressWarnings("unchecked")
        List<String> topPerformers = (List<String>) cachedStats.get("topPerformers");
        if (topPerformers != null && !topPerformers.isEmpty()) {
            for (int i = 0; i < topPerformers.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, topPerformers.get(i));
            }
        } else {
            System.out.println("No student data available");
        }
        System.out.println();
    }
    
    private void displayThreadStatus() {
        System.out.println("🔧 SYSTEM STATUS");
        System.out.println(createLine(50));
        
        String threadStatus = getThreadStatus();
        System.out.printf("Background Thread: %s\n", threadStatus);
        System.out.printf("Active Threads: %d\n", Thread.activeCount());
        
        long lastUpdate = lastUpdateTime.get();
        if (lastUpdate > 0) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date(lastUpdate));
            long secondsAgo = (System.currentTimeMillis() - lastUpdate) / 1000;
            System.out.printf("Last Update: %s (%ds ago)\n", timestamp, secondsAgo);
        } else {
            System.out.println("Last Update: Never");
        }
        
        // Performance metrics
        double hitRate = cacheRequests.get() > 0 ? 
            (cacheHits.get() * 100.0) / cacheRequests.get() : 0;
        System.out.printf("Cache Hit Rate: %.1f%% (%d/%d)\n", 
            hitRate, cacheHits.get(), cacheRequests.get());
        System.out.println();
    }
    
    private String getThreadStatus() {
        if (!isRunning.get()) return "STOPPED";
        if (isPaused.get()) return "PAUSED";
        if (isCalculating) return "CALCULATING";
        return "RUNNING";
    }
    
    private void displayControls() {
        System.out.println("⌨️  CONTROLS");
        System.out.println(createLine(50));
        System.out.println("[R] Manual Refresh  [P] Pause/Resume  [A] Add Student  [G] Record Grade  [Q] Quit");
        System.out.println("Auto-refresh: Every 1 second | Background update: Every 5 seconds");
        System.out.println();
    }
    
    private void togglePause() {
        boolean wasPaused = isPaused.get();
        isPaused.set(!wasPaused);
        System.out.println(wasPaused ? "Dashboard RESUMED" : "Dashboard PAUSED");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
    
    private void stopDashboard() {
        System.out.println("\nStopping dashboard...");
        isRunning.set(false);
        
        if (backgroundThread != null && backgroundThread.isAlive()) {
            backgroundThread.interrupt();
            try {
                backgroundThread.join(2000); // Wait up to 2 seconds
            } catch (InterruptedException e) {
                // Force stop if needed
                backgroundThread.interrupt();
            }
        }
        
        System.out.println("✓ Dashboard stopped successfully");
    }
    
    private void clearScreen() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }
    
    private String createProgressBar(int length) {
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < Math.min(length, 20); i++) {
            bar.append("█");
        }
        for (int i = length; i < 20; i++) {
            bar.append("░");
        }
        return bar.toString();
    }
    
    private String createLine(int length) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append("─");
        }
        return line.toString();
    }
    
    private void addStudentQuick() {
        System.out.println("\n⚡ QUICK ADD STUDENT");
        System.out.print("Name: ");
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();
        
        System.out.print("Age: ");
        int age = input.nextInt();
        input.nextLine(); // consume newline
        
        System.out.print("Email: ");
        String email = input.nextLine();
        
        System.out.print("Phone: ");
        String phone = input.nextLine();
        
        System.out.print("Type (1=Regular, 2=Honors): ");
        int type = input.nextInt();
        
        try {
            org.example.models.Student student;
            if (type == 2) {
                student = new org.example.models.HonorsStudent(name, age, email, phone);
            } else {
                student = new org.example.models.RegularStudent(name, age, email, phone);
            }
            studentManager.addStudent(student);
            System.out.println("✓ Student added: " + student.getStudentId());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        input.nextLine();
    }
    
    private void recordGradeQuick() {
        System.out.println("\n⚡ QUICK RECORD GRADE");
        Scanner input = new Scanner(System.in);
        
        System.out.print("Student ID: ");
        String studentId = input.nextLine();
        
        System.out.print("Subject (Math/English/Science/Art/Music): ");
        String subjectName = input.nextLine();
        
        System.out.print("Grade (0-100): ");
        double gradeValue = input.nextDouble();
        
        try {
            org.example.models.Subject subject;
            String courseCode = subjectName.substring(0, 3).toUpperCase() + "101";
            
            if (subjectName.toLowerCase().contains("math") || 
                subjectName.toLowerCase().contains("english") || 
                subjectName.toLowerCase().contains("science")) {
                subject = new org.example.models.CoreSubject(subjectName, courseCode);
            } else {
                subject = new org.example.models.ElectiveSubject(subjectName, courseCode);
            }
            
            org.example.models.Grade grade = new org.example.models.Grade(studentId, subject, gradeValue);
            studentManager.addGradeToStudent(studentId, grade);
            System.out.println("✓ Grade recorded successfully!");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        input.nextLine();
        input.nextLine();
    }
}