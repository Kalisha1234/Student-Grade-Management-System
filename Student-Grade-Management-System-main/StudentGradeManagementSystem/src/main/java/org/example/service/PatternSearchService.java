package org.example.service;

import org.example.models.PatternSearchResult;
import org.example.models.Student;

import java.util.*;
import java.util.regex.*;

public class PatternSearchService {
    private final EnhancedStudentManager studentManager;

    public PatternSearchService(EnhancedStudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public PatternSearchResult searchByEmailDomain(String domainPattern, boolean caseInsensitive) {
        String regex = ".*@" + domainPattern;
        return searchByPattern(regex, "email", caseInsensitive);
    }

    public PatternSearchResult searchByPhoneAreaCode(String areaCode) {
        String regex = "\\+?" + areaCode + "[-\\s].*";
        return searchByPattern(regex, "phone", false);
    }

    public PatternSearchResult searchByStudentIdPattern(String pattern) {
        String regex = pattern.replace("*", ".*").replace("?", ".");
        return searchByPattern(regex, "id", false);
    }

    public PatternSearchResult searchByNamePattern(String namePattern, boolean caseInsensitive) {
        return searchByPattern(namePattern, "name", caseInsensitive);
    }

    public PatternSearchResult searchByCustomPattern(String pattern, String field, boolean caseInsensitive) {
        return searchByPattern(pattern, field, caseInsensitive);
    }

    private PatternSearchResult searchByPattern(String patternStr, String field, boolean caseInsensitive) {
        long startTime = System.currentTimeMillis();
        PatternSearchResult result = new PatternSearchResult(patternStr);

        try {
            int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
            Pattern pattern = Pattern.compile(patternStr, flags);

            List<Student> students = studentManager.getAllStudents();
            result.setTotalScanned(students.size());

            for (Student student : students) {
                String textToSearch = getFieldValue(student, field);
                if (textToSearch == null) continue;

                Matcher matcher = pattern.matcher(textToSearch);
                if (matcher.find()) {
                    String matchedText = matcher.group();
                    String highlighted = highlightMatch(textToSearch, matcher);
                    result.addMatch(student, field, matchedText, highlighted);

                    if (field.equals("email")) {
                        String domain = extractDomain(textToSearch);
                        result.addDistributionStat(domain);
                    } else if (field.equals("phone")) {
                        String areaCode = extractAreaCode(textToSearch);
                        result.addDistributionStat(areaCode);
                    }
                }
            }

        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + e.getMessage());
        }

        result.setSearchTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }

    private String getFieldValue(Student student, String field) {
        switch (field.toLowerCase()) {
            case "email": return student.getEmail();
            case "phone": return student.getPhone();
            case "id": return student.getStudentId();
            case "name": return student.getName();
            default: return null;
        }
    }

    private String highlightMatch(String text, Matcher matcher) {
        int start = matcher.start();
        int end = matcher.end();
        return text.substring(0, start) + ">>>" + text.substring(start, end) + "<<<" + text.substring(end);
    }

    private String extractDomain(String email) {
        int atIndex = email.indexOf('@');
        return atIndex >= 0 ? email.substring(atIndex + 1) : "unknown";
    }

    private String extractAreaCode(String phone) {
        Pattern p = Pattern.compile("\\+?(\\d{1,3})");
        Matcher m = p.matcher(phone);
        return m.find() ? m.group(1) : "unknown";
    }

    public String analyzePatternComplexity(String pattern) {
        if (pattern.contains(".*.*") || pattern.contains(".+.+")) {
            return "HIGH - This pattern may be slow for large datasets";
        } else if (pattern.contains(".*") || pattern.contains(".+")) {
            return "MEDIUM - Pattern uses wildcards";
        } else {
            return "LOW - Simple pattern, fast execution";
        }
    }

    public void displaySearchResults(PatternSearchResult result) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           PATTERN SEARCH RESULTS                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        System.out.println("Pattern: " + result.getPattern());
        System.out.println("Total Scanned: " + result.getTotalScanned() + " students");
        System.out.println("Matches Found: " + result.getMatches().size());
        System.out.println("Search Time: " + result.getSearchTimeMs() + "ms");
        System.out.println();

        if (result.getMatches().isEmpty()) {
            System.out.println("No matches found.");
            return;
        }

        System.out.println("MATCHED STUDENTS:");
        System.out.println("─────────────────────────────────────────────────────────────");

        for (PatternSearchResult.StudentMatch match : result.getMatches()) {
            Student student = match.getStudent();
            System.out.println("\n" + student.getStudentId() + " - " + student.getName());
            System.out.println("  Field: " + match.getField());
            System.out.println("  Match: " + match.getHighlightedText());
            System.out.println("  Type: " + student.getStudentType() + " | GPA: " + 
                String.format("%.1f%%", student.calculateAverageGrade()));
        }

        if (!result.getDistributionStats().isEmpty()) {
            System.out.println("\n─────────────────────────────────────────────────────────────");
            System.out.println("DISTRIBUTION STATISTICS:");
            for (Map.Entry<String, Integer> entry : result.getDistributionStats().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " students");
            }
        }
    }
}
