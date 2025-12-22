package org.example.test;

import org.example.service.StatisticsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsCalculatorTest {
    private StatisticsCalculator statisticsCalculator;

    @BeforeEach
    void setUp() {
        statisticsCalculator = new StatisticsCalculator();
    }

    // Test 1: Calculate Mean (Average)
    @Test
    void testCalculateMean_ValidData() {
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0);
        double mean = statisticsCalculator.calculateMean(grades);
        assertEquals(86.6, mean, 0.01, "Mean calculation is incorrect");
    }

    @Test
    void testCalculateMean_EmptyList() {
        List<Double> emptyList = new ArrayList<>();
        double mean = statisticsCalculator.calculateMean(emptyList);
        assertEquals(0.0, mean, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateMean_NullList() {
        double mean = statisticsCalculator.calculateMean(null);
        assertEquals(0.0, mean, 0.01, "Null list should return 0.0");
    }

    @Test
    void testCalculateMean_SingleElement() {
        List<Double> singleGrade = Collections.singletonList(85.5);
        double mean = statisticsCalculator.calculateMean(singleGrade);
        assertEquals(85.5, mean, 0.01, "Single element mean should be the element itself");
    }

    @Test
    void testCalculateMean_NegativeValues() {
        List<Double> grades = Arrays.asList(-10.0, 20.0, 30.0);
        double mean = statisticsCalculator.calculateMean(grades);
        assertEquals(13.33, mean, 0.01, "Should handle negative values");
    }

    // Test 2: Calculate Median
    @Test
    void testCalculateMedian_OddNumberOfElements() {
        List<Double> grades = Arrays.asList(85.0, 78.0, 88.0, 90.0, 92.0);
        double median = statisticsCalculator.calculateMedian(grades);
        assertEquals(88.0, median, 0.01, "Median for odd count should be middle element");
    }

    @Test
    void testCalculateMedian_EvenNumberOfElements() {
        List<Double> grades = Arrays.asList(85.0, 78.0, 88.0, 90.0);
        double median = statisticsCalculator.calculateMedian(grades);
        assertEquals(86.5, median, 0.01, "Median for even count should be average of two middle elements");
    }

    @Test
    void testCalculateMedian_UnsortedList() {
        List<Double> grades = Arrays.asList(92.0, 78.0, 85.0, 90.0, 88.0);
        double median = statisticsCalculator.calculateMedian(grades);
        assertEquals(88.0, median, 0.01, "Median should work with unsorted lists");
    }

    @Test
    void testCalculateMedian_EmptyList() {
        List<Double> emptyList = new ArrayList<>();
        double median = statisticsCalculator.calculateMedian(emptyList);
        assertEquals(0.0, median, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateMedian_DuplicateValues() {
        List<Double> grades = Arrays.asList(85.0, 85.0, 90.0, 95.0, 95.0);
        double median = statisticsCalculator.calculateMedian(grades);
        assertEquals(90.0, median, 0.01, "Median with duplicates should work correctly");
    }

    // Test 3: Calculate Mode
    @Test
    void testCalculateMode_SingleMode() {
        List<Double> grades = Arrays.asList(85.0, 90.0, 85.0, 78.0, 90.0, 85.0);
        double mode = statisticsCalculator.calculateMode(grades);
        assertEquals(85.0, mode, 0.01, "Mode should be the most frequent value");
    }

    @Test
    void testCalculateMode_MultipleModes() {
        List<Double> grades = Arrays.asList(85.0, 90.0, 85.0, 90.0, 78.0);
        double mode = statisticsCalculator.calculateMode(grades);
        assertEquals(85.0, mode, 0.01, "When multiple modes, should return first encountered");
    }

    @Test
    void testCalculateMode_EmptyList() {
        List<Double> emptyList = new ArrayList<>();
        double mode = statisticsCalculator.calculateMode(emptyList);
        assertEquals(0.0, mode, 0.01, "Empty list should return 0.0");
    }

    // Test 4: Calculate Standard Deviation
    @Test
    void testCalculateStandardDeviation_ValidData() {
        List<Double> grades = Arrays.asList(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        double stdDev = statisticsCalculator.calculateStandardDeviation(grades);
        assertEquals(2.0, stdDev, 0.1, "Standard deviation calculation is incorrect");
    }

    @Test
    void testCalculateStandardDeviation_SingleElement() {
        List<Double> singleGrade = Collections.singletonList(85.0);
        double stdDev = statisticsCalculator.calculateStandardDeviation(singleGrade);
        assertEquals(0.0, stdDev, 0.01, "Single element should return 0.0");
    }

    @Test
    void testCalculateStandardDeviation_EmptyList() {
        List<Double> emptyList = new ArrayList<>();
        double stdDev = statisticsCalculator.calculateStandardDeviation(emptyList);
        assertEquals(0.0, stdDev, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateStandardDeviation_SameValues() {
        List<Double> grades = Arrays.asList(85.0, 85.0, 85.0, 85.0, 85.0);
        double stdDev = statisticsCalculator.calculateStandardDeviation(grades);
        assertEquals(0.0, stdDev, 0.01, "All same values should have 0 standard deviation");
    }

    // Test 5: Calculate Grade Distribution
    @Test
    void testCalculateGradeDistribution_AllCategories() {
        List<Double> grades = Arrays.asList(95.0, 85.0, 75.0, 65.0, 55.0);
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);
        
        assertNotNull(distribution);
        assertEquals(5, distribution.size());
        assertEquals(1, distribution.get("A (90-100%)"));
        assertEquals(1, distribution.get("B (80-89%)"));
        assertEquals(1, distribution.get("C (70-79%)"));
        assertEquals(1, distribution.get("D (60-69%)"));
        assertEquals(1, distribution.get("F (0-59%)"));
    }

    @Test
    void testCalculateGradeDistribution_BoundaryValues() {
        List<Double> grades = Arrays.asList(90.0, 80.0, 70.0, 60.0, 59.9);
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);
        
        assertEquals(1, distribution.get("A (90-100%)"));
        assertEquals(1, distribution.get("B (80-89%)"));
        assertEquals(1, distribution.get("C (70-79%)"));
        assertEquals(1, distribution.get("D (60-69%)"));
        assertEquals(1, distribution.get("F (0-59%)"));
    }

    @Test
    void testCalculateGradeDistribution_EmptyList() {
        List<Double> emptyList = new ArrayList<>();
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(emptyList);
        
        assertNotNull(distribution);
        assertEquals(0, distribution.get("A (90-100%)"));
        assertEquals(0, distribution.get("B (80-89%)"));
        assertEquals(0, distribution.get("C (70-79%)"));
        assertEquals(0, distribution.get("D (60-69%)"));
        assertEquals(0, distribution.get("F (0-59%)"));
    }

    @Test
    void testCalculateGradeDistribution_MultipleGradesInSameCategory() {
        List<Double> grades = Arrays.asList(95.0, 92.0, 85.0, 82.0, 75.0);
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);
        
        assertEquals(2, distribution.get("A (90-100%)"));
        assertEquals(2, distribution.get("B (80-89%)"));
        assertEquals(1, distribution.get("C (70-79%)"));
        assertEquals(0, distribution.get("D (60-69%)"));
        assertEquals(0, distribution.get("F (0-59%)"));
    }

    @Test
    void testCalculateGradeDistribution_LargeDataset() {
        List<Double> grades = new ArrayList<>();
        Random random = new Random(42);
        
        for (int i = 0; i < 100; i++) {
            grades.add(random.nextDouble() * 100);
        }
        
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);
        int totalCount = distribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(100, totalCount, "Total count should equal number of grades");
        assertTrue(distribution.values().stream().allMatch(count -> count >= 0));
    }

    @Test
    void testStatisticalConsistency() {
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0, 95.0, 82.0, 79.0, 91.0, 86.0);
        
        double mean = statisticsCalculator.calculateMean(grades);
        double median = statisticsCalculator.calculateMedian(grades);
        double mode = statisticsCalculator.calculateMode(grades);
        double stdDev = statisticsCalculator.calculateStandardDeviation(grades);
        
        assertTrue(mean >= 0 && mean <= 100, "Mean should be between 0 and 100");
        assertTrue(median >= 0 && median <= 100, "Median should be between 0 and 100");
        assertTrue(mode >= 0 && mode <= 100, "Mode should be between 0 and 100");
        assertTrue(stdDev >= 0, "Standard deviation should be non-negative");
        
        double meanMedianDiff = Math.abs(mean - median);
        assertTrue(meanMedianDiff < 10.0, "Mean and median should be relatively close");
    }

    @Test
    void testPerformance_LargeDataset() {
        List<Double> largeDataset = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            largeDataset.add(random.nextDouble() * 100);
        }
        
        long startTime = System.nanoTime();
        
        statisticsCalculator.calculateMean(largeDataset);
        statisticsCalculator.calculateMedian(largeDataset);
        statisticsCalculator.calculateMode(largeDataset);
        statisticsCalculator.calculateStandardDeviation(largeDataset);
        statisticsCalculator.calculateGradeDistribution(largeDataset);
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        assertTrue(duration < 5_000_000_000L, "All statistical calculations for 10,000 grades should complete in under 5 seconds");
    }

    static Stream<Arguments> meanTestData() {
        return Stream.of(
                Arguments.of(Arrays.asList(100.0, 100.0, 100.0), 100.0),
                Arguments.of(Arrays.asList(0.0, 0.0, 0.0), 0.0),
                Arguments.of(Arrays.asList(50.0, 100.0), 75.0),
                Arguments.of(Arrays.asList(33.33, 66.67), 50.0)
        );
    }

    @ParameterizedTest
    @MethodSource("meanTestData")
    void testCalculateMean_Parameterized(List<Double> grades, double expectedMean) {
        double actualMean = statisticsCalculator.calculateMean(grades);
        assertEquals(expectedMean, actualMean, 0.01);
    }

    @Test
    void testRealWorldScenario_ClassGrades() {
        List<Double> classGrades = Arrays.asList(
                95.0, 92.0, 88.0, 85.0, 82.0, 78.0, 75.0, 72.0, 68.0, 65.0,
                62.0, 58.0, 55.0, 92.0, 89.0, 86.0, 83.0, 79.0, 76.0, 73.0
        );
        
        double mean = statisticsCalculator.calculateMean(classGrades);
        double median = statisticsCalculator.calculateMedian(classGrades);
        double mode = statisticsCalculator.calculateMode(classGrades);
        double stdDev = statisticsCalculator.calculateStandardDeviation(classGrades);
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(classGrades);
        
        assertTrue(mean > 70 && mean < 85, "Class average should be reasonable");
        assertTrue(median > 70 && median < 85, "Class median should be reasonable");
        assertTrue(stdDev > 5 && stdDev < 20, "Standard deviation should be reasonable");
        
        int total = distribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(20, total, "Distribution should account for all 20 grades");
    }
}