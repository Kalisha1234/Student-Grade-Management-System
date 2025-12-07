package org.example.test;


import org.example.service.GPACalculator;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GPACalculatorTest {
    private GPACalculator gpaCalculator;

    @BeforeEach
    void setUp() {
        gpaCalculator = new GPACalculator();
    }

    // Test 1: Percentage to GPA Conversion (PDF Grading Scale)
    @ParameterizedTest(name = "Percentage {0}% should convert to GPA {1}")
    @CsvSource({
            "100.0, 4.0",  // A
            "97.0, 4.0",   // A
            "95.0, 4.0",   // A
            "93.0, 4.0",   // A
            "92.9, 3.7",   // A- boundary
            "92.0, 3.7",   // A-
            "91.0, 3.7",   // A-
            "90.0, 3.7",   // A-
            "89.9, 3.3",   // B+ boundary
            "89.0, 3.3",   // B+
            "88.0, 3.3",   // B+
            "87.0, 3.3",   // B+
            "86.9, 3.0",   // B boundary
            "85.0, 3.0",   // B
            "84.0, 3.0",   // B
            "83.0, 3.0",   // B
            "82.9, 2.7",   // B- boundary
            "82.0, 2.7",   // B-
            "81.0, 2.7",   // B-
            "80.0, 2.7",   // B-
            "79.9, 2.3",   // C+ boundary
            "78.0, 2.3",   // C+
            "77.0, 2.3",   // C+
            "76.9, 2.0",   // C boundary
            "75.0, 2.0",   // C
            "74.0, 2.0",   // C
            "73.0, 2.0",   // C
            "72.9, 1.7",   // C- boundary
            "72.0, 1.7",   // C-
            "71.0, 1.7",   // C-
            "70.0, 1.7",   // C-
            "69.9, 1.3",   // D+ boundary
            "68.0, 1.3",   // D+
            "67.0, 1.3",   // D+
            "66.9, 1.0",   // D boundary
            "65.0, 1.0",   // D
            "63.0, 1.0",   // D
            "60.0, 1.0",   // D
            "59.9, 0.0",   // F boundary
            "50.0, 0.0",   // F
            "30.0, 0.0",   // F
            "0.0, 0.0"     // F
    })
    void testConvertPercentageToGPA(double percentage, double expectedGPA) {
        // When
        double result = gpaCalculator.convertPercentageToGPA(percentage);

        // Then
        assertEquals(expectedGPA, result, 0.01,
                String.format("Percentage %.1f%% should convert to GPA %.1f", percentage, expectedGPA));
    }

    // Test 2: Percentage to Letter Grade Conversion
    @ParameterizedTest(name = "Percentage {0}% should be letter grade {1}")
    @CsvSource({
            "100.0, A",
            "95.0, A",
            "93.0, A",
            "92.9, A-",
            "92.0, A-",
            "90.0, A-",
            "89.9, B+",
            "88.0, B+",
            "87.0, B+",
            "86.9, B",
            "85.0, B",
            "83.0, B",
            "82.9, B-",
            "81.0, B-",
            "80.0, B-",
            "79.9, C+",
            "78.0, C+",
            "77.0, C+",
            "76.9, C",
            "75.0, C",
            "73.0, C",
            "72.9, C-",
            "71.0, C-",
            "70.0, C-",
            "69.9, D+",
            "68.0, D+",
            "67.0, D+",
            "66.9, D",
            "65.0, D",
            "63.0, D",
            "60.0, D",
            "59.9, F",
            "50.0, F",
            "0.0, F"
    })
    void testConvertPercentageToLetterGrade(double percentage, String expectedLetter) {
        // When
        String result = gpaCalculator.convertPercentageToLetterGrade(percentage);

        // Then
        assertEquals(expectedLetter, result,
                String.format("Percentage %.1f%% should be letter grade %s", percentage, expectedLetter));
    }

    // Test 3: Calculate Cumulative GPA with valid grades
    @Test
    void testCalculateCumulativeGPA_ValidGrades() {
        // Given
        List<Double> grades = Arrays.asList(95.0, 87.0, 92.0, 78.0, 85.0);

        // When
        double result = gpaCalculator.calculateCumulativeGPA(grades);

        // Then
        // Expected: (4.0 + 3.3 + 3.7 + 2.3 + 3.0) / 5 = 16.3 / 5 = 3.26
        assertEquals(3.26, result, 0.01, "Cumulative GPA calculation is incorrect");
    }

    // Test 4: Calculate Cumulative GPA with empty list
    @Test
    void testCalculateCumulativeGPA_EmptyList() {
        // Given
        List<Double> emptyGrades = new ArrayList<>();

        // When
        double result = gpaCalculator.calculateCumulativeGPA(emptyGrades);

        // Then
        assertEquals(0.0, result, 0.01, "Empty list should return 0.0 GPA");
    }

    // Test 5: Calculate Cumulative GPA with null list
    @Test
    void testCalculateCumulativeGPA_NullList() {
        // When
        double result = gpaCalculator.calculateCumulativeGPA(null);

        // Then
        assertEquals(0.0, result, 0.01, "Null list should return 0.0 GPA");
    }

    // Test 6: Calculate Cumulative GPA with all A grades (4.0)
    @Test
    void testCalculateCumulativeGPA_AllPerfectScores() {
        // Given
        List<Double> perfectGrades = Arrays.asList(100.0, 95.0, 98.0, 100.0, 93.0);

        // When
        double result = gpaCalculator.calculateCumulativeGPA(perfectGrades);

        // Then
        assertEquals(4.0, result, 0.01, "All A grades should give 4.0 GPA");
    }

    // Test 7: Calculate Cumulative GPA with all F grades (0.0)
    @Test
    void testCalculateCumulativeGPA_AllFailingScores() {
        // Given
        List<Double> failingGrades = Arrays.asList(45.0, 50.0, 30.0, 55.0, 40.0);

        // When
        double result = gpaCalculator.calculateCumulativeGPA(failingGrades);

        // Then
        assertEquals(0.0, result, 0.01, "All F grades should give 0.0 GPA");
    }

    // Test 8: Calculate Cumulative GPA with mixed grades including boundary values
    @Test
    void testCalculateCumulativeGPA_BoundaryValues() {
        // Given: grades at exact boundaries
        List<Double> boundaryGrades = Arrays.asList(93.0, 90.0, 87.0, 83.0, 80.0, 77.0, 73.0, 70.0, 67.0, 60.0);

        // When
        double result = gpaCalculator.calculateCumulativeGPA(boundaryGrades);

        // Then: (4.0 + 3.7 + 3.3 + 3.0 + 2.7 + 2.3 + 2.0 + 1.7 + 1.3 + 1.0) / 10
        double expected = (4.0 + 3.7 + 3.3 + 3.0 + 2.7 + 2.3 + 2.0 + 1.7 + 1.3 + 1.0) / 10;
        assertEquals(expected, result, 0.01,
                String.format("Boundary grades GPA should be %.2f", expected));
    }

    // Test 9: Verify GPA scale consistency (all values between 0-100)
    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -1.0, 101.0, 150.0})
    void testConvertPercentageToGPA_OutOfRange(double invalidPercentage) {
        // When & Then - Should handle gracefully (return 0.0 for < 60, 4.0 for > 100)
        double result = gpaCalculator.convertPercentageToGPA(invalidPercentage);

        if (invalidPercentage < 0) {
            assertEquals(0.0, result, 0.01, "Negative percentage should return 0.0 GPA");
        } else if (invalidPercentage > 100) {
            assertEquals(4.0, result, 0.01, "Percentage > 100 should return 4.0 GPA");
        }
    }

    // Test 10: Test with decimal percentages
    @Test
    void testConvertPercentageToGPA_DecimalValues() {
        assertEquals(4.0, gpaCalculator.convertPercentageToGPA(93.5), 0.01);
        assertEquals(3.7, gpaCalculator.convertPercentageToGPA(90.1), 0.01);
        assertEquals(3.3, gpaCalculator.convertPercentageToGPA(87.3), 0.01);
        assertEquals(3.0, gpaCalculator.convertPercentageToGPA(83.7), 0.01);
        assertEquals(2.7, gpaCalculator.convertPercentageToGPA(80.2), 0.01);
        assertEquals(2.3, gpaCalculator.convertPercentageToGPA(77.8), 0.01);
        assertEquals(2.0, gpaCalculator.convertPercentageToGPA(73.9), 0.01);
        assertEquals(1.7, gpaCalculator.convertPercentageToGPA(70.5), 0.01);
        assertEquals(1.3, gpaCalculator.convertPercentageToGPA(67.1), 0.01);
        assertEquals(1.0, gpaCalculator.convertPercentageToGPA(60.9), 0.01);
        assertEquals(0.0, gpaCalculator.convertPercentageToGPA(59.5), 0.01);
    }

    // Test 11: Performance test - large list of grades
    @Test
    void testCalculateCumulativeGPA_LargeDataset() {
        // Given: 1000 random grades between 0-100
        List<Double> largeDataset = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeDataset.add((double) (i % 101)); // 0 to 100
        }

        // When
        long startTime = System.nanoTime();
        double result = gpaCalculator.calculateCumulativeGPA(largeDataset);
        long endTime = System.nanoTime();

        // Then
        assertTrue(result >= 0.0 && result <= 4.0, "GPA should be between 0.0 and 4.0");

        // Performance check (should complete in reasonable time)
        long duration = endTime - startTime;
        assertTrue(duration < 1_000_000_000L, // 1 second
                "GPA calculation for 1000 grades should complete in under 1 second");
    }
}
