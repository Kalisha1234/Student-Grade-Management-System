package org.example.test;

import org.example.models.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {
    
    @Test
    void testThreadSafeCollectionsUnderConcurrentLoad() throws InterruptedException {
        System.out.println("\n=== Thread-Safe Collections Under Concurrent Load ===");
        
        ConcurrentHashMap<String, Student> safeMap = new ConcurrentHashMap<>();
        Map<String, Student> unsafeMap = new HashMap<>();
        
        int threadCount = 10;
        int operationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // Test ConcurrentHashMap (thread-safe)
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "STU" + (threadId * operationsPerThread + j);
                        Student student = new RegularStudent("John Smith", 20, id + "@test.edu", "555-123-4567");
                        safeMap.put(id, student);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("ConcurrentHashMap size: " + safeMap.size());
        assertEquals(threadCount * operationsPerThread, safeMap.size(), "All elements should be inserted");
        
        // Test HashMap (not thread-safe) - expect issues
        ExecutorService executor2 = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch2 = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor2.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "STU" + (threadId * operationsPerThread + j);
                        Student student = new RegularStudent("Jane Doe", 20, id + "@test.edu", "555-123-4567");
                        unsafeMap.put(id, student);
                    }
                } catch (Exception e) {
                    // Expected: ConcurrentModificationException or other issues
                } finally {
                    latch2.countDown();
                }
            });
        }
        
        latch2.await();
        executor2.shutdown();
        executor2.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("HashMap size: " + unsafeMap.size());
        System.out.println("Expected: " + (threadCount * operationsPerThread));
        assertTrue(unsafeMap.size() <= threadCount * operationsPerThread, "HashMap may lose data under concurrent access");
    }
    
    @Test
    void testRaceConditionsInUnsynchronizedCode() throws InterruptedException {
        System.out.println("\n=== Race Conditions in Unsynchronized Code ===");
        
        // Unsynchronized counter (race condition)
        class UnsafeCounter {
            private int count = 0;
            public void increment() { count++; }
            public int getCount() { return count; }
        }
        
        // Synchronized counter (no race condition)
        class SafeCounter {
            private int count = 0;
            public synchronized void increment() { count++; }
            public synchronized int getCount() { return count; }
        }
        
        // Atomic counter (no race condition)
        AtomicInteger atomicCounter = new AtomicInteger(0);
        
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        SafeCounter safeCounter = new SafeCounter();
        
        int threadCount = 10;
        int incrementsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * 3);
        
        // Test all three counters concurrently
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        unsafeCounter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        safeCounter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        atomicCounter.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        int expected = threadCount * incrementsPerThread;
        System.out.println("Expected count: " + expected);
        System.out.println("Unsafe counter: " + unsafeCounter.getCount());
        System.out.println("Safe counter: " + safeCounter.getCount());
        System.out.println("Atomic counter: " + atomicCounter.get());
        
        assertTrue(unsafeCounter.getCount() < expected, "Unsafe counter should lose increments due to race condition");
        assertEquals(expected, safeCounter.getCount(), "Safe counter should have exact count");
        assertEquals(expected, atomicCounter.get(), "Atomic counter should have exact count");
    }
    
    @Test
    void testDeadlockScenariosAndPrevention() throws InterruptedException {
        System.out.println("\n=== Deadlock Scenarios and Prevention ===");
        
        Object lock1 = new Object();
        Object lock2 = new Object();
        
        // Deadlock prevention using ordered locking
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger preventedOperations = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        
        executor.submit(() -> {
            try {
                synchronized (lock1) {
                    Thread.sleep(10);
                    synchronized (lock2) {
                        preventedOperations.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                latch.countDown();
            }
        });
        
        executor.submit(() -> {
            try {
                synchronized (lock1) {  // Same order as thread1
                    Thread.sleep(10);
                    synchronized (lock2) {
                        preventedOperations.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                latch.countDown();
            }
        });
        
        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Operations completed (ordered locking): " + preventedOperations.get());
        assertEquals(2, preventedOperations.get(), "Ordered locking should prevent deadlock");
    }
    
    @Test
    void testProperThreadPoolShutdown() throws InterruptedException {
        System.out.println("\n=== Proper Thread Pool Shutdown ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        // Submit tasks
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                    completedTasks.incrementAndGet();
                    System.out.println("Task " + taskId + " completed");
                } catch (InterruptedException e) {
                    System.out.println("Task " + taskId + " interrupted");
                }
            });
        }
        
        // Graceful shutdown
        executor.shutdown();
        System.out.println("Shutdown initiated");
        
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Terminated: " + terminated);
        System.out.println("Completed tasks: " + completedTasks.get());
        
        assertTrue(terminated, "Executor should terminate gracefully");
        assertEquals(10, completedTasks.get(), "All tasks should complete");
        assertTrue(executor.isShutdown(), "Executor should be shutdown");
        assertTrue(executor.isTerminated(), "Executor should be terminated");
        
        // Test shutdownNow (immediate)
        ExecutorService executor2 = Executors.newFixedThreadPool(5);
        AtomicInteger interruptedTasks = new AtomicInteger(0);
        
        for (int i = 0; i < 10; i++) {
            executor2.submit(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    interruptedTasks.incrementAndGet();
                }
            });
        }
        
        Thread.sleep(100);
        List<Runnable> pendingTasks = executor2.shutdownNow();
        executor2.awaitTermination(2, TimeUnit.SECONDS);
        
        System.out.println("Interrupted tasks: " + interruptedTasks.get());
        System.out.println("Pending tasks: " + pendingTasks.size());
        
        assertTrue(interruptedTasks.get() > 0 || pendingTasks.size() > 0, "shutdownNow should interrupt or cancel tasks");
    }
    
    @Test
    void testCacheConsistencyUnderConcurrentAccess() throws InterruptedException {
        System.out.println("\n=== Cache Consistency Under Concurrent Access ===");
        
        ConcurrentHashMap<String, Student> cache = new ConcurrentHashMap<>();
        AtomicInteger cacheHits = new AtomicInteger(0);
        AtomicInteger cacheMisses = new AtomicInteger(0);
        
        int threadCount = 20;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // Concurrent cache operations
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Random random = new Random(threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        String id = "STU" + random.nextInt(50);
                        
                        // Read from cache
                        Student student = cache.get(id);
                        if (student != null) {
                            cacheHits.incrementAndGet();
                        } else {
                            cacheMisses.incrementAndGet();
                            // Simulate loading from database
                            student = new RegularStudent("Bob Johnson", 20, id + "@test.edu", "555-123-4567");
                            cache.put(id, student);
                        }
                        
                        // Verify consistency
                        Student cached = cache.get(id);
                        assertNotNull(cached, "Cache should contain the student");
                        assertEquals(id + "@test.edu", cached.getEmail(), "Cached data should be consistent");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Cache size: " + cache.size());
        System.out.println("Cache hits: " + cacheHits.get());
        System.out.println("Cache misses: " + cacheMisses.get());
        System.out.println("Hit ratio: " + String.format("%.2f%%", cacheHits.get() * 100.0 / (cacheHits.get() + cacheMisses.get())));
        
        assertTrue(cache.size() <= 50, "Cache should contain at most 50 unique students");
        assertTrue(cacheHits.get() > 0, "Should have cache hits");
        assertTrue(cacheMisses.get() > 0, "Should have cache misses");
        assertEquals(threadCount * operationsPerThread, cacheHits.get() + cacheMisses.get(), "Total operations should match");
    }
}
