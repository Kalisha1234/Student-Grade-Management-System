package org.example.service;

import org.example.models.CacheEntry;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class LRUCache<K, V> {
    private final int maxSize;
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final ConcurrentLinkedDeque<K> accessOrder;
    
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);
    private final AtomicLong evictions = new AtomicLong(0);
    private final AtomicLong totalHitTime = new AtomicLong(0);
    private final AtomicLong totalMissTime = new AtomicLong(0);
    
    private final ScheduledExecutorService refreshScheduler;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new ConcurrentHashMap<>(maxSize);
        this.accessOrder = new ConcurrentLinkedDeque<>();
        this.refreshScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public V get(K key) {
        long startTime = System.nanoTime();
        CacheEntry<V> entry = cache.get(key);
        
        if (entry != null) {
            entry.recordAccess();
            updateAccessOrder(key);
            hits.incrementAndGet();
            totalHitTime.addAndGet(System.nanoTime() - startTime);
            return entry.getValue();
        } else {
            misses.incrementAndGet();
            totalMissTime.addAndGet(System.nanoTime() - startTime);
            return null;
        }
    }

    public void put(K key, V value) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            evictLRU();
        }
        
        cache.put(key, new CacheEntry<>(key.toString(), value));
        updateAccessOrder(key);
    }

    private void updateAccessOrder(K key) {
        accessOrder.remove(key);
        accessOrder.addLast(key);
    }

    private void evictLRU() {
        K lruKey = accessOrder.pollFirst();
        if (lruKey != null) {
            cache.remove(lruKey);
            evictions.incrementAndGet();
        }
    }

    public void invalidate(K key) {
        cache.remove(key);
        accessOrder.remove(key);
    }

    public void clear() {
        cache.clear();
        accessOrder.clear();
        hits.set(0);
        misses.set(0);
        evictions.set(0);
        totalHitTime.set(0);
        totalMissTime.set(0);
    }

    public double getHitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0 : (hits.get() * 100.0) / total;
    }

    public double getMissRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0 : (misses.get() * 100.0) / total;
    }

    public long getAverageHitTime() {
        long hitCount = hits.get();
        return hitCount == 0 ? 0 : totalHitTime.get() / hitCount;
    }

    public long getAverageMissTime() {
        long missCount = misses.get();
        return missCount == 0 ? 0 : totalMissTime.get() / missCount;
    }

    public int size() {
        return cache.size();
    }

    public long getEvictionCount() {
        return evictions.get();
    }

    public long getHits() {
        return hits.get();
    }

    public long getMisses() {
        return misses.get();
    }

    public long getMemoryUsage() {
        return cache.size() * 1024; // Rough estimate
    }

    public List<CacheEntry<V>> getEntries() {
        return new ArrayList<>(cache.values());
    }

    public void startAutoRefresh(Runnable refreshTask, long intervalSeconds) {
        refreshScheduler.scheduleAtFixedRate(refreshTask, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public void shutdown() {
        refreshScheduler.shutdown();
    }
}
