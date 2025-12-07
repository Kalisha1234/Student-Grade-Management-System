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
        // Given
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0);

        // When
        double mean = statisticsCalculator.calculateMean(grades);

        // Then: (85+90+78+92+88)/5 = 433/5 = 86.6
        assertEquals(86.6, mean, 0.01, "Mean calculation is incorrect");
    }

    @Test
    void testCalculateMean_EmptyList() {
        // Given
        List<Double> emptyList = new ArrayList<>();

        // When
        double mean = statisticsCalculator.calculateMean(emptyList);

        // Then
        assertEquals(0.0, mean, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateMean_NullList() {
        // When
        double mean = statisticsCalculator.calculateMean(null);

        // Then
        assertEquals(0.0, mean, 0.01, "Null list should return 0.0");
    }

    @Test
    void testCalculateMean_SingleElement() {
        // Given
        List<Double> singleGrade = Collections.singletonList(85.5);

        // When
        double mean = statisticsCalculator.calculateMean(singleGrade);

        // Then
        assertEquals(85.5, mean, 0.01, "Single element mean should be the element itself");
    }

    @Test
    void testCalculateMean_NegativeValues() {
        // Given
        List<Double> grades = Arrays.asList(-10.0, 20.0, 30.0);

        // When
        double mean = statisticsCalculator.calculateMean(grades);

        // Then: (-10+20+30)/3 = 40/3 = 13.33
        assertEquals(13.33, mean, 0.01, "Should handle negative values");
    }

    // Test 2: Calculate Median
    @Test
    void testCalculateMedian_OddNumberOfElements() {
        // Given: 85, 78, 88, 90, 92 → sorted: 78, 85, 88, 90, 92
        List<Double> grades = Arrays.asList(85.0, 78.0, 88.0, 90.0, 92.0);

        // When
        double median = statisticsCalculator.calculateMedian(grades);

        // Then: Middle element (88)
        assertEquals(88.0, median, 0.01, "Median for odd count should be middle element");
    }

    @Test
    void testCalculateMedian_EvenNumberOfElements() {
        // Given: 85, 78, 88, 90 → sorted: 78, 85, 88, 90
        List<Double> grades = Arrays.asList(85.0, 78.0, 88.0, 90.0);

        // When
        double median = statisticsCalculator.calculateMedian(grades);

        // Then: (85 + 88) / 2 = 86.5
        assertEquals(86.5, median, 0.01, "Median for even count should be average of two middle elements");
    }

    @Test
    void testCalculateMedian_UnsortedList() {
        // Given: Unsorted list
        List<Double> grades = Arrays.asList(92.0, 78.0, 85.0, 90.0, 88.0);

        // When
        double median = statisticsCalculator.calculateMedian(grades);

        // Then: Sorted: 78, 85, 88, 90, 92 → median = 88
        assertEquals(88.0, median, 0.01, "Median should work with unsorted lists");
    }

    @Test
    void testCalculateMedian_EmptyList() {
        // Given
        List<Double> emptyList = new ArrayList<>();

        // When
        double median = statisticsCalculator.calculateMedian(emptyList);

        // Then
        assertEquals(0.0, median, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateMedian_DuplicateValues() {
        // Given: 85, 85, 90, 95, 95
        List<Double> grades = Arrays.asList(85.0, 85.0, 90.0, 95.0, 95.0);

        // When
        double median = statisticsCalculator.calculateMedian(grades);

        // Then
        assertEquals(90.0, median, 0.01, "Median with duplicates should work correctly");
    }

    // Test 3: Calculate Mode
    @Test
    void testCalculateMode_SingleMode() {
        // Given: 85 appears 3 times, 90 appears 2 times, 78 appears 1 time
        List<Double> grades = Arrays.asList(85.0, 90.0, 85.0, 78.0, 90.0, 85.0);

        // When
        double mode = statisticsCalculator.calculateMode(grades);

        // Then: Mode should be 85.0 (appears 3 times)
        assertEquals(85.0, mode, 0.01, "Mode should be the most frequent value");
    }

    @Test
    void testCalculateMode_MultipleModes() {
        // Given: Both 85 and 90 appear 2 times
        List<Double> grades = Arrays.asList(85.0, 90.0, 85.0, 90.0, 78.0);

        // When
        double mode = statisticsCalculator.calculateMode(grades);

        // Then: Should return the first mode encountered (85)
        assertEquals(85.0, mode, 0.01, "When multiple modes, should return first encountered");
    }



    @Test
    void testCalculateMode_EmptyList() {
        // Given
        List<Double> emptyList = new ArrayList<>();

        // When
        double mode = statisticsCalculator.calculateMode(emptyList);

        // Then
        assertEquals(0.0, mode, 0.01, "Empty list should return 0.0");
    }


    @Test
    void testCalculateStandardDeviation_SingleElement() {
        // Given
        List<Double> singleGrade = Collections.singletonList(85.0);

        // When
        double stdDev = statisticsCalculator.calculateStandardDeviation(singleGrade);

        // Then: Need at least 2 elements for std dev
        assertEquals(0.0, stdDev, 0.01, "Single element should return 0.0");
    }

    @Test
    void testCalculateStandardDeviation_EmptyList() {
        // Given
        List<Double> emptyList = new ArrayList<>();

        // When
        double stdDev = statisticsCalculator.calculateStandardDeviation(emptyList);

        // Then
        assertEquals(0.0, stdDev, 0.01, "Empty list should return 0.0");
    }

    @Test
    void testCalculateStandardDeviation_SameValues() {
        // Given: All values are the same
        List<Double> grades = Arrays.asList(85.0, 85.0, 85.0, 85.0, 85.0);

        // When
        double stdDev = statisticsCalculator.calculateStandardDeviation(grades);

        // Then: Standard deviation should be 0
        assertEquals(0.0, stdDev, 0.01, "All same values should have 0 standard deviation");
    }

    // Test 5: Calculate Grade Distribution
    @Test
    void testCalculateGradeDistribution_AllCategories() {
        // Given: Grades covering all categories
        List<Double> grades = Arrays.asList(
                95.0,  // A
                85.0,  // B
                75.0,  // C
                65.0,  // D
                55.0   // F
        );

        // When
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);

        // Then
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
        // Given: Grades at exact boundaries
        List<Double> grades = Arrays.asList(90.0, 80.0, 70.0, 60.0, 59.9);

        // When
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);

        // Then
        assertEquals(1, distribution.get("A (90-100%)"));  // 90.0
        assertEquals(1, distribution.get("B (80-89%)"));   // 80.0
        assertEquals(1, distribution.get("C (70-79%)"));   // 70.0
        assertEquals(1, distribution.get("D (60-69%)"));   // 60.0
        assertEquals(1, distribution.get("F (0-59%)"));    // 59.9
    }

    @Test
    void testCalculateGradeDistribution_EmptyList() {
        // Given
        List<Double> emptyList = new ArrayList<>();

        // When
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(emptyList);

        // Then
        assertNotNull(distribution);
        assertEquals(0, distribution.get("A (90-100%)"));
        assertEquals(0, distribution.get("B (80-89%)"));
        assertEquals(0, distribution.get("C (70-79%)"));
        assertEquals(0, distribution.get("D (60-69%)"));
        assertEquals(0, distribution.get("F (0-59%)"));
    }

    @Test
    void testCalculateGradeDistribution_LargeDataset() {
        // Given: 100 grades randomly distributed
        List<Double> grades = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < 100; i++) {
            grades.add(random.nextDouble() * 100);
        }

        // When
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(grades);

        // Then
        int totalCount = distribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(100, totalCount, "Total count should equal number of grades");

        // All counts should be non-negative
        assertTrue(distribution.values().stream().allMatch(count -> count >= 0));
    }


    @Test
    void testStatisticalConsistency() {
        // Given: A normal distribution of grades
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0, 95.0, 82.0, 79.0, 91.0, 86.0);

        // When
        double mean = statisticsCalculator.calculateMean(grades);
        double median = statisticsCalculator.calculateMedian(grades);
        double mode = statisticsCalculator.calculateMode(grades);
        double stdDev = statisticsCalculator.calculateStandardDeviation(grades);

        // Then: Verify logical relationships
        assertTrue(mean >= 0 && mean <= 100, "Mean should be between 0 and 100");
        assertTrue(median >= 0 && median <= 100, "Median should be between 0 and 100");
        assertTrue(mode >= 0 && mode <= 100, "Mode should be between 0 and 100");
        assertTrue(stdDev >= 0, "Standard deviation should be non-negative");

        // Mean and median should be close for symmetric distributions
        double meanMedianDiff = Math.abs(mean - median);
        assertTrue(meanMedianDiff < 10.0, "Mean and median should be relatively close");
    }

    // Test 7: Performance Tests
    @Test
    void testPerformance_LargeDataset() {
        // Given: 10,000 random grades
        List<Double> largeDataset = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            largeDataset.add(random.nextDouble() * 100);
        }

        // When: Time the calculations
        long startTime = System.nanoTime();

        statisticsCalculator.calculateMean(largeDataset);
        statisticsCalculator.calculateMedian(largeDataset);
        statisticsCalculator.calculateMode(largeDataset);
        statisticsCalculator.calculateStandardDeviation(largeDataset);
        statisticsCalculator.calculateGradeDistribution(largeDataset);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        // Then: Should complete in reasonable time
        assertTrue(duration < 5_000_000_000L, // 5 seconds
                "All statistical calculations for 10,000 grades should complete in under 5 seconds");
    }

    // Parameterized test for mean calculation
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

    // Test 8: Test with real-world grade scenarios
    @Test
    void testRealWorldScenario_ClassGrades() {
        // Given: A typical class grade distribution
        List<Double> classGrades = Arrays.asList(
                95.0, 92.0, 88.0, 85.0, 82.0, 78.0, 75.0, 72.0, 68.0, 65.0,
                62.0, 58.0, 55.0, 92.0, 89.0, 86.0, 83.0, 79.0, 76.0, 73.0
        );

        // When
        double mean = statisticsCalculator.calculateMean(classGrades);
        double median = statisticsCalculator.calculateMedian(classGrades);
        double mode = statisticsCalculator.calculateMode(classGrades);
        double stdDev = statisticsCalculator.calculateStandardDeviation(classGrades);
        Map<String, Integer> distribution = statisticsCalculator.calculateGradeDistribution(classGrades);

        // Then: Verify realistic values
        assertTrue(mean > 70 && mean < 85, "Class average should be reasonable");
        assertTrue(median > 70 && median < 85, "Class median should be reasonable");
        assertTrue(stdDev > 5 && stdDev < 20, "Standard deviation should be reasonable");

        // Verify distribution sums to total grades
        int total = distribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(20, total, "Distribution should account for all 20 grades");
    }
}
