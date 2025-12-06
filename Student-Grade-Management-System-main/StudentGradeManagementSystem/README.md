# Student Grade Management System

✅ **Student Grade Management System** is a lightweight Java console application (Maven-based) that demonstrates simple management of students, subjects, and grades. The project emphasizes fundamental OOP design concepts (abstraction, encapsulation, inheritance, polymorphism and interface usage) and demonstrates core features such as adding students, recording grades, viewing students and grade reports.

---

## Table of Contents
- Project Summary
- Project Structure
- Core Functionality
- Data Model & Class Relationships
- OOP Concepts & Patterns Used
- Build & Run
- Examples & Code Snippets
- Testing & Sample Data
- Suggestions for Improvement
- License

---

## Project Summary

This project is a small console application that allows managing a list of students and their grades for subjects. It supports two types of students (Regular and Honors), subject categorization (Core and Elective), and grade recording. It demonstrates the core OOP principles and provides an educational example of a small model-driven application.

## Project Structure

Top-level files:
- `pom.xml` — Maven project descriptor
- `README.md` — Project documentation

Main sources (`src/main/java/org/example/`):
- `Main.java` — main console entry point, menu flow and input validation
- `Student.java` — abstract base class for students (common properties and methods)
- `RegularStudent.java` — concrete subclass of `Student` with regular passing grade
- `HonorsStudent.java` — concrete subclass with honors logic
- `StudentManager.java` — manages list of students and a `GradeManager` instance, initialization with sample data, major operations
- `Grade.java` — model representing a recorded grade, implements `Gradable` interface
- `Gradable.java` — interface defining grade operations (record/validate)
- `GradeManager.java` — manages `Grade` objects (add, view, compute averages per type)
- `Subject.java` — abstract base for subjects
- `CoreSubject.java` — a Core subject type
- `ElectiveSubject.java` — an elective subject type

---

## Core Functionality
- Add Student: Add a `RegularStudent` or `HonorsStudent` with validated input (name, age, email, phone). Each new student receives a sequential ID.
- View Students: Display a summary list of all students, their average grades, and status (passing/failing). Honors students show honors eligibility.
- Record Grade: Choose a student, choose subject type and subject, then record a grade (0–100). Grade recording uses `Gradable` interface validation.
- View Grade Report: View all grades for a selected student, plus averages per subject type and a performance summary that indicates whether the student meets passing requirements.

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

- Abstraction: `Student` and `Subject` are abstract base classes that encapsulate shared behavior and require concrete subclasses to implement type-specific details.
- Inheritance: `RegularStudent` and `HonorsStudent` inherit from `Student`. `CoreSubject` and `ElectiveSubject` inherit from `Subject`.
- Polymorphism / Method Overriding: Subclasses override base methods — e.g., `displayStudentDetails()`, `getPassingGrade()` and `getStudentType()`.
- Interface: `Gradable` enforces `recordGrade` and `validateGrade` on `Grade` objects.
- Encapsulation: Fields are private and exposed via getters and setters where appropriate.
- Composition & Aggregation: `StudentManager` includes `GradeManager`, and `Student` contains `List<Grade>`, showing relationships between managers and models.
- Static counters: `studentCounter` and `gradeCounter` implement uniquely-sequenced id generation for students and grades.

---

## Build & Run

Prerequisites:
- Java JDK 8+ (this project uses Java 8 features)
- Maven

From project root (PowerShell / Windows):

```powershell
mvn clean package
java -cp target/StudentGradeManagementSystem-1.0-SNAPSHOT.jar org.example.Main
```

If you haven't created a runnable JAR (no exec plugin), use the classpath of compiled classes:

```powershell
mvn clean compile
java -cp target/classes org.example.Main
```

---

## Examples & Key Code Snippets

1) Abstract student class (common methods, encapsulation, and basic operations):

```java
public abstract class Student {
    private String studentId;
    private String name;
    private int age;
    private List<Grade> grades;

    public Student(String name, int age, String email, String phone) {
        this.studentId = "STU" + String.format("%03d", studentCounter++);
        this.name = name;
        // ...
        this.grades = new ArrayList<>();
    }

    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    public void addGrade(Grade grade) { grades.add(grade); }
    public double calculateAverageGrade() { /* ... */ }
}
```

2) Example of inheritance: `HonorsStudent` overrides behavior:

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

The application comes with a built-in sample dataset generated at startup inside `StudentManager#initializeSampleData`. This sets up 5 sample students and multiple sample grades for demonstration purposes.

To test the console interface, compile and run the program and use the menu to add students, record grades or view reports.

---

## Suggestions for Improvements (Possible enhancements)

- Add persistent storage (file based, JSON, or a DB) so data survives application restarts.
- Replace fixed-size arrays in managers with `List` implementations for dynamic sizing and simplified operations.
- Add unit tests and integration tests (JUnit) for robust code validation.
- Add CLI parameters or a REST API layer to make interaction easier and enable automation.
- Add validation features and clearer error messages, and internationalization support.
- Package a runnable JAR using Maven `maven-assembly-plugin` or `spring-boot` (if converted) for easy distribution.

---

## Contributing

Feel free to open issues and submit PRs. If you plan to extend the project, please add tests and update this README for usage instructions.

---

## License

This sample project is distributed with no specific license. Add a LICENSE file to specify the project license.

---

If you want, I can also:
- Add a small UML-style diagram (text-based) summarizing the classes and relationships
- Replace array-based manager implementations with Lists for more idiomatic Java
- Add unit tests (JUnit 5) for `Student`, `Grade`, and `Manager` classes

If you'd like any of the above, tell me which item to implement next.
