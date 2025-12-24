package org.example.service;

import org.example.models.Student;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Concurrent grade processor using ExecutorService for parallel statistical calculations.
 * Provides async methods for mean, median, and comprehensive statistics.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class ConcurrentGradeProcessor {
    private final ExecutorService executor;
    private final StatisticsCalculator statisticsCalculator;

    public ConcurrentGradeProcessor() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.statisticsCalculator = new StatisticsCalculator();
    }

    /**
     * Calculates mean asynchronously.
     * 
     * @param grades list of grades
     * @return CompletableFuture with mean value
     */
    public CompletableFuture<Double> calculateMeanAsync(List<Double> grades) {
        return CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateMean(grades), executor);
    }

    /**
     * Calculates median asynchronously.
     * 
     * @param grades list of grades
     * @return CompletableFuture with median value
     */
    public CompletableFuture<Double> calculateMedianAsync(List<Double> grades) {
        return CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateMedian(grades), executor);
    }

    /**
     * Processes all statistics concurrently.
     * 
     * @param grades list of grades
     * @return CompletableFuture with map of all statistics
     */
    public CompletableFuture<Map<String, Object>> processAllStatisticsAsync(List<Double> grades) {
        CompletableFuture<Double> meanFuture = calculateMeanAsync(grades);
        CompletableFuture<Double> medianFuture = calculateMedianAsync(grades);
        CompletableFuture<Double> modeFuture = CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateMode(grades), executor);
        CompletableFuture<Double> stdDevFuture = CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateStandardDeviation(grades), executor);

        return CompletableFuture.allOf(meanFuture, medianFuture, modeFuture, stdDevFuture)
                .thenApply(v -> {
                    Map<String, Object> results = new HashMap<>();
                    results.put("mean", meanFuture.join());
                    results.put("median", medianFuture.join());
                    results.put("mode", modeFuture.join());
                    results.put("standardDeviation", stdDevFuture.join());
                    return results;
                });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}