package org.example.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegexValidationTest {
    
    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Phone pattern (formats: 123-456-7890, (123) 456-7890, +1-123-456-7890)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$");
    
    // Student ID pattern (STU followed by 3-5 digits)
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3,5}$");
    
    // Name pattern (letters, spaces, hyphens, apostrophes)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'-]*$");
    
    // Grade pattern (0-100 with optional decimal)
    private static final Pattern GRADE_PATTERN = Pattern.compile("^(100(\\.0{1,2})?|\\d{1,2}(\\.\\d{1,2})?)$");
    
    @ParameterizedTest
    @ValueSource(strings = {
        "test@example.com",
        "user.name@domain.com",
        "first.last@company.co.uk",
        "email+tag@test.org",
        "user_123@test-domain.com",
        "a@b.co",
        "test.email.with.dots@example.com",
        "user-name@domain-name.com",
        "123@test.com",
        "test@sub.domain.com",
        "user@test123.com"
    })
    void testEmailPattern_ValidInputs(String email) {
        assertTrue(EMAIL_PATTERN.matcher(email).matches(), "Should accept valid email: " + email);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "invalid.email",
        "@example.com",
        "user@",
        "user @example.com",
        "user@.com",
        "user@domain",
        "",
        "user@domain.c",
        "user name@example.com",
        "user#@example.com",
        "user@domain@com"
    })
    void testEmailPattern_InvalidInputs(String email) {
        assertFalse(EMAIL_PATTERN.matcher(email).matches(), "Should reject invalid email: " + email);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "123-456-7890",
        "(123) 456-7890",
        "+1-123-456-7890",
        "1234567890",
        "+1 123 456 7890",
        "(555)555-5555",
        "555-555-5555",
        "+44-123-456-7890",
        "+1-555-555-5555",
        "(800) 555-1234",
        "800-555-1234"
    })
    void testPhonePattern_ValidInputs(String phone) {
        assertTrue(PHONE_PATTERN.matcher(phone).matches(), "Should accept valid phone: " + phone);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "123-456",
        "12-345-6789",
        "abc-def-ghij",
        "123 456 789",
        "+1-123-456",
        "(123)456-789",
        "123-456-78901",
        "",
        "phone",
        "123.456.7890",
        "123_456_7890"
    })
    void testPhonePattern_InvalidInputs(String phone) {
        assertFalse(PHONE_PATTERN.matcher(phone).matches(), "Should reject invalid phone: " + phone);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "STU001",
        "STU123",
        "STU999",
        "STU1234",
        "STU12345",
        "STU100",
        "STU500",
        "STU9999",
        "STU00001",
        "STU12000",
        "STU99999"
    })
    void testStudentIdPattern_ValidInputs(String id) {
        assertTrue(STUDENT_ID_PATTERN.matcher(id).matches(), "Should accept valid student ID: " + id);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "STU12",
        "STU123456",
        "stu123",
        "STU",
        "123",
        "STUD123",
        "STU-123",
        "STU 123",
        "",
        "STU12A",
        "STUDENT001"
    })
    void testStudentIdPattern_InvalidInputs(String id) {
        assertFalse(STUDENT_ID_PATTERN.matcher(id).matches(), "Should reject invalid student ID: " + id);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "John",
        "Mary Jane",
        "O'Brien",
        "Jean-Pierre",
        "Mary-Ann Smith",
        "John O'Connor",
        "Anne-Marie",
        "D'Angelo",
        "Van Der Berg",
        "Al-Rashid",
        "Maria Garcia"
    })
    void testNamePattern_ValidInputs(String name) {
        assertTrue(NAME_PATTERN.matcher(name).matches(), "Should accept valid name: " + name);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "123John",
        "John123",
        "John@Smith",
        "John_Smith",
        "John.Smith",
        "",
        " John",
        "John!",
        "John#Smith",
        "John&Smith",
        "John*Smith"
    })
    void testNamePattern_InvalidInputs(String name) {
        assertFalse(NAME_PATTERN.matcher(name).matches(), "Should reject invalid name: " + name);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "50",
        "100",
        "85.5",
        "90.25",
        "100.0",
        "100.00",
        "75.75",
        "0.5",
        "99.99",
        "1.1"
    })
    void testGradePattern_ValidInputs(String grade) {
        assertTrue(GRADE_PATTERN.matcher(grade).matches(), "Should accept valid grade: " + grade);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "101",
        "100.01",
        "-5",
        "abc",
        "50.5.5",
        "",
        "100.000",
        "50.",
        ".5",
        "1000",
        "50 points"
    })
    void testGradePattern_InvalidInputs(String grade) {
        assertFalse(GRADE_PATTERN.matcher(grade).matches(), "Should reject invalid grade: " + grade);
    }
    
    @Test
    void testEdgeCases_EmptyStrings() {
        System.out.println("\n=== Edge Cases: Empty Strings ===");
        
        assertFalse(EMAIL_PATTERN.matcher("").matches(), "Empty email should be invalid");
        assertFalse(PHONE_PATTERN.matcher("").matches(), "Empty phone should be invalid");
        assertFalse(STUDENT_ID_PATTERN.matcher("").matches(), "Empty student ID should be invalid");
        assertFalse(NAME_PATTERN.matcher("").matches(), "Empty name should be invalid");
        assertFalse(GRADE_PATTERN.matcher("").matches(), "Empty grade should be invalid");
        
        System.out.println("All empty string tests passed");
    }
    
    @Test
    void testEdgeCases_SpecialCharacters() {
        System.out.println("\n=== Edge Cases: Special Characters ===");
        
        // Email with special characters
        assertTrue(EMAIL_PATTERN.matcher("user+tag@example.com").matches());
        assertTrue(EMAIL_PATTERN.matcher("user_name@example.com").matches());
        assertTrue(EMAIL_PATTERN.matcher("user.name@example.com").matches());
        assertFalse(EMAIL_PATTERN.matcher("user@exam ple.com").matches());
        assertFalse(EMAIL_PATTERN.matcher("user#name@example.com").matches());
        
        // Phone with special characters
        assertTrue(PHONE_PATTERN.matcher("+1-123-456-7890").matches());
        assertTrue(PHONE_PATTERN.matcher("(123) 456-7890").matches());
        assertFalse(PHONE_PATTERN.matcher("123.456.7890").matches());
        assertFalse(PHONE_PATTERN.matcher("123_456_7890").matches());
        
        // Name with special characters
        assertTrue(NAME_PATTERN.matcher("O'Brien").matches());
        assertTrue(NAME_PATTERN.matcher("Jean-Pierre").matches());
        assertFalse(NAME_PATTERN.matcher("John@Smith").matches());
        assertFalse(NAME_PATTERN.matcher("John_Smith").matches());
        
        System.out.println("All special character tests passed");
    }
    
    @Test
    void testEdgeCases_Whitespace() {
        System.out.println("\n=== Edge Cases: Whitespace ===");
        
        assertFalse(EMAIL_PATTERN.matcher(" test@example.com").matches());
        assertFalse(EMAIL_PATTERN.matcher("test@example.com ").matches());
        assertFalse(EMAIL_PATTERN.matcher("test @example.com").matches());
        
        assertFalse(PHONE_PATTERN.matcher(" 123-456-7890").matches());
        assertFalse(PHONE_PATTERN.matcher("123-456-7890 ").matches());
        
        assertFalse(STUDENT_ID_PATTERN.matcher(" STU123").matches());
        assertFalse(STUDENT_ID_PATTERN.matcher("STU123 ").matches());
        assertFalse(STUDENT_ID_PATTERN.matcher("STU 123").matches());
        
        assertTrue(NAME_PATTERN.matcher("John Smith").matches());
        assertFalse(NAME_PATTERN.matcher(" John Smith").matches());
        
        System.out.println("All whitespace tests passed");
    }
    
    @Test
    void testPatternCompilationPerformance() {
        System.out.println("\n=== Pattern Compilation Performance ===");
        
        int iterations = 10000;
        
        // Test pre-compiled pattern (fast)
        long startPrecompiled = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            EMAIL_PATTERN.matcher("test@example.com").matches();
        }
        long timePrecompiled = System.nanoTime() - startPrecompiled;
        
        // Test inline compilation (slow)
        long startInline = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matcher("test@example.com").matches();
        }
        long timeInline = System.nanoTime() - startInline;
        
        System.out.println("Pre-compiled pattern: " + (timePrecompiled / 1_000_000) + " ms");
        System.out.println("Inline compilation: " + (timeInline / 1_000_000) + " ms");
        System.out.println("Speedup: " + String.format("%.2f", (double) timeInline / timePrecompiled) + "x");
        
        assertTrue(timePrecompiled < timeInline, "Pre-compiled pattern should be faster");
    }
    
    @Test
    void testCapturingGroups_PhoneNumber() {
        System.out.println("\n=== Capturing Groups: Phone Number ===");
        
        Pattern phoneWithGroups = Pattern.compile("^(\\+\\d{1,3}[- ]?)?(\\(?\\d{3}\\)?)[- ]?(\\d{3})[- ]?(\\d{4})$");
        
        // Test with country code
        Matcher matcher1 = phoneWithGroups.matcher("+1-123-456-7890");
        assertTrue(matcher1.matches());
        assertEquals("+1-", matcher1.group(1));
        assertEquals("123", matcher1.group(2));
        assertEquals("456", matcher1.group(3));
        assertEquals("7890", matcher1.group(4));
        System.out.println("Extracted: Country=" + matcher1.group(1) + ", Area=" + matcher1.group(2) + 
            ", Prefix=" + matcher1.group(3) + ", Line=" + matcher1.group(4));
        
        // Test without country code
        Matcher matcher2 = phoneWithGroups.matcher("(555) 123-4567");
        assertTrue(matcher2.matches());
        assertNull(matcher2.group(1));
        assertEquals("(555)", matcher2.group(2));
        assertEquals("123", matcher2.group(3));
        assertEquals("4567", matcher2.group(4));
        System.out.println("Extracted: Area=" + matcher2.group(2) + ", Prefix=" + matcher2.group(3) + 
            ", Line=" + matcher2.group(4));
    }
    
    @Test
    void testCapturingGroups_Email() {
        System.out.println("\n=== Capturing Groups: Email ===");
        
        Pattern emailWithGroups = Pattern.compile("^([A-Za-z0-9+_.-]+)@([A-Za-z0-9.-]+)\\.([A-Za-z]{2,})$");
        
        Matcher matcher = emailWithGroups.matcher("john.doe@example.com");
        assertTrue(matcher.matches());
        assertEquals("john.doe", matcher.group(1));
        assertEquals("example", matcher.group(2));
        assertEquals("com", matcher.group(3));
        System.out.println("Extracted: Username=" + matcher.group(1) + ", Domain=" + matcher.group(2) + 
            ", TLD=" + matcher.group(3));
    }
    
    @Test
    void testCapturingGroups_StudentId() {
        System.out.println("\n=== Capturing Groups: Student ID ===");
        
        Pattern studentIdWithGroups = Pattern.compile("^(STU)(\\d{3,5})$");
        
        Matcher matcher = studentIdWithGroups.matcher("STU12345");
        assertTrue(matcher.matches());
        assertEquals("STU", matcher.group(1));
        assertEquals("12345", matcher.group(2));
        System.out.println("Extracted: Prefix=" + matcher.group(1) + ", Number=" + matcher.group(2));
        
        // Convert to integer
        int studentNumber = Integer.parseInt(matcher.group(2));
        assertEquals(12345, studentNumber);
        System.out.println("Student number as int: " + studentNumber);
    }
    
    @Test
    void testCapturingGroups_Name() {
        System.out.println("\n=== Capturing Groups: Name ===");
        
        Pattern nameWithGroups = Pattern.compile("^([A-Za-z]+)(?:\\s+([A-Za-z'-]+))?(?:\\s+([A-Za-z'-]+))?$");
        
        // First name only
        Matcher matcher1 = nameWithGroups.matcher("John");
        assertTrue(matcher1.matches());
        assertEquals("John", matcher1.group(1));
        assertNull(matcher1.group(2));
        System.out.println("Extracted: First=" + matcher1.group(1));
        
        // First and last name
        Matcher matcher2 = nameWithGroups.matcher("John Smith");
        assertTrue(matcher2.matches());
        assertEquals("John", matcher2.group(1));
        assertEquals("Smith", matcher2.group(2));
        System.out.println("Extracted: First=" + matcher2.group(1) + ", Last=" + matcher2.group(2));
        
        // First, middle, and last name
        Matcher matcher3 = nameWithGroups.matcher("John Michael Smith");
        assertTrue(matcher3.matches());
        assertEquals("John", matcher3.group(1));
        assertEquals("Michael", matcher3.group(2));
        assertEquals("Smith", matcher3.group(3));
        System.out.println("Extracted: First=" + matcher3.group(1) + ", Middle=" + matcher3.group(2) + 
            ", Last=" + matcher3.group(3));
    }
    
    @Test
    void testCapturingGroups_Grade() {
        System.out.println("\n=== Capturing Groups: Grade ===");
        
        Pattern gradeWithGroups = Pattern.compile("^(100(\\.0{1,2})?|(\\d{1,2})(\\.\\d{1,2})?)$");
        
        // Whole number grade
        Matcher matcher1 = gradeWithGroups.matcher("85");
        assertTrue(matcher1.matches());
        System.out.println("Grade: " + matcher1.group(1));
        
        // Decimal grade
        Matcher matcher2 = gradeWithGroups.matcher("92.5");
        assertTrue(matcher2.matches());
        System.out.println("Grade: " + matcher2.group(1));
        
        // Perfect score
        Matcher matcher3 = gradeWithGroups.matcher("100");
        assertTrue(matcher3.matches());
        System.out.println("Grade: " + matcher3.group(1));
    }
}
