package org.example.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CacheEntry<T> implements Serializable {
    private final String key;
    private final T value;
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    private int accessCount;

    public CacheEntry(String key, T value) {
        this.key = key;
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
        this.accessCount = 0;
    }

    public void recordAccess() {
        this.lastAccessed = LocalDateTime.now();
        this.accessCount++;
    }

    public String getKey() { return key; }
    public T getValue() { return value; }
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getAccessCount() { return accessCount; }
}
