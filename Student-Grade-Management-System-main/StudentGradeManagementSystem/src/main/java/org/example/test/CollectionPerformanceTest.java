package org.example.test;

import org.example.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CollectionPerformanceTest {
    private List<Student> studentList;
    private Map<String, Student> studentMap;
    private TreeMap<String, Student> studentTreeMap;
    private Set<String> studentIdSet;
    
    @BeforeEach
    void setUp() {
        studentList = new ArrayList<>();
        studentMap = new HashMap<>();
        studentTreeMap = new TreeMap<>();
        studentIdSet = new HashSet<>();
        
        // Create 10,000 students for performance testing
        String[] firstNames = {"John", "Jane", "Bob", "Alice", "Tom", "Mary", "David", "Sarah", "Mike", "Lisa"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson"};
        
        for (int i = 0; i < 10000; i++) {
            String name = firstNames[i % 10] + " " + lastNames[(i / 10) % 10];
            String phone = String.format("%03d-%03d-%04d", 555, (i % 900) + 100, i % 10000);
            Student student = new RegularStudent(name, 20, "student" + i + "@test.edu", phone);
            studentList.add(student);
            studentMap.put(student.getStudentId(), student);
            studentTreeMap.put(student.getStudentId(), student);
            studentIdSet.add(student.getStudentId());
        }
    }
    
    @Test
    void testHashMapLookupVsArrayListSearch() {
        System.out.println("\n=== HashMap Lookup vs ArrayList Search ===");
        
        String searchId = studentList.get(5000).getStudentId(); // Middle element
        
        // HashMap lookup - O(1)
        long hashMapStart = System.nanoTime();
        Student hashMapResult = studentMap.get(searchId);
        long hashMapTime = System.nanoTime() - hashMapStart;
        
        // ArrayList search - O(n)
        long arrayListStart = System.nanoTime();
        Student arrayListResult = studentList.stream()
            .filter(s -> s.getStudentId().equals(searchId))
            .findFirst()
            .orElse(null);
        long arrayListTime = System.nanoTime() - arrayListStart;
        
        System.out.println("HashMap lookup time: " + hashMapTime + " ns");
        System.out.println("ArrayList search time: " + arrayListTime + " ns");
        System.out.println("HashMap is " + (arrayListTime / hashMapTime) + "x faster");
        
        assertNotNull(hashMapResult);
        assertNotNull(arrayListResult);
        assertEquals(hashMapResult.getStudentId(), arrayListResult.getStudentId());
        assertTrue(hashMapTime < arrayListTime, "HashMap should be faster than ArrayList");
    }
    
    @Test
    void testTreeMapSortingPerformance() {
        System.out.println("\n=== TreeMap Sorting Performance ===");
        
        // TreeMap maintains sorted order - O(log n) insertion
        long treeMapStart = System.nanoTime();
        TreeMap<String, Student> sortedMap = new TreeMap<>();
        for (Student s : studentList) {
            sortedMap.put(s.getStudentId(), s);
        }
        long treeMapTime = System.nanoTime() - treeMapStart;
        
        // HashMap + manual sort - O(n log n)
        long hashMapSortStart = System.nanoTime();
        HashMap<String, Student> unsortedMap = new HashMap<>();
        for (Student s : studentList) {
            unsortedMap.put(s.getStudentId(), s);
        }
        List<String> sortedKeys = new ArrayList<>(unsortedMap.keySet());
        Collections.sort(sortedKeys);
        long hashMapSortTime = System.nanoTime() - hashMapSortStart;
        
        System.out.println("TreeMap insertion (auto-sorted): " + treeMapTime + " ns");
        System.out.println("HashMap + manual sort: " + hashMapSortTime + " ns");
        
        // Verify TreeMap is sorted
        List<String> treeMapKeys = new ArrayList<>(sortedMap.keySet());
        assertEquals(treeMapKeys, sortedKeys);
        assertTrue(isSorted(treeMapKeys), "TreeMap keys should be sorted");
    }
    
    @Test
    void testHashSetUniquenessGuarantees() {
        System.out.println("\n=== HashSet Uniqueness Guarantees ===");
        
        Set<String> uniqueIds = new HashSet<>();
        List<String> duplicateIds = new ArrayList<>();
        
        // Add IDs including duplicates
        for (int i = 0; i < 1000; i++) {
            uniqueIds.add("STU" + (i % 100)); // Creates duplicates
            duplicateIds.add("STU" + (i % 100));
        }
        
        System.out.println("List with duplicates size: " + duplicateIds.size());
        System.out.println("HashSet unique size: " + uniqueIds.size());
        
        assertEquals(1000, duplicateIds.size());
        assertEquals(100, uniqueIds.size());
        
        // Verify no duplicates in HashSet
        Set<String> verifySet = new HashSet<>(uniqueIds);
        assertEquals(uniqueIds.size(), verifySet.size(), "HashSet should maintain uniqueness");
        
        // Test add duplicate returns false
        assertFalse(uniqueIds.add("STU0"), "Adding duplicate should return false");
        assertTrue(uniqueIds.add("STU999"), "Adding new element should return true");
    }
    
    @Test
    void testBigOComplexityEmpirical() {
        System.out.println("\n=== Big-O Complexity Empirical Measurement ===");
        
        int[] sizes = {1000, 2000, 4000, 8000};
        
        System.out.println("\nArrayList Search (O(n)):");
        for (int size : sizes) {
            List<Student> list = studentList.subList(0, size);
            String searchId = "STU" + String.format("%05d", size - 1);
            
            long start = System.nanoTime();
            list.stream().filter(s -> s.getStudentId().equals(searchId)).findFirst();
            long time = System.nanoTime() - start;
            
            System.out.printf("Size: %5d, Time: %8d ns\n", size, time);
        }
        
        System.out.println("\nHashMap Lookup (O(1)):");
        for (int size : sizes) {
            Map<String, Student> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                map.put(studentList.get(i).getStudentId(), studentList.get(i));
            }
            String searchId = studentList.get(size - 1).getStudentId();
            
            long start = System.nanoTime();
            map.get(searchId);
            long time = System.nanoTime() - start;
            
            System.out.printf("Size: %5d, Time: %8d ns\n", size, time);
        }
        
        System.out.println("\nTreeMap Lookup (O(log n)):");
        for (int size : sizes) {
            TreeMap<String, Student> treeMap = new TreeMap<>();
            for (int i = 0; i < size; i++) {
                treeMap.put(studentList.get(i).getStudentId(), studentList.get(i));
            }
            String searchId = studentList.get(size - 1).getStudentId();
            
            long start = System.nanoTime();
            treeMap.get(searchId);
            long time = System.nanoTime() - start;
            
            System.out.printf("Size: %5d, Time: %8d ns\n", size, time);
        }
    }
    
    @Test
    void testParallelStreamVsSequentialPerformance() {
        System.out.println("\n=== Parallel Stream vs Sequential Performance ===");
        
        List<Student> testList = studentList.subList(0, 1000);
        
        // Sequential stream
        long sequentialStart = System.nanoTime();
        List<Student> sequentialResult = testList.stream()
            .filter(s -> s.getAge() >= 18)
            .map(s -> {
                // Simulate computation
                double sum = 0;
                for (int i = 0; i < 1000; i++) sum += Math.sqrt(i);
                return s;
            })
            .collect(Collectors.toList());
        long sequentialTime = System.nanoTime() - sequentialStart;
        
        // Parallel stream
        long parallelStart = System.nanoTime();
        List<Student> parallelResult = testList.parallelStream()
            .filter(s -> s.getAge() >= 18)
            .map(s -> {
                // Simulate computation
                double sum = 0;
                for (int i = 0; i < 1000; i++) sum += Math.sqrt(i);
                return s;
            })
            .collect(Collectors.toList());
        long parallelTime = System.nanoTime() - parallelStart;
        
        System.out.println("Sequential time: " + (sequentialTime / 1_000_000) + " ms");
        System.out.println("Parallel time: " + (parallelTime / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) sequentialTime / parallelTime) + "x");
        
        assertEquals(sequentialResult.size(), parallelResult.size());
    }
    
    @Test
    void testCollectionMemoryUsage() {
        System.out.println("\n=== Collection Memory Usage ===");
        
        Runtime runtime = Runtime.getRuntime();
        
        // ArrayList memory
        runtime.gc();
        long beforeArrayList = runtime.totalMemory() - runtime.freeMemory();
        List<Student> arrayList = new ArrayList<>(10000);
        for (int i = 0; i < 10000; i++) {
            arrayList.add(new RegularStudent("Test Student", 20, "test@test.com", "555-123-4567"));
        }
        long afterArrayList = runtime.totalMemory() - runtime.freeMemory();
        long arrayListMemory = afterArrayList - beforeArrayList;
        
        // HashMap memory
        runtime.gc();
        long beforeHashMap = runtime.totalMemory() - runtime.freeMemory();
        Map<String, Student> hashMap = new HashMap<>(10000);
        for (int i = 0; i < 10000; i++) {
            hashMap.put("STU" + i, new RegularStudent("Test Student", 20, "test@test.com", "555-123-4567"));
        }
        long afterHashMap = runtime.totalMemory() - runtime.freeMemory();
        long hashMapMemory = afterHashMap - beforeHashMap;
        
        System.out.println("ArrayList memory: " + formatMemory(arrayListMemory));
        System.out.println("HashMap memory: " + formatMemory(hashMapMemory));
        System.out.println("Memory difference: " + formatMemory(Math.abs(hashMapMemory - arrayListMemory)));
        
        // Note: Memory measurements can vary due to GC and JVM optimizations
        assertTrue(arrayListMemory > 0 && hashMapMemory > 0, "Both collections should use memory");
    }
    
    @Test
    void testConcurrentModification() {
        System.out.println("\n=== Concurrent Modification Test ===");
        
        List<Student> list = new ArrayList<>(studentList.subList(0, 100));
        
        // This should throw ConcurrentModificationException
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Student s : list) {
                if (s.getAge() == 20) {
                    list.remove(s); // Modifying while iterating
                }
            }
        });
        
        // Safe way using iterator
        Iterator<Student> iterator = list.iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            Student s = iterator.next();
            if (s.getAge() == 20) {
                iterator.remove();
                removed++;
            }
        }
        
        System.out.println("Safely removed " + removed + " students using iterator");
        assertTrue(removed > 0);
    }
    
    private boolean isSorted(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
    
    private String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
