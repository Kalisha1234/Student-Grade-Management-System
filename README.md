# Student Grade Management System

✅ **Student Grade Management System** is an enhanced Java console application (Maven-based) that provides comprehensive management of students, subjects, and grades with advanced features including bulk CSV import, statistical analysis, GPA calculation, and report generation. The project emphasizes fundamental OOP design concepts (abstraction, encapsulation, inheritance, polymorphism, and interface usage) along with service-oriented architecture and file I/O operations.

---

## Table of Contents
- [Project Summary](#project-summary)
- [Project Structure](#project-structure)
- [Core Functionality](#core-functionality)
- [Advanced Features](#advanced-features)
- [Data Model & Class Relationships](#data-model--class-relationships)
- [OOP Concepts & Patterns Used](#oop-concepts--patterns-used)
- [Build & Run](#build--run)
- [Examples & Code Snippets](#examples--code-snippets)
- [Testing & Sample Data](#testing--sample-data)
- [Suggestions for Improvement](#suggestions-for-improvement)
- [License](#license)

---

## Project Summary

This project is an enhanced console application that allows comprehensive management of students and their grades across multiple subjects. It supports two types of students (Regular and Honors), subject categorization (Core and Elective), grade recording, bulk CSV imports, statistical analysis, GPA calculations, and automated report generation. The application demonstrates advanced OOP principles and provides a complete educational example of a service-oriented model-driven application with file persistence.

## Project Structure

Top-level files:
- `pom.xml` — Maven project descriptor
- `README.md` — Project documentation
- `imports/` — Directory for CSV import files
- `logs/` — Directory for import log files
- `reports/` — Directory for exported reports

Main sources (`src/main/java/org/example/`):

**Main Entry:**
- `Main.java` — Enhanced console entry point with comprehensive menu system and input validation

**Models (`models/`):**
- `Student.java` — Abstract base class for students (common properties and methods)
- `RegularStudent.java` — Concrete subclass of `Student` with regular passing grade (50%)
- `HonorsStudent.java` — Concrete subclass with honors logic (passing: 60%, honors eligibility: 85%+)
- `Grade.java` — Model representing a recorded grade, implements `Gradable` interface
- `GradeRecord.java` — Model for CSV import records
- `ImportResult.java` — Model for bulk import results tracking
- `Subject.java` — Abstract base for subjects
- `CoreSubject.java` — Core subject type (mandatory subjects)
- `ElectiveSubject.java` — Elective subject type (optional subjects)

**Interfaces (`interfaces/`):**
- `Gradable.java` — Interface defining grade operations (record/validate)
- `Searchable.java` — Interface for student search operations
- `Exportable.java` — Interface for report export operations

**Services (`service/`):**
- `EnhancedStudentManager.java` — Enhanced student management with search, statistics, and GPA reporting
- `GradeManager.java` — Manages `Grade` objects (add, view, compute averages per type)
- `BulkImportService.java` — CSV bulk import service with validation and error handling
- `CSVParser.java` — Parses CSV files with validation
- `FileExporter.java` — Exports reports to file system
- `GPACalculator.java` — Calculates GPA on 4.0 scale and letter grades
- `StatisticsCalculator.java` — Computes statistical measures (mean, median, mode, std dev)
- `ReportGenerator.java` — Generates formatted reports

**Exceptions (`exceptions/`):**
- `StudentNotFoundException.java` — Custom exception for missing students
- `InvalidGradeException.java` — Custom exception for invalid grades
- `CSVProcessingException.java` — Custom exception for CSV parsing errors
- `InvalidFileFormatException.java` — Custom exception for invalid file formats

**Tests (`test/`):**
- `GPACalculatorTest.java` — Unit tests for GPA calculation
- `StatisticsCalculatorTest.java` — Unit tests for statistics

---

## Core Functionality

1. **Add Student**: Add a `RegularStudent` or `HonorsStudent` with validated input (name, age, email, phone). Each new student receives a sequential ID (STU001, STU002, etc.).

2. **View Students**: Display a comprehensive list of all students with:
   - Student ID, name, type
   - Average grade and enrolled subjects count
   - Passing/failing status
   - Honors eligibility status (for honors students)

3. **Record Grade**: Select a student, choose subject type (Core/Elective) and subject, then record a grade (0–100). Uses `Gradable` interface validation.

4. **View Grade Report**: Display detailed grade report for a selected student including:
   - All recorded grades by subject
   - Core subjects average
   - Elective subjects average
   - Overall average
   - Performance summary with pass/fail status

---

## Advanced Features

5. **Export Grade Report**: Generate and save professional reports to file system:
   - **Summary Report**: Student overview with performance metrics
   - **Detailed Report**: Comprehensive report with all grades, GPA, and analysis
   - **Multi-format Export**: CSV, JSON, Binary serialization
   - **Format Comparison**: Performance and size analysis

6. **Calculate Student GPA**: 
   - Convert percentage grades to 4.0 GPA scale
   - Calculate cumulative GPA
   - Display letter grades (A, A-, B+, B, B-, C+, C, C-, D+, D, F)
   - Show class rank with TreeMap-based rankings

7. **Search Students**: Advanced search capabilities:
   - Search by student ID (O(1) HashMap lookup)
   - Search by name (partial match with sorting)
   - Search by grade range (filtered and sorted)
   - Search by student type (Regular/Honors)
   - **Pattern Search**: Regex-based search (email domain, phone area code, ID pattern)

8. **View Statistics**: Comprehensive class statistics:
   - Grade distribution (A, B+, B, C+, C, D, F percentages)
   - Statistical analysis (mean, median, mode, standard deviation, range)
   - Total students and grades count
   - Visual bar charts with Unicode characters

9. **Bulk Import Grades**: CSV bulk import feature:
   - Import multiple grades from CSV file
   - Automatic validation (student exists, grade range 0-100)
   - Error handling with detailed error reporting
   - Import log generation with success/failure breakdown
   - **Streaming Import**: Memory-efficient processing with Files.lines()

10. **Real-Time Statistics Dashboard**: Live monitoring with background threads:
   - Auto-refresh every 5 seconds
   - Grade distribution visualization
   - Top performers tracking
   - Thread status monitoring
   - Cache hit rate metrics
   - Quick add student/grade from dashboard

11. **Batch Report Generation**: Concurrent report processing:
   - Multi-threaded report generation (2-8 threads)
   - Progress monitoring with thread visualization
   - Performance metrics (throughput, avg time)
   - Configurable thread pool size
   - Success/failure tracking per report

12. **Stream-Based Data Processing**: Java 8 Streams API:
   - Filter operations (honors students, grade ranges)
   - Map operations (extract emails, IDs)
   - Reduce operations (total grades, averages)
   - Grouping and partitioning
   - Parallel vs sequential performance comparison
   - Short-circuiting operations (findFirst, anyMatch)

13. **Scheduled Tasks**: Background automation:
   - Daily GPA recalculation
   - Hourly statistics refresh
   - Weekly batch reports
   - Daily database backup
   - Custom task scheduling (hourly/daily/weekly)
   - Email notifications
   - Audit logging

14. **Cache Management**: LRU cache with performance monitoring:
   - Student cache (150 entries)
   - Report cache (150 entries)
   - Statistics cache (150 entries)
   - Hit/miss rate tracking
   - Auto-refresh every 5 minutes
   - Cache warming on startup
   - Memory usage monitoring

15. **Enhanced File Operations**: NIO.2 with multiple formats:
   - CSV export (Excel compatible)
   - JSON export (Web/API format)
   - Binary export (Java serialization)
   - File watching for auto-import
   - Streaming for large files
   - UTF-8 encoding support

16. **Audit Logging**: Comprehensive operation tracking:
   - Thread-safe async logging
   - Operation type, execution time, success/failure
   - Date range search
   - Operation type filtering
   - Statistics (success rate, avg execution time)
   - Automatic log rotation (10MB limit)

---

## Data Model & Class Relationships

**Student Hierarchy (Inheritance):**
- `Student` (abstract)
  - Fields: studentId, name, age, email, phone, status, List<Grade>
  - Subclasses: `RegularStudent` (50% passing), `HonorsStudent` (60% passing, 85%+ honors)

**Subject Hierarchy (Inheritance):**
- `Subject` (abstract)
  - Fields: subjectName, subjectCode, subjectType
  - Subclasses: `CoreSubject`, `ElectiveSubject`

**Grade Model:**
- `Grade` implements `Gradable`
  - Fields: gradeId, studentId, Subject subject, grade (0-100), date
  - Methods: recordGrade(), validateGrade()

**Service Layer (Composition):**
- `EnhancedStudentManager` 
  - Owns: Student[] students, StatisticsCalculator, GPACalculator, ReportGenerator
  - Implements: `Searchable` interface
  
- `BulkImportService`
  - Owns: EnhancedStudentManager, CSVParser, FileExporter
  - Handles: CSV import workflow with validation

- `FileExporter` implements `Exportable`
  - Owns: ReportGenerator, GPACalculator, EnhancedStudentManager
  - Handles: Report generation and file I/O

This design demonstrates:
- **Aggregation**: Student has many Grades
- **Association**: Grade references a Subject
- **Composition**: Managers own their service dependencies

## Data Model & Class Relationships

- Student (abstract)
  - fields: `studentId`, `name`, `age`, `email`, `phone`, `status`, `List<Grade>`
  - subclasses: `RegularStudent`, `HonorsStudent`
- Subject (abstract)
  - subclasses: `CoreSubject`, `ElectiveSubject`
- Grade
  - fields: `gradeId`, `studentId`, `Subject subject`, `grade` (numeric), `date`
  - implements `Gradable` which defines `recordGrade` and `validateGrade`.
- StudentManager
  - owns array of `Student[] students` and a `GradeManager` instance
  - handles add/find/display and sample data initialization
- GradeManager
  - owns fixed array `Grade[] grades` and provides aggregate methods such as `calculateCoreAverage`, `calculateElectiveAverage`, and `calculateOverallAverage`.

This design shows aggregation/association: `Student` has many `Grade`s, `Grade` references a `Subject`, `StudentManager` has a `GradeManager` (composition-like relationship).

---

## OOP Concepts & Patterns Used

**1. Abstraction:**
- `Student` and `Subject` are abstract base classes encapsulating shared behavior
- Force concrete subclasses to implement type-specific details

**2. Inheritance:**
- `RegularStudent` and `HonorsStudent` extend `Student`
- `CoreSubject` and `ElectiveSubject` extend `Subject`
- Promotes code reuse and establishes IS-A relationships

**3. Polymorphism / Method Overriding:**
- Subclasses override base methods: `displayStudentDetails()`, `getPassingGrade()`, `getStudentType()`
- Runtime polymorphism allows treating different student types uniformly

**4. Interface Implementation:**
- `Gradable` enforces `recordGrade()` and `validateGrade()` on `Grade` objects
- `Searchable` defines search contract for student managers
- `Exportable` defines export operations for reports

**5. Encapsulation:**
- All fields are private with controlled access via getters/setters
- Data hiding and validation enforce business rules

**6. Composition & Aggregation:**
- `EnhancedStudentManager` owns service components (composition)
- `Student` contains `List<Grade>` (aggregation)
- Shows proper object relationships and lifecycle management

**7. Service-Oriented Architecture:**
- Separation of concerns with dedicated service classes
- `BulkImportService`, `FileExporter`, `GPACalculator`, `StatisticsCalculator`
- Single Responsibility Principle applied

**8. Exception Handling:**
- Custom exceptions for domain-specific errors
- Proper error propagation and handling throughout the application

**9. Static Members:**
- `studentCounter` and `gradeCounter` for unique ID generation
- Shared state across all instances

---

## Build & Run

**Prerequisites:**
- Java JDK 8+ (project uses Java 8+ features including streams)
- Maven 3.6+

**Build and Run (PowerShell / Windows):**

```powershell
# Compile and package
mvn clean package

# Run the application
java -cp target/StudentGradeManagementSystem-1.0-SNAPSHOT.jar org.example.Main
```

**Alternative - Run without packaging:**

```powershell
mvn clean compile
java -cp target/classes org.example.Main
```

**IntelliJ IDEA / VS Code:**
- Open project in IDE
- Set working directory to workspace root (where `imports/`, `logs/`, `reports/` folders are)
- Run `Main.java`

---

## CSV Import Format

To use the bulk import feature, create a CSV file in the `imports/` directory with the following format:

```csv
StudentID,SubjectName,SubjectType,Grade
STU001,Mathematics,Core,85
STU001,English,Core,78
STU002,Science,Core,92
```

**CSV Requirements:**
- First row must be header
- StudentID must match existing student in system
- SubjectType must be either "Core" or "Elective"
- Grade must be between 0 and 100

---

## Examples & Key Code Snippets

**1. Abstract Student Class (Inheritance & Abstraction):**

```java
public abstract class Student {
    private String studentId;
    private String name;
    private int age;
    private List<Grade> grades;

    public Student(String name, int age, String email, String phone) {
        this.studentId = "STU" + String.format("%03d", studentCounter++);
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    public void addGrade(Grade grade) { grades.add(grade); }
    public double calculateAverageGrade() { /* calculates average */ }
    public boolean isPassing() { return calculateAverageGrade() >= getPassingGrade(); }
}
```

**2. Honors Student (Polymorphism):**

```java
public class HonorsStudent extends Student {
    private double passingGrade = 60.0;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public String getStudentType() { return "Honors"; }

    @Override
    public double getPassingGrade() { return passingGrade; }

    public boolean checkHonorsEligibility() { 
        return calculateAverageGrade() >= 85.0; 
    }
}
```

**3. Gradable Interface & Grade Implementation:**

```java
public interface Gradable {
    boolean recordGrade(double grade);
    boolean validateGrade(double grade);
}

public class Grade implements Gradable {
    private String gradeId;
    private String studentId;
    private Subject subject;
    private double grade;
    private Date date;

    @Override
    public boolean recordGrade(double grade) {
        if (validateGrade(grade)) {
            this.grade = grade;
            return true;
        }
        return false;
    }

    @Override
    public boolean validateGrade(double grade) { 
        return grade >= 0 && grade <= 100; 
    }
}
```

**4. Enhanced Student Manager (Service Layer):**

```java
public class EnhancedStudentManager implements Searchable {
    private Student[] students = new Student[MAX_STUDENTS];
    private StatisticsCalculator statisticsCalculator;
    private GPACalculator gpaCalculator;

    @Override
    public Student searchById(String studentId) throws StudentNotFoundException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    public void displayGPAReport(String studentId) {
        // GPA calculation and display
    }

    public void calculateAndDisplayStatistics() {
        // Statistical analysis
    }
}
```

**5. Bulk Import Service (CSV Processing):**

```java
public class BulkImportService {
    private EnhancedStudentManager studentManager;
    private CSVParser csvParser;
    private FileExporter fileExporter;

    public ImportResult importResult(String filename) {
        ImportResult result = new ImportResult();
        List<GradeRecord> records = csvParser.parseCSV("imports/" + filename + ".csv");
        
        for (GradeRecord record : records) {
            try {
                processGradeRecord(record);
                result.incrementSuccessful();
            } catch (Exception e) {
                result.incrementFailed();
                result.addError("Row " + record.getRowNumber() + ": " + e.getMessage());
            }
        }
        
        fileExporter.logImport(filename, records.size(), 
            result.getSuccessful(), result.getFailed(), result.getErrors());
        return result;
    }
}
```

---

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public String getStudentType() { return "Honors"; }

    @Override
    public double getPassingGrade() { return passingGrade; }

    public boolean checkHonorsEligibility() { return calculateAverageGrade() >= 85.0; }
}
```

3) `Gradable` interface and `Grade` implementation (encapsulation + input validation via `validateGrade`):

```java
public interface Gradable {
    boolean recordGrade(double grade);
    boolean validateGrade(double grade);
}

public class Grade implements Gradable {
    // fields ...
    @Override
    public boolean recordGrade(double grade) {
        if (validateGrade(grade)) {
            this.grade = grade; return true;
        }
        return false;
    }
    @Override
    public boolean validateGrade(double grade) { return grade >= 0 && grade <= 100; }
}
```

4) Manager examples: `StudentManager` and `GradeManager` (arrays, simple aggregation):

```java
public class StudentManager {
    private Student[] students = new Student[MAX_STUDENTS];
    private GradeManager gradeManager = new GradeManager();

    private void initializeSampleData() {
        addStudent(new RegularStudent("Alice Johnson", 16, "alice@school.edu", "+1-555-1001"));
        // add sample grades...
        syncGradesWithStudents();
    }

    public void viewAllStudents() { /* pretty-printing with averages/brackets */ }
}

public class GradeManager {
    private Grade[] grades = new Grade[MAX_GRADES];
    public double calculateOverallAverage(String studentId) { /* compute */ }
}
```

5) From `Main.java`: validate user input, add student, record grade and interact with managers.

```java
Student student;
if (typeChoice == 1) student = new RegularStudent(name, age, email, phone);
else student = new HonorsStudent(name, age, email, phone);
studentManager.addStudent(student);

// Recording a grade
Grade grade = new Grade(studentId, subject, gradeValue);
gradeManager.addGrade(grade);
student.addGrade(grade);
```

---

## Testing & Sample Data

The application comes with built-in sample data initialized at startup in `EnhancedStudentManager#initializeSampleData()`:

**Sample Students (20 total):**
- **STU001-STU005**: Original set with various performance levels
- **STU006-STU020**: Additional students with diverse academic profiles
- Mix of Regular and Honors students for comprehensive testing

**Sample Grades:**
- Each student has 4-6 grades across Core and Elective subjects
- Mix of passing and failing grades for testing different scenarios
- Honors students with grades demonstrating eligibility criteria

**Testing CSV Import:**
1. Sample CSV file in `imports/sample_grades.csv` with 39 records
2. Includes 37 valid records and 2 invalid records for validation testing:
   - Invalid student ID (STU999 - doesn't exist)
   - Invalid grade (105 - exceeds maximum of 100)

**Comprehensive Test Suite:**

1. **GPACalculatorTest.java** - 11 test methods
   - Percentage to GPA conversion (4.0 scale)
   - Letter grade conversion (A, A-, B+, etc.)
   - Cumulative GPA calculation
   - Boundary value testing
   - Large dataset performance (1000 grades)

2. **StatisticsCalculatorTest.java** - 15+ test methods
   - Mean, median, mode calculations
   - Standard deviation
   - Grade distribution
   - Edge cases (empty, null, single element)
   - Real-world class scenarios

3. **CollectionPerformanceTest.java** - 8 test methods
   - HashMap O(1) vs ArrayList O(n) lookup
   - TreeMap auto-sorting performance
   - HashSet uniqueness guarantees
   - Empirical Big-O complexity measurements
   - Parallel vs sequential stream performance
   - Memory usage comparison
   - Concurrent modification handling

4. **ConcurrencyTest.java** - 5 test methods
   - Thread-safe collections (ConcurrentHashMap vs HashMap)
   - Race conditions (unsafe vs synchronized vs atomic)
   - Deadlock prevention with ordered locking
   - Thread pool shutdown (graceful vs immediate)
   - Cache consistency under concurrent access

5. **FileOperationsTest.java** - 7 test methods
   - NIO.2 file reading (1KB, 100KB, 1MB)
   - Streaming vs loading entire file
   - Concurrent file access handling
   - UTF-8 encoding (Chinese, Russian, Arabic, emojis)
   - Mock file system testing
   - Path operations (copy, move, delete)
   - Buffered vs direct I/O performance

6. **RegexValidationTest.java** - 20+ test methods
   - Email, phone, student ID, name, grade patterns
   - 11+ valid and invalid inputs per pattern
   - Edge cases (empty, whitespace, special chars)
   - Pattern compilation performance
   - Capturing groups extraction

7. **StreamProcessingTest.java** - 6 test methods
   - Filter/map/reduce operations
   - Parallel stream correctness
   - Short-circuiting operations
   - Lazy evaluation behavior
   - Sequential vs parallel performance
   - Stream collectors and statistics

8. **ConcurrentGradeProcessorTest.java** - 4 test methods
   - Async mean/median calculation
   - CompletableFuture operations
   - Concurrent statistics processing

**Manual Testing:**
Run the application and test each menu option:
1. Add new students (test validation)
2. View all students (verify display formatting)
3. Record grades (test validation rules)
4. View grade reports (verify calculations)
5. Export reports (check file creation in `reports/`)
6. Calculate GPA (verify 4.0 scale conversion)
7. Search students (test all search types)
8. View statistics (verify statistical calculations)
9. Bulk import (test with sample CSV)
10. Real-time dashboard (test concurrent updates)
11. Batch report generation (test multi-threading)
12. Pattern search (test regex matching)
13. Stream processing (test parallel operations)
14. Scheduled tasks (test background execution)
15. Cache management (test LRU eviction)
16. Enhanced file operations (test CSV/JSON/Binary)

**Running Tests:**
```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GPACalculatorTest

# Run with coverage
mvn clean test jacoco:report
```

---

## Suggestions for Improvement

**Current Limitations & Future Enhancements:**

1. **Persistent Storage:**
   - Add database integration (SQLite, H2, or MySQL)
   - Implement file-based persistence (JSON/XML serialization)
   - Data survives application restarts

2. **Dynamic Data Structures:**
   - Replace fixed-size arrays with `ArrayList` for unlimited students/grades
   - Improve memory efficiency and simplify operations

3. **Enhanced Testing:**
   - Comprehensive JUnit 5 test suite
   - Integration tests for service layer
   - Mock testing for file I/O operations
   - Code coverage analysis

4. **User Interface:**
   - JavaFX or Swing GUI
   - Web interface with Spring Boot
   - REST API for external integrations

5. **Advanced Features:**
   - Multi-semester support
   - Grade history and trends analysis
   - Student attendance tracking
   - Parent/guardian access portal
   - Email notifications

6. **Reporting Enhancements:**
   - PDF report generation (using iText or Apache PDFBox)
   - Excel export (Apache POI)
   - Customizable report templates
   - Chart/graph visualizations

7. **Security & Authentication:**
   - User login system (students, teachers, admins)
   - Role-based access control
   - Password encryption
   - Audit logging

8. **Configuration Management:**
   - External configuration files (application.properties)
   - Customizable grading scales
   - Configurable validation rules

9. **Performance Optimization:**
   - Implement caching for frequent calculations
   - Lazy loading for large datasets
   - Batch operations for bulk imports

10. **Code Quality:**
    - Apply design patterns (Factory, Strategy, Observer)
    - Implement dependency injection
    - Add logging framework (Log4j, SLF4J)
    - Code documentation improvements

---

## Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes** and add tests
4. **Commit your changes**: `git commit -m 'Add some feature'`
5. **Push to the branch**: `git push origin feature/your-feature-name`
6. **Submit a Pull Request**

**Guidelines:**
- Follow existing code style and conventions
- Add unit tests for new features
- Update README.md with new functionality
- Ensure all tests pass before submitting PR
- Write clear commit messages

---

## License

This project is an educational sample and is distributed without a specific license. Feel free to use it for learning purposes, modify it, and extend it as needed.

For production use or distribution, please add an appropriate LICENSE file (MIT, Apache 2.0, GPL, etc.).

---

**Happy Coding! 🎓**
