package org.example.service;

import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class CSVGenerator {
    
    public static void generateLargeCSV(String filename, int recordCount) throws IOException {
        Path dir = Paths.get("./imports");
        Files.createDirectories(dir);
        Path csvFile = dir.resolve(filename);
        
        System.out.println("Generating " + recordCount + " grade records...");
        
        try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
            writer.write("StudentID,SubjectName,SubjectType,Grade\n");
            
            String[] subjects = {"Mathematics", "English", "Science", "History", "Geography"};
            String[] types = {"Core", "Elective"};
            Random random = new Random(42);
            
            for (int i = 0; i < recordCount; i++) {
                String studentId = "STU" + String.format("%03d", (i % 20) + 1);
                String subject = subjects[i % subjects.length];
                String type = types[i % types.length];
                double grade = 60 + random.nextDouble() * 40;
                
                writer.write(String.format("%s,%s,%s,%.1f\n", studentId, subject, type, grade));
                
                if ((i + 1) % 1000 == 0) {
                    System.out.print(".");
                }
            }
        }
        
        System.out.println("\n✓ CSV generated: " + csvFile);
        System.out.println("  Location: ./imports/" + filename);
        System.out.println("  Records: " + recordCount);
    }
}
