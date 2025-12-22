package org.example.test;

import org.example.service.ConcurrentGradeProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentGradeProcessorTest {
    private ConcurrentGradeProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ConcurrentGradeProcessor();
    }

    @AfterEach
    void tearDown() {
        processor.shutdown();
    }

    @Test
    void testCalculateMeanAsync() throws ExecutionException, InterruptedException {
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0);
        
        CompletableFuture<Double> future = processor.calculateMeanAsync(grades);
        Double result = future.get();
        
        assertEquals(86.6, result, 0.01);
    }

    @Test
    void testCalculateMedianAsync() throws ExecutionException, InterruptedException {
        List<Double> grades = Arrays.asList(85.0, 78.0, 88.0, 90.0, 92.0);
        
        CompletableFuture<Double> future = processor.calculateMedianAsync(grades);
        Double result = future.get();
        
        assertEquals(88.0, result, 0.01);
    }

    @Test
    void testProcessAllStatisticsAsync() throws ExecutionException, InterruptedException {
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0);
        
        CompletableFuture<Map<String, Object>> future = processor.processAllStatisticsAsync(grades);
        Map<String, Object> results = future.get();
        
        assertNotNull(results);
        assertEquals(86.6, (Double) results.get("mean"), 0.01);
        assertEquals(88.0, (Double) results.get("median"), 0.01);
        assertTrue(results.containsKey("mode"));
        assertTrue(results.containsKey("standardDeviation"));
    }

    @Test
    void testConcurrentProcessing() throws ExecutionException, InterruptedException {
        List<Double> grades1 = Arrays.asList(85.0, 90.0, 78.0);
        List<Double> grades2 = Arrays.asList(92.0, 88.0, 75.0);
        
        CompletableFuture<Double> future1 = processor.calculateMeanAsync(grades1);
        CompletableFuture<Double> future2 = processor.calculateMeanAsync(grades2);
        
        CompletableFuture<Void> combined = CompletableFuture.allOf(future1, future2);
        combined.get();
        
        assertEquals(84.33, future1.get(), 0.01);
        assertEquals(85.0, future2.get(), 0.01);
    }
}