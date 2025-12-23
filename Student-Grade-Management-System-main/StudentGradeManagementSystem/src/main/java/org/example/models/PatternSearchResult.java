package org.example.models;

import java.util.*;

public class PatternSearchResult {
    private List<StudentMatch> matches;
    private int totalScanned;
    private long searchTimeMs;
    private String pattern;
    private Map<String, Integer> distributionStats;

    public PatternSearchResult(String pattern) {
        this.pattern = pattern;
        this.matches = new ArrayList<>();
        this.distributionStats = new HashMap<>();
    }

    public void addMatch(Student student, String field, String matchedText, String highlightedText) {
        matches.add(new StudentMatch(student, field, matchedText, highlightedText));
    }

    public List<StudentMatch> getMatches() { return matches; }
    public int getTotalScanned() { return totalScanned; }
    public long getSearchTimeMs() { return searchTimeMs; }
    public String getPattern() { return pattern; }
    public Map<String, Integer> getDistributionStats() { return distributionStats; }

    public void setTotalScanned(int totalScanned) { this.totalScanned = totalScanned; }
    public void setSearchTimeMs(long searchTimeMs) { this.searchTimeMs = searchTimeMs; }
    public void addDistributionStat(String key) {
        distributionStats.put(key, distributionStats.getOrDefault(key, 0) + 1);
    }

    public static class StudentMatch {
        private Student student;
        private String field;
        private String matchedText;
        private String highlightedText;

        public StudentMatch(Student student, String field, String matchedText, String highlightedText) {
            this.student = student;
            this.field = field;
            this.matchedText = matchedText;
            this.highlightedText = highlightedText;
        }

        public Student getStudent() { return student; }
        public String getField() { return field; }
        public String getMatchedText() { return matchedText; }
        public String getHighlightedText() { return highlightedText; }
    }
}
