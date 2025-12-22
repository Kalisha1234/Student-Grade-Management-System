package org.example.service;

import java.util.List;

public class BatchReportResult {
    private final List<ReportResult> results;
    private final long totalTimeMs;
    private final int threadsUsed;
    private final int totalReports;
    
    public BatchReportResult(List<ReportResult> results, long totalTimeMs, int threadsUsed, int totalReports) {
        this.results = results;
        this.totalTimeMs = totalTimeMs;
        this.threadsUsed = threadsUsed;
        this.totalReports = totalReports;
    }
    
    public List<ReportResult> getResults() {
        return results;
    }
    
    public long getTotalTimeMs() {
        return totalTimeMs;
    }
    
    public int getThreadsUsed() {
        return threadsUsed;
    }
    
    public int getTotalReports() {
        return totalReports;
    }
    
    public int getSuccessfulReports() {
        return (int) results.stream().filter(ReportResult::isSuccess).count();
    }
    
    public int getFailedReports() {
        return totalReports - getSuccessfulReports();
    }
}