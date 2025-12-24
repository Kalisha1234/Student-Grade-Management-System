package org.example.service;

import java.util.*;

/**
 * Calculates statistical measures for grade data including mean, median, mode,
 * standard deviation, and grade distribution.
 * 
 * @author Student Grade Management System
 * @version 3.0
 */
public class StatisticsCalculator {

    /**
     * Calculates the arithmetic mean (average) of grades.
     * 
     * @param grades list of grade values
     * @return mean value, or 0.0 if list is empty/null
     */
    public double calculateMean(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        double sum = 0;
        for (Double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    /**
     * Calculates the median (middle value) of grades.
     * 
     * @param grades list of grade values
     * @return median value, or 0.0 if list is empty/null
     */
    public double calculateMedian(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        List<Double> sorted = new ArrayList<>(grades);
        Collections.sort(sorted);

        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2.0;
        } else {
            return sorted.get(size/2);
        }
    }

    /**
     * Calculates the mode (most frequent value) of grades.
     * 
     * @param grades list of grade values
     * @return mode value, or 0.0 if list is empty/null
     */
    public double calculateMode(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        Map<Double, Integer> frequencyMap = new HashMap<>();
        for (Double grade : grades) {
            frequencyMap.put(grade, frequencyMap.getOrDefault(grade, 0) + 1);
        }

        double mode = grades.get(0);
        int maxCount = 0;

        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mode = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mode;
    }

    /**
     * Calculates the standard deviation of grades.
     * 
     * @param grades list of grade values
     * @return standard deviation, or 0.0 if list has fewer than 2 elements
     */
    public double calculateStandardDeviation(List<Double> grades) {
        if (grades == null || grades.size() < 2) return 0.0;

        double mean = calculateMean(grades);
        double sum = 0;

        for (Double grade : grades) {
            sum += Math.pow(grade - mean, 2);
        }

        return Math.sqrt(sum / (grades.size() - 1));
    }

    /**
     * Calculates grade distribution across letter grade categories.
     * 
     * @param grades list of grade values
     * @return map of grade categories to counts
     */
    public Map<String, Integer> calculateGradeDistribution(List<Double> grades) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A (90-100%)", 0);
        distribution.put("B (80-89%)", 0);
        distribution.put("C (70-79%)", 0);
        distribution.put("D (60-69%)", 0);
        distribution.put("F (0-59%)", 0);

        for (Double grade : grades) {
            if (grade >= 90) {
                distribution.put("A (90-100%)", distribution.get("A (90-100%)") + 1);
            } else if (grade >= 80) {
                distribution.put("B (80-89%)", distribution.get("B (80-89%)") + 1);
            } else if (grade >= 70) {
                distribution.put("C (70-79%)", distribution.get("C (70-79%)") + 1);
            } else if (grade >= 60) {
                distribution.put("D (60-69%)", distribution.get("D (60-69%)") + 1);
            } else {
                distribution.put("F (0-59%)", distribution.get("F (0-59%)") + 1);
            }
        }

        return distribution;
    }
}
