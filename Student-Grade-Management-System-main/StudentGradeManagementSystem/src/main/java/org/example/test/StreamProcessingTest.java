
package org.example.test;

import org.example.models.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class StreamProcessingTest {
    
    private List<Student> students;
    private List<Integer> numbers;
    
    @BeforeEach
    void setUp() {
        students = new ArrayList<>();
        String[] names = {"John Smith", "Jane Doe", "Bob Johnson", "Alice Brown", "Tom Wilson"};
        String[] codes = {"MAT101", "ENG102", "SCI103", "HIS104", "PHY105"};
        for (int i = 0; i < 100; i++) {
            Student student = new RegularStudent(names[i % 5], 18 + (i % 10), "student" + i + "@test.edu", "555-123-4567");
            for (int j = 0; j < 5; j++) {
                Grade grade = new Grade(student.getStudentId(), new CoreSubject("Subject" + j, codes[j]), 50 + (i % 50));
                student.addGrade(grade);
            }
            students.add(student);
        }
        
        numbers = IntStream.range(1, 1001).boxed().collect(Collectors.toList());
    }
    

    @Test
    void testParallelStreamCorrectness() {
        System.out.println("\n=== Parallel Stream Correctness ===");
        
        // Sequential sum
        int sequentialSum = numbers.stream()
            .mapToInt(Integer::intValue)
            .sum();
        
        // Parallel sum
        int parallelSum = numbers.parallelStream()
            .mapToInt(Integer::intValue)
            .sum();
        
        assertEquals(sequentialSum, parallelSum, "Parallel and sequential should produce same result");
        System.out.println("Sequential sum: " + sequentialSum);
        System.out.println("Parallel sum: " + parallelSum);
        
        // Test with filtering
        long sequentialCount = students.stream()
            .filter(s -> s.calculateAverageGrade() >= 70)
            .count();
        
        long parallelCount = students.parallelStream()
            .filter(s -> s.calculateAverageGrade() >= 70)
            .count();
        
        assertEquals(sequentialCount, parallelCount);
        System.out.println("Students with avg >= 70: " + sequentialCount);
        
        // Test with grouping
        Map<Integer, Long> sequentialGrouping = students.stream()
            .collect(Collectors.groupingBy(Student::getAge, Collectors.counting()));
        
        Map<Integer, Long> parallelGrouping = students.parallelStream()
            .collect(Collectors.groupingBy(Student::getAge, Collectors.counting()));
        
        assertEquals(sequentialGrouping, parallelGrouping);
        System.out.println("Age groups: " + sequentialGrouping.size());
    }
    
    @Test
    void testStreamShortCircuitingOperations() {
        System.out.println("\n=== Stream Short-Circuiting Operations ===");
        
        AtomicInteger filterCount = new AtomicInteger(0);
        
        // findFirst - should stop after finding first match
        Optional<Student> firstPassing = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return s.calculateAverageGrade() >= 90;
            })
            .findFirst();
        
        System.out.println("Filter operations for findFirst: " + filterCount.get());
        assertTrue(filterCount.get() < students.size(), "Should short-circuit before processing all elements");
        
        // anyMatch - should stop after finding first match
        filterCount.set(0);
        boolean anyMatch = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return true;
            })
            .anyMatch(s -> s.calculateAverageGrade() >= 90);
        
        System.out.println("Filter operations for anyMatch: " + filterCount.get());
        assertTrue(filterCount.get() <= students.size());
        
        // allMatch - should stop at first non-match
        filterCount.set(0);
        boolean allMatch = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return true;
            })
            .allMatch(s -> s.calculateAverageGrade() >= 50);
        
        System.out.println("Filter operations for allMatch: " + filterCount.get());
        
        // limit - should only process limited elements
        filterCount.set(0);
        List<Student> limited = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return true;
            })
            .limit(10)
            .collect(Collectors.toList());
        
        assertEquals(10, limited.size());
        System.out.println("Filter operations for limit(10): " + filterCount.get());
        assertTrue(filterCount.get() <= 10, "Should only process limited elements");
    }
    
    @Test
    void testLazyEvaluationBehavior() {
        System.out.println("\n=== Lazy Evaluation Behavior ===");
        
        AtomicInteger mapCount = new AtomicInteger(0);
        AtomicInteger filterCount = new AtomicInteger(0);
        
        // Create stream pipeline without terminal operation
        Stream<String> lazyStream = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return s.calculateAverageGrade() >= 70;
            })
            .map(s -> {
                mapCount.incrementAndGet();
                return s.getName();
            });
        
        System.out.println("After pipeline creation - Filter: " + filterCount.get() + ", Map: " + mapCount.get());
        assertEquals(0, filterCount.get(), "Filter should not execute without terminal operation");
        assertEquals(0, mapCount.get(), "Map should not execute without terminal operation");
        
        // Execute terminal operation
        List<String> result = lazyStream.collect(Collectors.toList());
        
        System.out.println("After terminal operation - Filter: " + filterCount.get() + ", Map: " + mapCount.get());
        assertTrue(filterCount.get() > 0, "Filter should execute with terminal operation");
        assertTrue(mapCount.get() > 0, "Map should execute with terminal operation");
        assertTrue(result.size() > 0);
        
        // Test that operations are fused
        filterCount.set(0);
        mapCount.set(0);
        
        long count = students.stream()
            .filter(s -> {
                filterCount.incrementAndGet();
                return s.calculateAverageGrade() >= 70;
            })
            .map(s -> {
                mapCount.incrementAndGet();
                return s.getName();
            })
            .count();
        
        System.out.println("Operations executed: Filter=" + filterCount.get() + ", Map=" + mapCount.get());
        assertEquals(filterCount.get(), students.size(), "Filter should process all elements");
        assertTrue(mapCount.get() <= filterCount.get(), "Map should only process filtered elements");
    }
    
    @Test
    void testSequentialVsParallelPerformance() {
        System.out.println("\n=== Sequential vs Parallel Performance ===");
        
        // Create larger dataset
        List<Integer> largeNumbers = IntStream.range(1, 10001).boxed().collect(Collectors.toList());
        
        // Sequential processing
        long startSeq = System.nanoTime();
        long sequentialSum = largeNumbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> {
                // Simulate computation
                double sum = 0;
                for (int i = 0; i < 100; i++) sum += Math.sqrt(n);
                return n;
            })
            .mapToLong(Integer::longValue)
            .sum();
        long timeSeq = System.nanoTime() - startSeq;
        
        // Parallel processing
        long startPar = System.nanoTime();
        long parallelSum = largeNumbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .map(n -> {
                // Simulate computation
                double sum = 0;
                for (int i = 0; i < 100; i++) sum += Math.sqrt(n);
                return n;
            })
            .mapToLong(Integer::longValue)
            .sum();
        long timePar = System.nanoTime() - startPar;
        
        System.out.println("Sequential time: " + (timeSeq / 1_000_000) + " ms");
        System.out.println("Parallel time: " + (timePar / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) timeSeq / timePar) + "x");
        
        assertEquals(sequentialSum, parallelSum, "Results should be identical");
        
        // Test with students
        long startSeqStudents = System.nanoTime();
        double seqAvg = students.stream()
            .mapToDouble(Student::calculateAverageGrade)
            .average()
            .orElse(0.0);
        long timeSeqStudents = System.nanoTime() - startSeqStudents;
        
        long startParStudents = System.nanoTime();
        double parAvg = students.parallelStream()
            .mapToDouble(Student::calculateAverageGrade)
            .average()
            .orElse(0.0);
        long timeParStudents = System.nanoTime() - startParStudents;
        
        System.out.println("\nStudent processing:");
        System.out.println("Sequential: " + (timeSeqStudents / 1_000_000) + " ms");
        System.out.println("Parallel: " + (timeParStudents / 1_000_000) + " ms");
        
        assertEquals(seqAvg, parAvg, 0.01, "Averages should be equal");
    }
    
    @Test
    void testStreamCollectors() {
        System.out.println("\n=== Stream Collectors ===");
        
        // Collect to List
        List<String> namesList = students.stream()
            .map(Student::getName)
            .collect(Collectors.toList());
        assertEquals(100, namesList.size());
        
        // Collect to Set
        Set<Integer> ageSet = students.stream()
            .map(Student::getAge)
            .collect(Collectors.toSet());
        System.out.println("Unique ages: " + ageSet.size());
        assertTrue(ageSet.size() <= 10);
        
        // Collect to Map
        Map<String, Student> studentMap = students.stream()
            .collect(Collectors.toMap(Student::getStudentId, s -> s));
        assertEquals(100, studentMap.size());
        
        // Grouping by age
        Map<Integer, List<Student>> byAge = students.stream()
            .collect(Collectors.groupingBy(Student::getAge));
        System.out.println("Age groups: " + byAge.size());
        
        // Partitioning by passing grade
        Map<Boolean, List<Student>> partitioned = students.stream()
            .collect(Collectors.partitioningBy(s -> s.calculateAverageGrade() >= 75));
        System.out.println("Passing: " + partitioned.get(true).size());
        System.out.println("Failing: " + partitioned.get(false).size());
        
        // Joining names
        String allNames = students.stream()
            .limit(5)
            .map(Student::getName)
            .collect(Collectors.joining(", "));
        System.out.println("First 5 names: " + allNames);
        assertTrue(allNames.contains("John Smith"));
    }
    
    @Test
    void testStreamStatistics() {
        System.out.println("\n=== Stream Statistics ===");
        
        // IntStream statistics
        IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        
        System.out.println("Count: " + stats.getCount());
        System.out.println("Sum: " + stats.getSum());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
        System.out.println("Average: " + stats.getAverage());
        
        assertEquals(1000, stats.getCount());
        assertEquals(1, stats.getMin());
        assertEquals(1000, stats.getMax());
        
        // DoubleSummaryStatistics for grades
        DoubleSummaryStatistics gradeStats = students.stream()
            .mapToDouble(Student::calculateAverageGrade)
            .summaryStatistics();
        
        System.out.println("\nGrade Statistics:");
        System.out.println("Count: " + gradeStats.getCount());
        System.out.println("Average: " + gradeStats.getAverage());
        System.out.println("Min: " + gradeStats.getMin());
        System.out.println("Max: " + gradeStats.getMax());
        
        assertEquals(100, gradeStats.getCount());
        assertTrue(gradeStats.getAverage() > 0);
    }
}
