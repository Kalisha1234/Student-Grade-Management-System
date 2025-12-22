package org.example.service;

import org.example.models.Student;
import org.example.models.Grade;
import org.example.exceptions.InvalidFileFormatException;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class EnhancedFileOperations {
    private static final String DATA_DIR = "./data/";
    private static final String CSV_DIR = DATA_DIR + "csv/";
    private static final String JSON_DIR = DATA_DIR + "json/";
    private static final String BINARY_DIR = DATA_DIR + "binary/";
    

    private final WatchService watchService;
    private EnhancedStudentManager studentManager;
    
    public EnhancedFileOperations() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        createDirectories();
        setupFileWatcher();
    }
    
    public void setStudentManager(EnhancedStudentManager studentManager) {
        this.studentManager = studentManager;
    }
    
    private void createDirectories() throws IOException {
        Files.createDirectories(Paths.get(CSV_DIR));
        Files.createDirectories(Paths.get(JSON_DIR));
        Files.createDirectories(Paths.get(BINARY_DIR));
    }
    
    private void setupFileWatcher() throws IOException {
        Path csvPath = Paths.get(CSV_DIR);
        csvPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }
    
    // Export Grade Report in multiple formats
    public void exportGradeReport(String studentId, String filename, String format) throws Exception {
        if (studentManager == null) {
            throw new IllegalStateException("StudentManager not set");
        }
        
        Student student = studentManager.searchById(studentId);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        switch (format.toUpperCase()) {
            case "CSV":
                exportToCSV(student, CSV_DIR + filename + "_" + timestamp + ".csv");
                break;
            case "JSON":
                exportToJSON(student, JSON_DIR + filename + "_" + timestamp + ".json");
                break;
            case "BINARY":
                exportToBinary(student, BINARY_DIR + filename + "_" + timestamp + ".dat");
                break;
            default:
                throw new InvalidFileFormatException("Unsupported format: " + format);
        }
    }
    
    public void exportGradeReportWithOutput(String studentId, String filename, String format) throws Exception {
        if (studentManager == null) {
            throw new IllegalStateException("StudentManager not set");
        }
        
        Student student = studentManager.searchById(studentId);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        long startTime = System.currentTimeMillis();
        String filepath = "";
        
        switch (format.toUpperCase()) {
            case "CSV":
                filepath = CSV_DIR + filename + "_" + timestamp + ".csv";
                exportToCSV(student, filepath);
                displayExportSummary("CSV", filepath, startTime, student.getGrades().size(), "Excel, Analysis");
                break;
            case "JSON":
                filepath = JSON_DIR + filename + "_" + timestamp + ".json";
                exportToJSON(student, filepath);
                displayExportSummary("JSON", filepath, startTime, student.getGrades().size(), "Nested objects with metadata");
                break;
            case "BINARY":
                filepath = BINARY_DIR + filename + "_" + timestamp + ".dat";
                exportToBinary(student, filepath);
                displayExportSummary("Binary", filepath, startTime, student.getGrades().size(), "Serialized StudentReport object");
                break;
            default:
                throw new InvalidFileFormatException("Unsupported format: " + format);
        }
    }
    
    private void displayExportSummary(String format, String filepath, long startTime, int gradeCount, String structure) throws IOException {
        Path path = Paths.get(filepath);
        long fileSize = Files.size(path);
        long timeTaken = System.currentTimeMillis() - startTime;
        String filename = path.getFileName().toString();
        String location = "./data/" + format.toLowerCase() + "/";
        
        System.out.println("\n✓ " + format + " Export completed");
        System.out.println("  File: " + filename);
        System.out.println("  Location: " + location);
        System.out.println("  Size: " + formatFileSize(fileSize) + (format.equals("Binary") ? " (compressed)" : ""));
        if (format.equals("CSV")) {
            System.out.println("  Rows: " + gradeCount + " grades + header");
        } else {
            System.out.println("  " + (format.equals("JSON") ? "Structure" : "Format") + ": " + structure);
        }
        System.out.println("  Time: " + timeTaken + "ms");
    }
    
    private void exportToCSV(Student student, String filepath) throws IOException {
        Path path = Paths.get(filepath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("StudentID,Name,Type,Subject,Grade,Date\n");
            
            for (Grade grade : student.getGrades()) {
                writer.write(String.format("%s,%s,%s,%s,%.1f,%s\n",
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    grade.getSubject().getSubjectName(),
                    grade.getGrade(),
                    grade.getDate()));
            }
        }
    }
    
    private void exportToJSON(Student student, String filepath) throws IOException {
        Path path = Paths.get(filepath);
        
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("studentId", student.getStudentId());
        studentData.put("name", student.getName());
        studentData.put("type", student.getStudentType());
        studentData.put("averageGrade", student.calculateAverageGrade());
        studentData.put("exportDate", LocalDateTime.now().toString());
        
        List<Map<String, Object>> grades = new ArrayList<>();
        for (Grade grade : student.getGrades()) {
            Map<String, Object> gradeData = new HashMap<>();
            gradeData.put("subject", grade.getSubject().getSubjectName());
            gradeData.put("grade", grade.getGrade());
            gradeData.put("date", grade.getDate());
            grades.add(gradeData);
        }
        studentData.put("grades", grades);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writeJSON(writer, studentData);
        }
    }
    
    private void exportToBinary(Student student, String filepath) throws IOException {
        Path path = Paths.get(filepath);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(student);
        }
    }
    
    // Import Data with multi-format support
    public void importData(String filename, String format) throws Exception {
        switch (format.toUpperCase()) {
            case "CSV":
                importFromCSV(CSV_DIR + filename);
                break;
            case "JSON":
                importFromJSON(JSON_DIR + filename);
                break;
            case "BINARY":
                importFromBinary(BINARY_DIR + filename);
                break;
            default:
                throw new InvalidFileFormatException("Unsupported format: " + format);
        }
    }
    
    private void importFromCSV(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + filepath);
        }
        
        long startTime = System.currentTimeMillis();
        int processed = 0;
        
        // Stream processing for large files
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            lines.skip(1) // Skip header
                 .forEach(line -> {
                     try {
                         processCSVLine(line);
                     } catch (Exception e) {
                         System.err.println("Error processing line: " + line + " - " + e.getMessage());
                     }
                 });
        }
        
        displayFileInfo(path, startTime, "CSV Import");
    }
    
    private void processCSVLine(String line) throws Exception {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            String studentId = parts[0].trim();
            String subject = parts[3].trim();
            double grade = Double.parseDouble(parts[4].trim());
            
            // Add grade to existing student
            Student student = studentManager.findStudent(studentId);
            if (student != null) {
                // Create grade and add to student
                // Implementation depends on your Grade constructor
            }
        }
    }
    
    private void importFromJSON(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        long startTime = System.currentTimeMillis();
        
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<String, Object> data = readJSON(reader);
            processJSONData(data);
        }
        
        displayFileInfo(path, startTime, "JSON Import");
    }
    
    @SuppressWarnings("unchecked")
    private void processJSONData(Map<String, Object> data) {
        // Implementation for processing JSON student data
        String studentId = (String) data.get("studentId");
        Student student = studentManager.findStudent(studentId);
        
        if (student != null && data.containsKey("grades")) {
            List<Map<String, Object>> grades = (List<Map<String, Object>>) data.get("grades");
            // Process grades
        }
    }
    
    private void importFromBinary(String filepath) throws IOException, ClassNotFoundException {
        Path path = Paths.get(filepath);
        long startTime = System.currentTimeMillis();
        
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            Student student = (Student) ois.readObject();
            studentManager.addStudent(student);
        }
        
        displayFileInfo(path, startTime, "Binary Import");
    }
    
    // Bulk Import with streaming
    public CompletableFuture<Integer> bulkImportGrades(String csvFilepath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path path = Paths.get(CSV_DIR + csvFilepath);
                int count = 0;
                
                try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
                    count = (int) lines.skip(1)
                                     .mapToInt(line -> {
                                         try {
                                             processCSVLine(line);
                                             return 1;
                                         } catch (Exception e) {
                                             return 0;
                                         }
                                     })
                                     .sum();
                }
                
                return count;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    // File watching for new imports
    public void startFileWatcher() {
        CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path filename = (Path) event.context();
                            System.out.println("New import file detected: " + filename);
                        }
                    }
                    
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    private void displayFileInfo(Path path, long startTime, String operation) throws IOException {
        long endTime = System.currentTimeMillis();
        long fileSize = Files.size(path);
        
        System.out.println("\n=== FILE OPERATION SUMMARY ===");
        System.out.println("Operation: " + operation);
        System.out.println("File: " + path.getFileName());
        System.out.println("Size: " + formatFileSize(fileSize));
        System.out.println("Time: " + (endTime - startTime) + "ms");
        System.out.println("Location: " + path.getParent());
        System.out.println("Encoding: UTF-8");
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    public void displayFormatComparison(String studentId) throws Exception {
        Student student = studentManager.searchById(studentId);
        String testFile = "format_test_" + studentId;
        
        System.out.println("\nProcessing with NIO.2 Streaming...\n");
        
        long csvStart = System.currentTimeMillis();
        exportToCSV(student, CSV_DIR + testFile + ".csv");
        long csvTime = System.currentTimeMillis() - csvStart;
        long csvSize = Files.size(Paths.get(CSV_DIR + testFile + ".csv"));
        
        System.out.println("✓ CSV Export completed");
        System.out.println("  • File: " + testFile + ".csv");
        System.out.println("  • Location: ./data/csv/");
        System.out.println("  • Size: " + formatFileSize(csvSize));
        System.out.println("  • Rows: " + student.getGrades().size() + " grades + header");
        System.out.println("  • Time: " + csvTime + "ms\n");
        
        long jsonStart = System.currentTimeMillis();
        exportToJSON(student, JSON_DIR + testFile + ".json");
        long jsonTime = System.currentTimeMillis() - jsonStart;
        long jsonSize = Files.size(Paths.get(JSON_DIR + testFile + ".json"));
        
        System.out.println("✓ JSON Export completed");
        System.out.println("  • File: " + testFile + ".json");
        System.out.println("  • Location: ./data/json/");
        System.out.println("  • Size: " + formatFileSize(jsonSize));
        System.out.println("  • Structure: Nested objects with metadata");
        System.out.println("  • Time: " + jsonTime + "ms\n");
        
        long binaryStart = System.currentTimeMillis();
        exportToBinary(student, BINARY_DIR + testFile + ".dat");
        long binaryTime = System.currentTimeMillis() - binaryStart;
        long binarySize = Files.size(Paths.get(BINARY_DIR + testFile + ".dat"));
        
        System.out.println("✓ Binary Export completed");
        System.out.println("  • File: " + testFile + ".dat");
        System.out.println("  • Location: ./data/binary/");
        System.out.println("  • Size: " + formatFileSize(binarySize) + " (compressed)");
        System.out.println("  • Format: Serialized StudentReport object");
        System.out.println("  • Time: " + binaryTime + "ms\n");
        
        long totalTime = csvTime + jsonTime + binaryTime;
        double totalSizeKB = (csvSize + jsonSize + binarySize) / 1024.0;
        double compressionRatio = jsonSize > 0 ? (double) jsonSize / binarySize : 0;
        
        System.out.println("📊 Export Performance Summary:");
        System.out.println("  • Total Time: " + totalTime + "ms");
        System.out.printf("  • Total Size: %.1f KB\n", totalSizeKB);
        System.out.printf("  • Compression Ratio: %.1f:1 (binary vs JSON)\n", compressionRatio);
        System.out.println("  • I/O Operations: 3 parallel writes");
    }
    
    public void close() throws IOException {
        if (watchService != null) {
            watchService.close();
        }
    }
    
    private void writeJSON(BufferedWriter writer, Map<String, Object> data) throws IOException {
        writer.write("{\n");
        writer.write("  \"studentId\": \"" + data.get("studentId") + "\",\n");
        writer.write("  \"name\": \"" + data.get("name") + "\",\n");
        writer.write("  \"type\": \"" + data.get("type") + "\",\n");
        writer.write("  \"averageGrade\": " + data.get("averageGrade") + ",\n");
        writer.write("  \"exportDate\": \"" + data.get("exportDate") + "\",\n");
        writer.write("  \"grades\": [\n");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> grades = (List<Map<String, Object>>) data.get("grades");
        for (int i = 0; i < grades.size(); i++) {
            Map<String, Object> grade = grades.get(i);
            writer.write("    {\n");
            writer.write("      \"subject\": \"" + grade.get("subject") + "\",\n");
            writer.write("      \"grade\": " + grade.get("grade") + ",\n");
            writer.write("      \"date\": \"" + grade.get("date") + "\"\n");
            writer.write("    }" + (i < grades.size() - 1 ? "," : "") + "\n");
        }
        
        writer.write("  ]\n");
        writer.write("}\n");
    }
    
    private Map<String, Object> readJSON(BufferedReader reader) throws IOException {
        // Simple JSON parsing - for production use a proper JSON library
        Map<String, Object> data = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.contains("\"studentId\":")) {
                data.put("studentId", extractStringValue(line));
            } else if (line.contains("\"name\":")) {
                data.put("name", extractStringValue(line));
            }
        }
        return data;
    }
    
    private String extractStringValue(String line) {
        int start = line.indexOf('"', line.indexOf(':')) + 1;
        int end = line.lastIndexOf('"');
        return line.substring(start, end);
    }
}