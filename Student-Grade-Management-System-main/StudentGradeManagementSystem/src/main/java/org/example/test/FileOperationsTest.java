package org.example.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationsTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void testNIO2FileReadingWithVariousSizes() throws IOException {
        System.out.println("\n=== NIO.2 File Reading with Various Sizes ===");
        
        // Small file (1KB)
        Path smallFile = tempDir.resolve("small.txt");
        StringBuilder smallContent = new StringBuilder();
        for (int i = 0; i < 1024; i++) smallContent.append("A");
        Files.write(smallFile, smallContent.toString().getBytes(StandardCharsets.UTF_8));
        
        long startSmall = System.nanoTime();
        byte[] readSmall = Files.readAllBytes(smallFile);
        long timeSmall = System.nanoTime() - startSmall;
        
        assertEquals(1024, readSmall.length);
        System.out.println("Small file (1KB): " + timeSmall + " ns");
        
        // Medium file (100KB)
        Path mediumFile = tempDir.resolve("medium.txt");
        StringBuilder mediumContent = new StringBuilder();
        for (int i = 0; i < 100 * 1024; i++) mediumContent.append("B");
        Files.write(mediumFile, mediumContent.toString().getBytes(StandardCharsets.UTF_8));
        
        long startMedium = System.nanoTime();
        byte[] readMedium = Files.readAllBytes(mediumFile);
        long timeMedium = System.nanoTime() - startMedium;
        
        assertEquals(100 * 1024, readMedium.length);
        System.out.println("Medium file (100KB): " + timeMedium + " ns");
        
        // Large file (1MB)
        Path largeFile = tempDir.resolve("large.txt");
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1024 * 1024; i++) largeContent.append("C");
        Files.write(largeFile, largeContent.toString().getBytes(StandardCharsets.UTF_8));
        
        long startLarge = System.nanoTime();
        byte[] readLarge = Files.readAllBytes(largeFile);
        long timeLarge = System.nanoTime() - startLarge;
        
        assertEquals(1024 * 1024, readLarge.length);
        System.out.println("Large file (1MB): " + timeLarge + " ns");
        
        assertTrue(timeLarge > timeSmall, "Larger files should take more time");
    }
    
    @Test
    void testStreamingVsLoadingEntireFile() throws IOException {
        System.out.println("\n=== Streaming vs Loading Entire File ===");
        
        // Create test file with 10,000 lines
        Path testFile = tempDir.resolve("test_lines.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(testFile)) {
            for (int i = 0; i < 10000; i++) {
                writer.write("Line " + i + "\n");
            }
        }
        
        // Method 1: Load entire file into memory
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memBefore1 = runtime.totalMemory() - runtime.freeMemory();
        
        long startLoad = System.nanoTime();
        List<String> allLines = Files.readAllLines(testFile);
        long timeLoad = System.nanoTime() - startLoad;
        
        long memAfter1 = runtime.totalMemory() - runtime.freeMemory();
        long memLoad = memAfter1 - memBefore1;
        
        assertEquals(10000, allLines.size());
        System.out.println("Load all lines: " + timeLoad + " ns, Memory: " + formatMemory(memLoad));
        
        // Method 2: Stream lines (lazy loading)
        runtime.gc();
        long memBefore2 = runtime.totalMemory() - runtime.freeMemory();
        
        long startStream = System.nanoTime();
        long count;
        try (Stream<String> lines = Files.lines(testFile)) {
            count = lines.count();
        }
        long timeStream = System.nanoTime() - startStream;
        
        long memAfter2 = runtime.totalMemory() - runtime.freeMemory();
        long memStream = memAfter2 - memBefore2;
        
        assertEquals(10000, count);
        System.out.println("Stream lines: " + timeStream + " ns, Memory: " + formatMemory(memStream));
        
        assertTrue(memLoad > 0 && memStream >= 0, "Both methods should use memory");
    }
    
    @Test
    void testConcurrentFileAccessHandling() throws InterruptedException, IOException {
        System.out.println("\n=== Concurrent File Access Handling ===");
        
        Path sharedFile = tempDir.resolve("shared.txt");
        Files.write(sharedFile, "Initial content\n".getBytes(StandardCharsets.UTF_8));
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successfulWrites = new AtomicInteger(0);
        AtomicInteger successfulReads = new AtomicInteger(0);
        
        // Concurrent writes
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String content = "Thread " + threadId + " content\n";
                    Files.write(sharedFile, content.getBytes(StandardCharsets.UTF_8), 
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    successfulWrites.incrementAndGet();
                } catch (IOException e) {
                    // Expected: some writes may fail due to concurrent access
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        // Concurrent reads
        CountDownLatch readLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    byte[] content = Files.readAllBytes(sharedFile);
                    assertNotNull(content);
                    successfulReads.incrementAndGet();
                } catch (IOException e) {
                    // Handle read errors
                } finally {
                    readLatch.countDown();
                }
            });
        }
        
        readLatch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Successful writes: " + successfulWrites.get());
        System.out.println("Successful reads: " + successfulReads.get());
        
        assertTrue(successfulWrites.get() > 0, "Some writes should succeed");
        assertTrue(successfulReads.get() > 0, "Some reads should succeed");
        
        // Verify file exists and has content
        assertTrue(Files.exists(sharedFile));
        assertTrue(Files.size(sharedFile) > 0);
    }
    
    @Test
    void testFileEncodingUTF8Handling() throws IOException {
        System.out.println("\n=== File Encoding UTF-8 Handling ===");
        
        Path utf8File = tempDir.resolve("utf8_test.txt");
        
        // Write UTF-8 content with special characters
        String utf8Content = "Hello World! 你好世界! Привет мир! مرحبا بالعالم! 🎉🎊";
        Files.write(utf8File, utf8Content.getBytes(StandardCharsets.UTF_8));
        
        // Read back with UTF-8 encoding
        byte[] bytes = Files.readAllBytes(utf8File);
        String readContent = new String(bytes, StandardCharsets.UTF_8);
        
        assertEquals(utf8Content, readContent);
        System.out.println("Original: " + utf8Content);
        System.out.println("Read back: " + readContent);
        
        // Test with BufferedReader
        try (BufferedReader reader = Files.newBufferedReader(utf8File, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            assertEquals(utf8Content, line);
        }
        
        System.out.println("UTF-8 encoding test passed");
    }
    
    @Test
    void testMockFileSystemWithoutActualIO() throws IOException {
        System.out.println("\n=== Mock File System Testing ===");
        
        // Use in-memory file system (jimfs library would be ideal, but using temp for simplicity)
        Path mockFile = tempDir.resolve("mock_test.txt");
        
        // Simulate file operations without actual I/O using StringWriter/StringReader
        StringWriter mockWriter = new StringWriter();
        mockWriter.write("Mock line 1\n");
        mockWriter.write("Mock line 2\n");
        mockWriter.write("Mock line 3\n");
        
        String mockContent = mockWriter.toString();
        
        // Verify mock content
        StringReader mockReader = new StringReader(mockContent);
        BufferedReader bufferedReader = new BufferedReader(mockReader);
        
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        
        assertEquals(3, lines.size());
        assertEquals("Mock line 1", lines.get(0));
        assertEquals("Mock line 2", lines.get(1));
        assertEquals("Mock line 3", lines.get(2));
        
        System.out.println("Mock file system test passed");
        System.out.println("Lines read: " + lines.size());
        
        // Alternative: Use actual temp file but clean up immediately
        Files.write(mockFile, mockContent.getBytes(StandardCharsets.UTF_8));
        List<String> actualLines = Files.readAllLines(mockFile);
        assertEquals(lines, actualLines);
        Files.deleteIfExists(mockFile);
        
        assertFalse(Files.exists(mockFile), "Mock file should be deleted");
    }
    
    @Test
    void testNIO2PathOperations() throws IOException {
        System.out.println("\n=== NIO.2 Path Operations ===");
        
        Path testFile = tempDir.resolve("path_test.txt");
        Files.write(testFile, "Test content".getBytes(StandardCharsets.UTF_8));
        
        // Test path operations
        assertTrue(Files.exists(testFile));
        assertTrue(Files.isRegularFile(testFile));
        assertFalse(Files.isDirectory(testFile));
        assertTrue(Files.isReadable(testFile));
        assertTrue(Files.isWritable(testFile));
        
        // Test file attributes
        long size = Files.size(testFile);
        assertEquals(12, size);
        
        // Test file copy
        Path copyFile = tempDir.resolve("copy_test.txt");
        Files.copy(testFile, copyFile);
        assertTrue(Files.exists(copyFile));
        assertEquals(Files.size(testFile), Files.size(copyFile));
        
        // Test file move
        Path movedFile = tempDir.resolve("moved_test.txt");
        Files.move(copyFile, movedFile);
        assertTrue(Files.exists(movedFile));
        assertFalse(Files.exists(copyFile));
        
        // Test file delete
        Files.delete(movedFile);
        assertFalse(Files.exists(movedFile));
        
        System.out.println("Path operations test passed");
    }
    
    @Test
    void testBufferedVsDirectFileIO() throws IOException {
        System.out.println("\n=== Buffered vs Direct File I/O ===");
        
        Path testFile = tempDir.resolve("io_test.txt");
        int lineCount = 10000;
        
        // Buffered write
        long startBuffered = System.nanoTime();
        try (BufferedWriter writer = Files.newBufferedWriter(testFile)) {
            for (int i = 0; i < lineCount; i++) {
                writer.write("Line " + i + "\n");
            }
        }
        long timeBuffered = System.nanoTime() - startBuffered;
        
        // Direct write (unbuffered)
        Path directFile = tempDir.resolve("direct_test.txt");
        long startDirect = System.nanoTime();
        try (OutputStream os = Files.newOutputStream(directFile);
             OutputStreamWriter writer = new OutputStreamWriter(os)) {
            for (int i = 0; i < lineCount; i++) {
                writer.write("Line " + i + "\n");
                writer.flush(); // Force write each line
            }
        }
        long timeDirect = System.nanoTime() - startDirect;
        
        System.out.println("Buffered write: " + (timeBuffered / 1_000_000) + " ms");
        System.out.println("Direct write: " + (timeDirect / 1_000_000) + " ms");
        
        assertTrue(timeBuffered < timeDirect, "Buffered I/O should be faster");
        assertEquals(Files.size(testFile), Files.size(directFile));
    }
    
    private String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
