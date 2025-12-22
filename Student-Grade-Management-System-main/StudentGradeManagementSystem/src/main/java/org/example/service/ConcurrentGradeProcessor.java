package org.example.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ConcurrentGradeProcessor {
    private final ExecutorService executor;
    private final StatisticsCalculator statisticsCalculator;

    public ConcurrentGradeProcessor() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.statisticsCalculator = new StatisticsCalculator();
    }

    public CompletableFuture<Double> calculateMeanAsync(List<Double> grades) {
        return CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateMean(grades), executor);
    }

    public CompletableFuture<Double> calculateMedianAsync(List<Double> grades) {
        return CompletableFuture.supplyAsync(() -> statisticsCalculator.calculateMedian(grades), executor);
    }

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