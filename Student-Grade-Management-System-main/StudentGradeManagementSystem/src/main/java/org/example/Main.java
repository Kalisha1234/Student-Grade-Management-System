package org.example;

import org.example.exceptions.StudentNotFoundException;
import org.example.models.*;
import org.example.service.*;
import org.example.utils.ValidationUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    private static EnhancedStudentManager studentManager;
    private static FileExporter fileExporter;
    private static BulkImportService bulkImportService;
    private static CSVParser csvParser;
    private static GPACalculator gpaCalculator;
    private static ReportGenerator reportGenerator;
    private static EnhancedFileOperations enhancedFileOps;
    private static ConcurrentReportGenerator concurrentReportGenerator;
    private static RealTimeStatisticsDashboard statisticsDashboard;
    private static TaskScheduler taskScheduler;
    private static PatternSearchService patternSearchService;
    private static CacheManager cacheManager;
    private static StreamDataProcessor streamDataProcessor;
    private static AuditLogger auditLogger;
    private static Scanner scanner;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+\\d\\s-()]+$");

    public static void main(String[] args) {
        initializeServices();
        displayMainMenu();
    }

    private static void initializeServices() {
        studentManager = new EnhancedStudentManager();
        csvParser = new CSVParser();
        gpaCalculator = new GPACalculator();
        reportGenerator = new ReportGenerator();
        fileExporter = new FileExporter(reportGenerator, gpaCalculator);
        fileExporter.setStudentManager(studentManager);
        concurrentReportGenerator = new ConcurrentReportGenerator(fileExporter, studentManager);
        statisticsDashboard = new RealTimeStatisticsDashboard(studentManager);
        taskScheduler = new TaskScheduler(studentManager);
        patternSearchService = new PatternSearchService(studentManager);
        cacheManager = new CacheManager(studentManager);
        streamDataProcessor = new StreamDataProcessor(studentManager, gpaCalculator);
        auditLogger = new AuditLogger();
        bulkImportService = new BulkImportService(studentManager, csvParser, fileExporter);
        
        try {
            enhancedFileOps = new EnhancedFileOperations();
            enhancedFileOps.setStudentManager(studentManager);
            enhancedFileOps.startFileWatcher();
        } catch (IOException e) {
            System.err.println("Warning: Enhanced file operations not available: " + e.getMessage());
        }
        
        scanner = new Scanner(System.in);
    }

    private static void displayMainMenu() {
        while (true) {
            System.out.println("\n┌─────────────────────────────────────────────────────────┐");
            System.out.println("│     STUDENT GRADE MANAGEMENT - MAIN MENU                │");
            System.out.println("│              [Advanced Edition v3.0]                    │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.println();
            System.out.println("STUDENT MANAGEMENT");
            System.out.println("1. Add Student (with validation)");
            System.out.println("2. View Students");
            System.out.println("3. Record Grade");
            System.out.println("4. View Grade Report");
            System.out.println();
            System.out.println("FILE OPERATIONS");
            System.out.println("5. Export Grade Report (CSV/JSON/Binary)");
            System.out.println("6. Import Data (Multi-format support)        [ENHANCED]");
            System.out.println("7. Bulk Import Grades");
            System.out.println();
            System.out.println("ANALYTICS & REPORTING");
            System.out.println("8. Calculate Student GPA");
            System.out.println("9. View Class Statistics");
            System.out.println("10. Real-Time Statistics Dashboard           [NEW]");
            System.out.println("11. Generate Batch Reports                   [NEW]");
            System.out.println();
            System.out.println("SEARCH & QUERY");
            System.out.println("12. Search Students (Advanced)               [ENHANCED]");
            System.out.println("13. Pattern-Based Search                     [NEW]");
            System.out.println("14. Stream-Based Data Processing             [NEW]");
            System.out.println();
            System.out.println("ADVANCED FEATURES");
            System.out.println("15. Schedule Automated Tasks                 [NEW]");
            System.out.println("16. View System Performance                  [NEW]");
            System.out.println("17. Cache Management                         [NEW]");
            System.out.println("18. File Operations Menu                     [NEW]");
            System.out.println("19. Audit Trail Viewer                       [NEW]");
            System.out.println();
            System.out.println("20. Exit");
            System.out.println();
            System.out.println("Background Tasks: ⚡ 3 active | 📊 Stats updating...");
            System.out.println();
            System.out.print("Enter choice: ");

            try {
                int choice = getIntInput();

                switch (choice) {
                    case 1: addStudent();break;
                    case 2: viewStudents(); break;
                    case 3: recordGrade(); break;
                    case 4: viewGradeReport(); break;
                    case 5: enhancedExportGradeReport(); break;
                    case 6: enhancedImportData(); break;
                    case 7: bulkImportGrades(); break;
                    case 8: calculateGPA(); break;
                    case 9: viewClassStatistics(); break;
                    case 10: realTimeStatisticsDashboard(); break;
                    case 11: batchReportGeneration(); break;
                    case 12: searchStudents(); break;
                    case 13: advancedPatternSearch(); break;
                    case 14: streamDataProcessing(); break;
                    case 15: scheduledTasksManager(); break;
                    case 16: displaySystemPerformance(); break;
                    case 17: cacheManagement(); break;
                    case 18: fileOperationsMenu(); break;
                    case 19: auditTrailViewer(); break;
                    case 20: exitApplication(); return;
                    default: System.out.println("Invalid choice! Please enter 1-20.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void addStudent() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nADD STUDENT");
        System.out.println();

        // Name validation
        String name;
        while (true) {
            System.out.print("Enter student name: ");
            name = scanner.nextLine().trim();
            try {
                ValidationUtils.validateName(name);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid name! Name should contain only letters and spaces.");
            }
        }

        // Age validation
        int age;
        while (true) {
            System.out.print("Enter student age: ");
            age = getIntInput();
            if (age >= 5 && age <= 100) {
                break;
            } else {
                System.out.println("Invalid age! Age should be between 5 and 100.");
            }
        }

        // Email validation
        String email;
        while (true) {
            System.out.print("Enter student email: ");
            email = scanner.nextLine().trim();
            try {
                ValidationUtils.validateEmail(email);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid email! Email must contain @ symbol and valid domain.");
            }
        }

        // Phone validation
        String phone;
        while (true) {
            System.out.print("Enter student phone: ");
            phone = scanner.nextLine().trim();
            try {
                ValidationUtils.validatePhone(phone);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid phone number! Use format like +1-555-1234 or (555) 123-4567.");
            }
        }

        System.out.println("\nStudent type:");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
        System.out.print("\nSelect type (1-2): ");

        int typeChoice = getIntInput();

        Student student;
        try {
            if (typeChoice == 1) {
                student = new RegularStudent(name, age, email, phone);
            } else if (typeChoice == 2) {
                student = new HonorsStudent(name, age, email, phone);
            } else {
                System.out.println("Invalid choice! Student not added.");
                auditLogger.log("ADD_STUDENT", "Add student: " + name, System.currentTimeMillis() - startTime, false, "Invalid student type");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error creating student: " + e.getMessage());
            auditLogger.log("ADD_STUDENT", "Add student: " + name, System.currentTimeMillis() - startTime, false, e.getMessage());
            return;
        }

        studentManager.addStudent(student);
        auditLogger.log("ADD_STUDENT", "Added student: " + student.getStudentId(), System.currentTimeMillis() - startTime, true, "Type: " + student.getStudentType());

        System.out.println("\n✓ Student added successfully!");
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("Age: " + student.getAge());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Passing Grade: " + student.getPassingGrade() + "%");
        System.out.println("Status: " + student.getStatus());

        if (student instanceof HonorsStudent) {
            System.out.println("Honors Eligible: " +
                    (((HonorsStudent) student).checkHonorsEligibility() ? "Yes" : "No"));
        }
    }

    private static void viewStudents() {
        long startTime = System.currentTimeMillis();
        studentManager.viewAllStudents();
        auditLogger.log("VIEW_STUDENTS", "Viewed all students", System.currentTimeMillis() - startTime, true, "Count: " + studentManager.getAllStudents().size());
    }

    private static void recordGrade() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nRECORD GRADE");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentManager.searchById(studentId);

            System.out.println("\nStudent Details:");
            System.out.println("Name: " + student.getName());
            System.out.println("Type: " + student.getStudentType() + " Student");
            System.out.printf("Current Average: %.1f%%\n", student.calculateAverageGrade());

            System.out.println("\nSubject type:");
            System.out.println("1. Core Subject (Mathematics, English, Science)");
            System.out.println("2. Elective Subject (Music, Art, Physical Education)");
            System.out.print("\nSelect type (1-2): ");

            int subjectType = getIntInput();

            Subject subject;
            if (subjectType == 1) {
                System.out.println("\nAvailable Core Subjects:");
                System.out.println("1. Mathematics");
                System.out.println("2. English");
                System.out.println("3. Science");
                System.out.print("\nSelect subject (1-3): ");

                int coreChoice = getIntInput();
                switch (coreChoice) {
                    case 1: subject = new CoreSubject("Mathematics", "MAT101"); break;
                    case 2: subject = new CoreSubject("English", "ENG101"); break;
                    case 3: subject = new CoreSubject("Science", "SCI101"); break;
                    default:
                        System.out.println("Invalid choice!");
                        return;
                }
            } else if (subjectType == 2) {
                System.out.println("\nAvailable Elective Subjects:");
                System.out.println("1. Music");
                System.out.println("2. Art");
                System.out.println("3. Physical Education");
                System.out.print("\nSelect subject (1-3): ");

                int electiveChoice = getIntInput();
                switch (electiveChoice) {
                    case 1: subject = new ElectiveSubject("Music", "MUS101"); break;
                    case 2: subject = new ElectiveSubject("Art", "ART101"); break;
                    case 3: subject = new ElectiveSubject("Physical Education", "PHY101"); break;
                    default:
                        System.out.println("Invalid choice!");
                        return;
                }
            } else {
                System.out.println("Invalid choice!");
                return;
            }

            // Grade validation
            double gradeValue;
            while (true) {
                System.out.print("Enter grade (0-100): ");
                gradeValue = getDoubleInput();
                if (gradeValue >= 0 && gradeValue <= 100) {
                    break;
                } else {
                    System.out.println("Invalid grade! Must be between 0 and 100.");
                }
            }

            Grade grade = new Grade(studentId, subject, gradeValue);

            System.out.println("\nGRADE CONFIRMATION");
            System.out.println();
            grade.displayGradeDetails();

            System.out.print("\nConfirm grade? (Y/N): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y")) {
                studentManager.addGradeToStudent(studentId, grade);
                auditLogger.log("RECORD_GRADE", "Recorded grade for " + studentId, System.currentTimeMillis() - startTime, true, subject.getSubjectName() + ": " + gradeValue);
                System.out.println("\n✓ Grade recorded successfully!");
            } else {
                auditLogger.log("RECORD_GRADE", "Grade recording cancelled for " + studentId, System.currentTimeMillis() - startTime, false, "User cancelled");
                System.out.println("Grade recording cancelled.");
            }

        } catch (StudentNotFoundException e) {
            System.out.println("\n✗ ERROR: " + e.getMessage());
            System.out.println("Available student IDs: " +
                    String.join(", ", studentManager.getAllStudentIds()));

            System.out.print("\nTry again? (Y/N): ");
            String tryAgain = scanner.nextLine();
            if (tryAgain.equalsIgnoreCase("Y")) {
                recordGrade();
            }
        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void viewGradeReport() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nVIEW GRADE REPORT");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentManager.searchById(studentId);
            auditLogger.log("VIEW_REPORT", "Viewed report for " + studentId, System.currentTimeMillis() - startTime, true, "Grades: " + student.getGrades().size());

            System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
            System.out.println("Type: " + student.getStudentType() + " Student");
            System.out.println("Passing Grade: " + student.getPassingGrade() + "%");

            if (student.getGrades().isEmpty()) {
                System.out.println("\nNo grades recorded for this student.");
            } else {
                System.out.printf("Current Average: %.1f%%\n", student.calculateAverageGrade());
                System.out.println("Status: " + (student.isPassing() ? "PASSING ✓" : "FAILING"));

                System.out.println("\nGRADE HISTORY");
                System.out.println("\nGRD ID | DATE       | SUBJECT     | TYPE    | GRADE");
                System.out.println("-----------------------------------------------------");

                // Display grades in reverse chronological order
                List<Grade> grades = student.getGrades();
                for (int i = grades.size() - 1; i >= 0; i--) {
                    Grade grade = grades.get(i);
                    System.out.printf("%-6s | %-10s | %-11s | %-7s | %.1f%%\n",
                            grade.getGradeId(),
                            grade.getDate(),
                            grade.getSubject().getSubjectName(),
                            grade.getSubject().getSubjectType(),
                            grade.getGrade());
                }

                // Calculate averages
                double coreSum = 0, electiveSum = 0;
                int coreCount = 0, electiveCount = 0;

                for (Grade grade : grades) {
                    if (grade.getSubject() instanceof CoreSubject) {
                        coreSum += grade.getGrade();
                        coreCount++;
                    } else {
                        electiveSum += grade.getGrade();
                        electiveCount++;
                    }
                }

                double coreAvg = coreCount > 0 ? coreSum / coreCount : 0;
                double electiveAvg = electiveCount > 0 ? electiveSum / electiveCount : 0;
                double overallAvg = student.calculateAverageGrade();

                System.out.println("\nTotal Grades: " + grades.size());
                System.out.printf("Core Subjects Average: %.1f%%\n", coreAvg);
                System.out.printf("Elective Subjects Average: %.1f%%\n", electiveAvg);
                System.out.printf("Overall Average: %.1f%%\n", overallAvg);

                System.out.println("\nPerformance Summary:");
                if (student.isPassing()) {
                    System.out.println("✓ Meeting passing grade requirement (" + student.getPassingGrade() + "%)");
                } else {
                    System.out.println("✗ Not meeting passing grade requirement (" + student.getPassingGrade() + "%)");
                }
            }

        } catch (StudentNotFoundException e) {
            auditLogger.log("VIEW_REPORT", "Failed to view report for " + studentId, System.currentTimeMillis() - startTime, false, e.getMessage());
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void exportGradeReport() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nEXPORT GRADE REPORT");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentManager.searchById(studentId);

            System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
            System.out.println("Type: " + student.getStudentType() + " Student");
            System.out.println("Total Grades: " + student.getGrades().size());

            System.out.println("\nExport options:");
            System.out.println("1. Summary Report (overview only)");
            System.out.println("2. Detailed Report (all grades)");
            System.out.println("3. Both");
            System.out.print("\nSelect option (1-3): ");

            int option = getIntInput();
            System.out.print("Enter filename (without extension): ");
            String filename = scanner.nextLine();

            if (option == 1) {
                fileExporter.exportSummaryReport(studentId, filename);
                auditLogger.log("EXPORT_REPORT", "Exported summary for " + studentId, System.currentTimeMillis() - startTime, true, filename);
                System.out.println("\n✓ Summary report exported successfully!");
                System.out.println("File: " + filename + "_summary.txt");
            } else if (option == 2) {
                fileExporter.exportDetailedReport(studentId, filename);
                auditLogger.log("EXPORT_REPORT", "Exported detailed for " + studentId, System.currentTimeMillis() - startTime, true, filename);
                System.out.println("\n✓ Detailed report exported successfully!");
                System.out.println("File: " + filename + "_detailed.txt");
            } else if (option == 3) {
                fileExporter.exportSummaryReport(studentId, filename);
                fileExporter.exportDetailedReport(studentId, filename);
                auditLogger.log("EXPORT_REPORT", "Exported both reports for " + studentId, System.currentTimeMillis() - startTime, true, filename);
                System.out.println("\n✓ Both reports exported successfully!");
                System.out.println("Files: " + filename + "_summary.txt, " + filename + "_detailed.txt");
            } else {
                System.out.println("Invalid option!");
                return;
            }

            System.out.println("Location: ./reports/");
            System.out.println("Contains: " + student.getGrades().size() +
                    " grades, averages, performance summary");

        } catch (StudentNotFoundException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n✗ Error exporting file: " + e.getMessage());
        }
    }

    private static void calculateGPA() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nCALCULATE STUDENT GPA");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            studentManager.displayGPAReport(studentId);
            auditLogger.log("CALCULATE_GPA", "Calculated GPA for " + studentId, System.currentTimeMillis() - startTime, true, "");
        } catch (StudentNotFoundException e) {
            auditLogger.log("CALCULATE_GPA", "Failed GPA calculation for " + studentId, System.currentTimeMillis() - startTime, false, e.getMessage());
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void bulkImportGrades() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nBULK IMPORT GRADES");
        System.out.println("\nPlace your CSV file in: ./imports/");
        System.out.println("\nCSV Format Required:");
        System.out.println("StudentID,SubjectName,SubjectType,Grade");
        System.out.println("Example: STU001,Mathematics,Core,85");

        System.out.print("\nEnter filename (without extension): ");
        String filename = scanner.nextLine();

        System.out.println("\nValidating file...");
        System.out.println("Processing grades...\n");

        ImportResult result = bulkImportService.importResult(filename);
        auditLogger.log("BULK_IMPORT", "Imported grades from " + filename, System.currentTimeMillis() - startTime, true, "Success: " + result.getSuccessful() + ", Failed: " + result.getFailed());

        System.out.println("IMPORT SUMMARY");
        System.out.println("\nTotal Rows: " + (result.getSuccessful() + result.getFailed()));
        System.out.println("Successfully Imported: " + result.getSuccessful());
        System.out.println("Failed: " + result.getFailed());

        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            System.out.println("\nFailed Records:");
            for (String error : result.getErrors()) {
                System.out.println(error);
            }
        }

        System.out.println("\n✓ Import completed!");
        System.out.println(result.getSuccessful() + " grades added to system");
        System.out.println("See import_log_*.txt in ./logs/ for details");
    }

    private static void viewClassStatistics() {
        long startTime = System.currentTimeMillis();
        studentManager.calculateAndDisplayStatistics();
        auditLogger.log("VIEW_STATISTICS", "Viewed class statistics", System.currentTimeMillis() - startTime, true, "");
    }

    private static void searchStudents() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nSEARCH STUDENTS");
        System.out.println("\nSearch options:");
        System.out.println("1. By Student ID");
        System.out.println("2. By Name (partial match)");
        System.out.println("3. By Grade Range");
        System.out.println("4. By Student Type");
        System.out.print("\nSelect option (1-4): ");

        int option = getIntInput();
        List<Student> results = new ArrayList<>();

        switch (option) {
            case 1:
                System.out.print("Enter Student ID: ");
                String studentId = scanner.nextLine();
                try {
                    Student student = studentManager.searchById(studentId);
                    results.add(student);
                    auditLogger.log("SEARCH", "Search by ID: " + studentId, System.currentTimeMillis() - startTime, true, "Found");
                    System.out.println("\nSEARCH RESULTS (1 found)");
                } catch (StudentNotFoundException e) {
                    auditLogger.log("SEARCH", "Search by ID: " + studentId, System.currentTimeMillis() - startTime, false, "Not found");
                    System.out.println("\n✗ " + e.getMessage());
                    return;
                }
                break;

            case 2:
                System.out.print("Enter name (partial or full): ");
                String name = scanner.nextLine();
                results = studentManager.searchByName(name);
                auditLogger.log("SEARCH", "Search by name: " + name, System.currentTimeMillis() - startTime, true, "Found: " + results.size());
                System.out.println("\nSEARCH RESULTS (" + results.size() + " found)");
                break;

            case 3:
                System.out.print("Enter minimum grade (0-100): ");
                double min = getDoubleInput();
                System.out.print("Enter maximum grade (0-100): ");
                double max = getDoubleInput();
                results = studentManager.searchByGradeRange(min, max);
                auditLogger.log("SEARCH", "Search by grade range: " + min + "-" + max, System.currentTimeMillis() - startTime, true, "Found: " + results.size());
                System.out.println("\nSEARCH RESULTS (" + results.size() + " found)");
                break;

            case 4:
                System.out.print("Enter student type (Regular/Honors): ");
                String type = scanner.nextLine();
                results = studentManager.searchByType(type);
                auditLogger.log("SEARCH", "Search by type: " + type, System.currentTimeMillis() - startTime, true, "Found: " + results.size());
                System.out.println("\nSEARCH RESULTS (" + results.size() + " found)");
                break;

            default:
                System.out.println("Invalid option!");
                return;
        }

        if (!results.isEmpty()) {
            System.out.println("\nSTU ID | NAME           | TYPE    | AVG GRADE | STATUS");
            System.out.println("----------------------------------------------------------");

            for (Student student : results) {
                System.out.printf("%-6s | %-14s | %-7s | %-9.1f%% | %s\n",
                        student.getStudentId(),
                        student.getName(),
                        student.getStudentType(),
                        student.calculateAverageGrade(),
                        student.isPassing() ? "Passing" : "Failing");
            }

            System.out.println("\nActions:");
            System.out.println("1. View full details for a student");
            System.out.println("2. Export search results");
            System.out.println("3. New search");
            System.out.println("4. Return to main menu");
            System.out.print("\nEnter choice: ");

            int action = getIntInput();

            switch (action) {
                case 1:
                    System.out.print("Enter Student ID to view details: ");
                    String viewId = scanner.nextLine();
                    try {
                        Student viewStudent = studentManager.searchById(viewId);
                        viewStudent.displayStudentDetails();
                    } catch (StudentNotFoundException e) {
                        System.out.println("Student not found!");
                    }
                    break;
                case 2:
                    System.out.print("Enter filename for export: ");
                    String exportFile = scanner.nextLine();
                    try {
                        fileExporter.exportSearchResults(results, exportFile);
                        System.out.println("✓ Search results exported!");
                    } catch (Exception e) {
                        System.out.println("Error exporting: " + e.getMessage());
                    }
                    break;
                case 3:
                    searchStudents();
                    break;
                case 4:
                    // Return to main menu
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } else {
            System.out.println("No students found matching the criteria.");
        }
    }

    private static void enhancedExportGradeReport() {
        if (enhancedFileOps == null) {
            System.out.println("Enhanced file operations not available. Using standard export...");
            exportGradeReport();
            return;
        }
        
        System.out.println("\nENHANCED EXPORT GRADE REPORT");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentManager.searchById(studentId);
            System.out.println("Student: " + student.getName() + " (" + student.getGrades().size() + " grades)");

            System.out.println("\nExport formats:");
            System.out.println("1. CSV (Excel compatible)");
            System.out.println("2. JSON (Web/API format)");
            System.out.println("3. Binary (Java serialized)");
            System.out.println("4. All formats");
            System.out.println("5. Format comparison");
            System.out.print("\nSelect option (1-5): ");

            int option = getIntInput();
            System.out.print("Enter filename (without extension): ");
            String filename = scanner.nextLine();

            switch (option) {
                case 1:
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "CSV");
                    break;
                case 2:
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "JSON");
                    break;
                case 3:
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "BINARY");
                    break;
                case 4:
                    System.out.println("\nProcessing with NIO.2 Streaming...\n");
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "CSV");
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "JSON");
                    enhancedFileOps.exportGradeReportWithOutput(studentId, filename, "BINARY");
                    break;
                case 5:
                    enhancedFileOps.displayFormatComparison(studentId);
                    break;
                default:
                    System.out.println("Invalid option!");
                    return;
            }

        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void enhancedImportData() {
        if (enhancedFileOps == null) {
            System.out.println("Enhanced file operations not available.");
            return;
        }
        
        System.out.println("\nENHANCED IMPORT DATA");
        System.out.println();
        System.out.println("Supported formats: CSV, JSON, Binary");
        System.out.println("File locations:");
        System.out.println("  CSV files: ./data/csv/");
        System.out.println("  JSON files: ./data/json/");
        System.out.println("  Binary files: ./data/binary/");

        System.out.println("\nImport formats:");
        System.out.println("1. CSV (Comma-separated values)");
        System.out.println("2. JSON (JavaScript Object Notation)");
        System.out.println("3. Binary (Java serialized objects)");
        System.out.print("\nSelect format (1-3): ");

        int formatChoice = getIntInput();
        System.out.print("Enter filename (with extension): ");
        String filename = scanner.nextLine();

        String format;
        switch (formatChoice) {
            case 1: format = "CSV"; break;
            case 2: format = "JSON"; break;
            case 3: format = "BINARY"; break;
            default:
                System.out.println("Invalid format choice!");
                return;
        }

        try {
            enhancedFileOps.importData(filename, format);
            System.out.println("\n✓ Import completed successfully!");
        } catch (Exception e) {
            System.out.println("\n✗ Import failed: " + e.getMessage());
        }
    }

    private static void fileOperationsMenu() {
        if (enhancedFileOps == null) {
            System.out.println("Enhanced file operations not available.");
            return;
        }
        
        System.out.println("\nFILE OPERATIONS");
        System.out.println();
        System.out.println("1. Bulk Import Grades (Streaming)");
        System.out.println("2. Format Comparison");
        System.out.println("3. Directory Status");
        System.out.println("4. Return to main menu");
        System.out.print("\nSelect option (1-4): ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                bulkImportWithStreaming();
                break;
            case 2:
                formatComparison();
                break;
            case 3:
                displayDirectoryStatus();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void bulkImportWithStreaming() {
        System.out.println("\nBULK IMPORT WITH STREAMING");
        System.out.println("Place CSV file in: ./data/csv/");
        System.out.print("Enter CSV filename: ");
        String filename = scanner.nextLine();

        try {
            System.out.println("\nProcessing with streaming (memory efficient)...");
            enhancedFileOps.bulkImportGrades(filename)
                .thenAccept(count -> {
                    System.out.println("\n✓ Bulk import completed!");
                    System.out.println("Records processed: " + count);
                })
                .exceptionally(throwable -> {
                    System.out.println("\n✗ Bulk import failed: " + throwable.getMessage());
                    return null;
                });
        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void formatComparison() {
        System.out.print("Enter Student ID for format comparison: ");
        String studentId = scanner.nextLine();

        try {
            enhancedFileOps.displayFormatComparison(studentId);
        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void batchReportGeneration() {
        long startTime = System.currentTimeMillis();
        System.out.println("\nBATCH REPORT GENERATION");
        System.out.println();
        
        System.out.println("Generate reports for:");
        System.out.println("1. All students");
        System.out.println("2. Students by type (Regular/Honors)");
        System.out.println("3. Students by grade range");
        System.out.print("\nSelect option (1-3): ");
        
        int option = getIntInput();
        List<Student> students = new ArrayList<>();
        
        switch (option) {
            case 1:
                students = studentManager.getAllStudents();
                break;
            case 2:
                System.out.print("Enter student type (Regular/Honors): ");
                String type = scanner.nextLine();
                students = studentManager.searchByType(type);
                break;
            case 3:
                System.out.print("Enter minimum grade: ");
                double min = getDoubleInput();
                System.out.print("Enter maximum grade: ");
                double max = getDoubleInput();
                students = studentManager.searchByGradeRange(min, max);
                break;
            default:
                System.out.println("Invalid option!");
                return;
        }
        
        if (students.isEmpty()) {
            System.out.println("No students found for batch report generation.");
            return;
        }
        
        System.out.println("\nFound " + students.size() + " students for batch processing.");
        
        System.out.println("\nReport type:");
        System.out.println("1. Summary reports");
        System.out.println("2. Detailed reports");
        System.out.print("Select type (1-2): ");
        
        int reportTypeChoice = getIntInput();
        String reportType = reportTypeChoice == 1 ? "summary" : "detailed";
        
        System.out.print("Enter number of threads (2-8): ");
        int threadCount = getIntInput();
        
        try {
            List<String> studentIds = students.stream()
                .map(Student::getStudentId)
                .collect(java.util.stream.Collectors.toList());
                
            BatchReportResult result = concurrentReportGenerator.generateBatchReports(studentIds, reportType, threadCount);
            auditLogger.log("BATCH_REPORT", "Generated " + reportType + " reports", System.currentTimeMillis() - startTime, true, "Count: " + result.getSuccessfulReports() + ", Time: " + result.getTotalTimeMs() + "ms");
            
            System.out.println("\n✓ Batch report generation completed!");
            System.out.println("Total reports: " + result.getTotalReports());
            System.out.println("Successful: " + result.getSuccessfulReports());
            System.out.println("Failed: " + result.getFailedReports());
            System.out.println("Total time: " + result.getTotalTimeMs() + "ms");
            System.out.println("Threads used: " + result.getThreadsUsed());
        } catch (Exception e) {
            System.out.println("\n✗ Error generating batch reports: " + e.getMessage());
        }
    }

    private static void realTimeStatisticsDashboard() {
        statisticsDashboard.startDashboard();
    }

    private static void scheduledTasksManager() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              SCHEDULED TASKS MANAGER                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n1. View All Scheduled Tasks");
        System.out.println("2. Add Custom Task");
        System.out.println("3. Configure & Schedule GPA Recalculation");
        System.out.println("4. Return to Main Menu");
        System.out.print("\nSelect option (1-4): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                taskScheduler.displayAllTasks();
                break;
            case 2:
                taskScheduler.addCustomTask();
                break;
            case 3:
                taskScheduler.scheduleGPARecalculationWithConfig();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void advancedPatternSearch() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           ADVANCED PATTERN SEARCH                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("Search by:");
        System.out.println("1. Email Domain Pattern (e.g., school.edu)");
        System.out.println("2. Phone Area Code Pattern (e.g., 555)");
        System.out.println("3. Student ID Pattern (e.g., STU0**)");
        System.out.println("4. Name Pattern (e.g., son)");
        System.out.println("5. Custom Regex Pattern");
        System.out.println("6. Return to Main Menu");
        System.out.print("\nSelect option (1-6): ");
        
        int choice = getIntInput();
        
        try {
            PatternSearchResult result = null;
            boolean caseInsensitive = false;
            
            switch (choice) {
                case 1:
                    System.out.print("Enter email domain pattern (e.g., school.edu): ");
                    String domain = scanner.nextLine();
                    System.out.print("Case insensitive? (Y/N): ");
                    caseInsensitive = scanner.nextLine().equalsIgnoreCase("Y");
                    result = patternSearchService.searchByEmailDomain(domain, caseInsensitive);
                    break;
                    
                case 2:
                    System.out.print("Enter phone area code (e.g., 1-555): ");
                    String areaCode = scanner.nextLine();
                    result = patternSearchService.searchByPhoneAreaCode(areaCode);
                    break;
                    
                case 3:
                    System.out.print("Enter student ID pattern (use * for wildcard, e.g., STU0**): ");
                    String idPattern = scanner.nextLine();
                    result = patternSearchService.searchByStudentIdPattern(idPattern);
                    break;
                    
                case 4:
                    System.out.print("Enter name pattern (e.g., son): ");
                    String namePattern = scanner.nextLine();
                    System.out.print("Case insensitive? (Y/N): ");
                    caseInsensitive = scanner.nextLine().equalsIgnoreCase("Y");
                    result = patternSearchService.searchByNamePattern(namePattern, caseInsensitive);
                    break;
                    
                case 5:
                    System.out.print("Enter custom regex pattern: ");
                    String customPattern = scanner.nextLine();
                    System.out.println("\nPattern Complexity: " + patternSearchService.analyzePatternComplexity(customPattern));
                    System.out.print("\nSearch in field (email/phone/id/name): ");
                    String field = scanner.nextLine();
                    System.out.print("Case insensitive? (Y/N): ");
                    caseInsensitive = scanner.nextLine().equalsIgnoreCase("Y");
                    result = patternSearchService.searchByCustomPattern(customPattern, field, caseInsensitive);
                    break;
                    
                case 6:
                    return;
                    
                default:
                    System.out.println("Invalid choice!");
                    return;
            }
            
            if (result != null) {
                patternSearchService.displaySearchResults(result);
                
                if (!result.getMatches().isEmpty()) {
                    System.out.println("\n═══════════════════════════════════════════════════════════════");
                    System.out.println("BULK OPERATIONS:");
                    System.out.println("1. Export matched students");
                    System.out.println("2. Generate reports for matched students");
                    System.out.println("3. Export search results to file");
                    System.out.println("4. Return to menu");
                    System.out.print("\nSelect option (1-4): ");
                    
                    int bulkChoice = getIntInput();
                    
                    switch (bulkChoice) {
                        case 1:
                            System.out.print("Enter filename for export: ");
                            String filename = scanner.nextLine();
                            List<Student> matchedStudents = new ArrayList<>();
                            for (PatternSearchResult.StudentMatch match : result.getMatches()) {
                                matchedStudents.add(match.getStudent());
                            }
                            fileExporter.exportSearchResults(matchedStudents, filename);
                            System.out.println("✓ Exported " + matchedStudents.size() + " students");
                            break;
                            
                        case 2:
                            System.out.print("Enter number of threads (2-8): ");
                            int threads = getIntInput();
                            List<String> studentIds = new ArrayList<>();
                            for (PatternSearchResult.StudentMatch match : result.getMatches()) {
                                studentIds.add(match.getStudent().getStudentId());
                            }
                            BatchReportResult batchResult = concurrentReportGenerator.generateBatchReports(studentIds, "summary", threads);
                            System.out.println("✓ Generated " + batchResult.getSuccessfulReports() + " reports in " + batchResult.getTotalTimeMs() + "ms");
                            break;
                            
                        case 3:
                            exportPatternSearchResults(result);
                            break;
                            
                        case 4:
                            break;
                    }
                }
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ ERROR: " + e.getMessage());
            System.out.println("Please check your regex pattern syntax.");
        } catch (Exception e) {
            System.out.println("\n✗ ERROR: " + e.getMessage());
        }
    }

    private static void displayDirectoryStatus() {
        System.out.println("\n=== DIRECTORY STATUS ===");
        
        try {
            Path csvDir = Paths.get("./data/csv/");
            Path jsonDir = Paths.get("./data/json/");
            Path binaryDir = Paths.get("./data/binary/");
            
            displayDirInfo("CSV", csvDir);
            displayDirInfo("JSON", jsonDir);
            displayDirInfo("Binary", binaryDir);
            
        } catch (Exception e) {
            System.out.println("Error reading directories: " + e.getMessage());
        }
    }
    
    private static void displayDirInfo(String type, Path dir) throws IOException {
        if (Files.exists(dir)) {
            long fileCount = Files.list(dir).count();
            System.out.printf("%-8s: %d files in %s\n", type, fileCount, dir);
        } else {
            System.out.printf("%-8s: Directory not found\n", type);
        }
    }

    private static void exitApplication() {
        System.out.println("\nThank you for using Enhanced Student Grade Management System!");
        System.out.println("Goodbye!");
        
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
        
        if (taskScheduler != null) {
            taskScheduler.shutdown();
        }
        
        if (enhancedFileOps != null) {
            try {
                enhancedFileOps.close();
            } catch (IOException e) {
                System.err.println("Warning: Error closing file operations: " + e.getMessage());
            }
        }
        
        if (auditLogger != null) {
            auditLogger.shutdown();
        }
        
        scanner.close();
    }

    private static void cacheManagement() {
        long startTime = System.currentTimeMillis();
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CACHE MANAGEMENT                                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("1. View Cache Statistics");
        System.out.println("2. View Cache Contents");
        System.out.println("3. Clear All Caches");
        System.out.println("4. Invalidate Student Cache");
        System.out.println("5. Test LRU Eviction (200 entries)");
        System.out.println("6. Return to Main Menu");
        System.out.print("\nSelect option (1-6): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                cacheManager.displayStatistics();
                auditLogger.log("CACHE_MGMT", "Viewed cache statistics", System.currentTimeMillis() - startTime, true, "");
                break;
            case 2:
                cacheManager.displayCacheContents();
                auditLogger.log("CACHE_MGMT", "Viewed cache contents", System.currentTimeMillis() - startTime, true, "");
                break;
            case 3:
                System.out.print("\nAre you sure you want to clear all caches? (Y/N): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("Y")) {
                    cacheManager.clearAll();
                    auditLogger.log("CACHE_MGMT", "Cleared all caches", System.currentTimeMillis() - startTime, true, "");
                } else {
                    auditLogger.log("CACHE_MGMT", "Clear cancelled", System.currentTimeMillis() - startTime, false, "User cancelled");
                    System.out.println("Operation cancelled.");
                }
                break;
            case 4:
                System.out.print("Enter Student ID to invalidate: ");
                String studentId = scanner.nextLine();
                cacheManager.invalidateStudent(studentId);
                auditLogger.log("CACHE_MGMT", "Invalidated cache for " + studentId, System.currentTimeMillis() - startTime, true, "");
                System.out.println("✓ Cache invalidated for " + studentId);
                break;
            case 5:
                testLRUEviction();
                auditLogger.log("CACHE_MGMT", "LRU eviction test", System.currentTimeMillis() - startTime, true, "200 entries");
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }
    
    private static void streamDataProcessing() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         STREAM-BASED DATA PROCESSING                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("1. Full Demo (All Stream Operations)");
        System.out.println("2. Filter: Honors Students with GPA > 3.5");
        System.out.println("3. Map: Extract All Emails");
        System.out.println("4. Reduce: Calculate Total Grades");
        System.out.println("5. Group Students by Grade Range");
        System.out.println("6. Partition by Passing Status");
        System.out.println("7. Average Grade per Subject");
        System.out.println("8. Extract Unique Course Codes");
        System.out.println("9. Top 5 Students");
        System.out.println("10. Match Operations Demo");
        System.out.println("11. Sequential vs Parallel Comparison");
        System.out.println("12. Stream Processing Performance Test");
        System.out.println("13. Return to Main Menu");
        System.out.print("\nSelect option (1-13): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                streamDataProcessor.displayStreamOperationsDemo();
                break;
            case 2:
                System.out.print("Enter minimum GPA (0.0-4.0): ");
                double minGPA = getDoubleInput();
                List<Student> honorsStudents = streamDataProcessor.findHonorsStudentsAboveGPA(minGPA);
                System.out.println("\nResults: " + honorsStudents.size() + " students found");
                honorsStudents.forEach(s -> System.out.printf("  %s - %s (%.2f%%)\n", 
                    s.getStudentId(), s.getName(), s.calculateAverageGrade()));
                break;
            case 3:
                List<String> emails = streamDataProcessor.extractAllEmails();
                System.out.println("\nExtracted " + emails.size() + " emails:");
                emails.forEach(e -> System.out.println("  " + e));
                break;
            case 4:
                double total = streamDataProcessor.calculateTotalGrades();
                System.out.printf("\nTotal of all grades: %.2f\n", total);
                break;
            case 5:
                Map<String, List<Student>> grouped = streamDataProcessor.groupStudentsByGradeRange();
                System.out.println("\nStudents grouped by grade range:");
                grouped.forEach((range, students) -> {
                    System.out.println("\n" + range + " (" + students.size() + " students):");
                    students.forEach(s -> System.out.printf("  %s - %s (%.2f%%)\n", 
                        s.getStudentId(), s.getName(), s.calculateAverageGrade()));
                });
                break;
            case 6:
                Map<Boolean, List<Student>> partitioned = streamDataProcessor.partitionByPassingStatus();
                System.out.println("\nPassing: " + partitioned.get(true).size() + " students");
                System.out.println("Failing: " + partitioned.get(false).size() + " students");
                break;
            case 7:
                Map<String, Double> avgBySubject = streamDataProcessor.calculateAverageGradePerSubject();
                System.out.println("\nAverage grade per subject:");
                avgBySubject.forEach((subject, avg) -> 
                    System.out.printf("  %s: %.2f%%\n", subject, avg));
                break;
            case 8:
                Set<String> courses = streamDataProcessor.extractUniqueCourses();
                System.out.println("\nUnique course codes: " + courses.size());
                System.out.println("  " + String.join(", ", courses));
                break;
            case 9:
                List<Student> top5 = streamDataProcessor.findTop5Students();
                System.out.println("\nTop 5 Students:");
                for (int i = 0; i < top5.size(); i++) {
                    Student s = top5.get(i);
                    System.out.printf("  %d. %s - %s (%.2f%%)\n", 
                        i + 1, s.getStudentId(), s.getName(), s.calculateAverageGrade());
                }
                break;
            case 10:
                System.out.println("\nMatch Operations:");
                System.out.println("  anyMatch - Has failing students: " + streamDataProcessor.hasFailingStudents());
                System.out.println("  allMatch - All students have grades: " + streamDataProcessor.allStudentsHaveGrades());
                System.out.println("  noneMatch - No students below min GPA: " + streamDataProcessor.noStudentsBelowMinGPA());
                break;
            case 11:
                streamDataProcessor.compareSequentialVsParallel();
                break;
            case 12:
                streamProcessingPerformanceTest();
                break;
            case 13:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void displaySystemPerformance() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              SYSTEM PERFORMANCE                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        System.out.println("MEMORY USAGE:");
        System.out.println("  Total Memory:     " + formatMemory(totalMemory));
        System.out.println("  Used Memory:      " + formatMemory(usedMemory));
        System.out.println("  Free Memory:      " + formatMemory(freeMemory));
        System.out.println("  Max Memory:       " + formatMemory(maxMemory));
        System.out.println();
        System.out.println("SYSTEM INFO:");
        System.out.println("  Available Processors: " + runtime.availableProcessors());
        System.out.println("  Total Students:       " + studentManager.getStudentCount());
        System.out.println("  Active Threads:       " + Thread.activeCount());
    }

    private static void streamProcessingPerformanceTest() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║     STREAM PROCESSING PERFORMANCE TEST                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        try {
            // Step 1: Generate CSV
            System.out.println("Step 1: Generate CSV with 10,000 grade records");
            System.out.print("Generate students.csv? (Y/N): ");
            String generate = scanner.nextLine();
            
            if (generate.equalsIgnoreCase("Y")) {
                CSVGenerator.generateLargeCSV("students.csv", 10000);
            }
            
            // Step 2-4: Import using streaming
            System.out.println("\nStep 2-4: Import using streaming (Files.lines())");
            System.out.print("Enter CSV filename (in ./imports/): ");
            String filename = scanner.nextLine();
            
            String filepath = "./imports/" + filename;
            
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memBefore = runtime.totalMemory() - runtime.freeMemory();
            
            long recordCount = streamDataProcessor.processCSVFileWithStreams(filepath);
            
            long memAfter = runtime.totalMemory() - runtime.freeMemory();
            long memUsed = memAfter - memBefore;
            
            System.out.println("✓ Streaming import completed");
            System.out.println("  Records: " + recordCount);
            System.out.println("  Memory: " + formatMemory(memUsed));
            
            // Step 5-6: Import without streaming
            System.out.println("\nStep 5-6: Import without streaming (load all at once)");
            System.out.print("Continue? (Y/N): ");
            String cont = scanner.nextLine();
            
            if (cont.equalsIgnoreCase("Y")) {
                runtime.gc();
                long memBefore2 = runtime.totalMemory() - runtime.freeMemory();
                long startTime = System.currentTimeMillis();
                
                long count2 = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filepath))
                    .stream().skip(1).filter(l -> !l.trim().isEmpty()).count();
                
                long duration = System.currentTimeMillis() - startTime;
                long memAfter2 = runtime.totalMemory() - runtime.freeMemory();
                long memUsed2 = memAfter2 - memBefore2;
                
                System.out.println("✓ Non-streaming import completed");
                System.out.println("  Records: " + count2);
                System.out.println("  Time: " + duration + "ms");
                System.out.println("  Memory: " + formatMemory(memUsed2));
                
                System.out.println("\nMemory Comparison:");
                System.out.println("  Streaming:     " + formatMemory(memUsed));
                System.out.println("  Non-streaming: " + formatMemory(memUsed2));
                System.out.println("  Difference:    " + formatMemory(memUsed2 - memUsed));
            }
            
            // Step 7-10: Parallel vs Sequential
            System.out.println("\nStep 7-10: Parallel vs Sequential Comparison");
            System.out.print("Run comparison? (Y/N): ");
            String runComp = scanner.nextLine();
            
            if (runComp.equalsIgnoreCase("Y")) {
                streamDataProcessor.compareSequentialVsParallel();
            }
            
            System.out.println("\n✓ Performance test completed!");
            
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private static void testLRUEviction() {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("           LRU EVICTION TEST");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        System.out.println("Adding 200 entries to student cache (max: 150)...");
        
        String[] firstNames = {"Alice", "Bob", "Carol", "David", "Emma", "Frank", "Grace", "Henry", "Iris", "Jack"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson"};
        
        for (int i = 0; i < 200; i++) {
            String name = firstNames[i % firstNames.length] + " " + lastNames[(i / 10) % lastNames.length];
            String email = "user" + i + "@test.edu";
            String phone = String.format("%03d-%03d-%04d", (i % 900) + 100, (i % 900) + 100, i % 10000);
            
            Student dummy = new RegularStudent(name, 20, email, phone);
            cacheManager.putStudent("TEST" + String.format("%03d", i), dummy);
            
            if ((i + 1) % 50 == 0) {
                System.out.println("  Added " + (i + 1) + " entries...");
            }
        }
        
        System.out.println("\n✓ Test complete!");
        System.out.println("Expected: ~50 evictions (200 added - 150 max capacity)\n");
        cacheManager.displayStatistics();
    }

    private static void auditTrailViewer() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              AUDIT TRAIL VIEWER                               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("1. View Recent Entries (Last 50)");
        System.out.println("2. Search by Operation Type");
        System.out.println("3. Search by Thread ID");
        System.out.println("4. Search by Date Range");
        System.out.println("5. View Audit Statistics");
        System.out.println("6. Return to Main Menu");
        System.out.print("\nSelect option (1-6): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                displayRecentEntries();
                break;
            case 2:
                searchByOperationType();
                break;
            case 3:
                searchByThreadId();
                break;
            case 4:
                searchByDateRange();
                break;
            case 5:
                displayAuditStatistics();
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }
    
    private static void displayRecentEntries() {
        List<AuditEntry> entries = auditLogger.getRecentEntries(50);
        
        if (entries.isEmpty()) {
            System.out.println("\nNo audit entries found.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("RECENT AUDIT ENTRIES (Last " + entries.size() + ")");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        for (AuditEntry entry : entries) {
            System.out.printf("%s | Thread-%d | %s | %s | %dms | %s\n",
                entry.getTimestamp().substring(11, 19),
                entry.getThreadId(),
                entry.getOperationType(),
                entry.getUserAction(),
                entry.getExecutionTimeMs(),
                entry.isSuccess() ? "✓" : "✗");
        }
    }
    
    private static void searchByOperationType() {
        System.out.print("\nEnter operation type (ADD_STUDENT/RECORD_GRADE/VIEW_STUDENTS/SEARCH/EXPORT/etc): ");
        String operationType = scanner.nextLine();
        
        List<AuditEntry> entries = auditLogger.searchByOperationType(operationType);
        
        if (entries.isEmpty()) {
            System.out.println("\nNo entries found for operation type: " + operationType);
            return;
        }
        
        System.out.println("\nFound " + entries.size() + " entries for " + operationType + ":\n");
        for (AuditEntry entry : entries) {
            System.out.println(entry.toLogString());
        }
    }
    
    private static void searchByThreadId() {
        System.out.print("\nEnter Thread ID: ");
        long threadId = Long.parseLong(scanner.nextLine());
        
        List<AuditEntry> entries = auditLogger.searchByThreadId(threadId);
        
        if (entries.isEmpty()) {
            System.out.println("\nNo entries found for Thread-" + threadId);
            return;
        }
        
        System.out.println("\nFound " + entries.size() + " entries for Thread-" + threadId + ":\n");
        for (AuditEntry entry : entries) {
            System.out.println(entry.toLogString());
        }
    }
    
    private static void searchByDateRange() {
        System.out.println("\nEnter start date/time (YYYY-MM-DDTHH:MM:SS): ");
        String startStr = scanner.nextLine();
        System.out.println("Enter end date/time (YYYY-MM-DDTHH:MM:SS): ");
        String endStr = scanner.nextLine();
        
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startStr);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endStr);
            
            List<AuditEntry> entries = auditLogger.searchByDateRange(start, end);
            
            if (entries.isEmpty()) {
                System.out.println("\nNo entries found in date range.");
                return;
            }
            
            System.out.println("\nFound " + entries.size() + " entries:\n");
            for (AuditEntry entry : entries) {
                System.out.println(entry.toLogString());
            }
        } catch (Exception e) {
            System.out.println("\n✗ Invalid date format: " + e.getMessage());
        }
    }
    
    private static void displayAuditStatistics() {
        Map<String, Object> stats = auditLogger.getStatistics();
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("AUDIT STATISTICS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        System.out.println("Total Operations: " + stats.get("totalOperations"));
        System.out.printf("Average Execution Time: %.2fms\n", (Double) stats.get("averageExecutionTime"));
        System.out.printf("Success Rate: %.1f%%\n", (Double) stats.get("successRate"));
        
        System.out.println("\nOperation Breakdown:");
        @SuppressWarnings("unchecked")
        Map<String, Long> breakdown = (Map<String, Long>) stats.get("operationBreakdown");
        if (breakdown != null) {
            for (Map.Entry<String, Long> entry : breakdown.entrySet()) {
                System.out.printf("  %-20s: %d operations\n", entry.getKey(), entry.getValue());
            }
        }
    }
    
    private static void exportPatternSearchResults(PatternSearchResult result) {
        System.out.print("\nEnter filename (without extension): ");
        String filename = scanner.nextLine();
        
        try {
            Path reportsDir = Paths.get("./reports");
            Files.createDirectories(reportsDir);
            Path outputFile = reportsDir.resolve(filename + "_pattern_search.txt");
            
            StringBuilder content = new StringBuilder();
            content.append("═══════════════════════════════════════════════════════════════\n");
            content.append("           PATTERN SEARCH RESULTS EXPORT\n");
            content.append("═══════════════════════════════════════════════════════════════\n\n");
            
            content.append("Search Pattern: ").append(result.getPattern()).append("\n");
            content.append("Total Students Scanned: ").append(result.getTotalScanned()).append("\n");
            content.append("Matches Found: ").append(result.getMatches().size()).append("\n");
            content.append("Search Time: ").append(result.getSearchTimeMs()).append("ms\n");
            content.append("Export Date: ").append(java.time.LocalDateTime.now()).append("\n\n");
            
            content.append("───────────────────────────────────────────────────────────────\n");
            content.append("MATCHED STUDENTS\n");
            content.append("───────────────────────────────────────────────────────────────\n\n");
            
            for (PatternSearchResult.StudentMatch match : result.getMatches()) {
                Student student = match.getStudent();
                content.append("Student ID: ").append(student.getStudentId()).append("\n");
                content.append("Name: ").append(student.getName()).append("\n");
                content.append("Type: ").append(student.getStudentType()).append("\n");
                content.append("Email: ").append(student.getEmail()).append("\n");
                content.append("Phone: ").append(student.getPhone()).append("\n");
                content.append("GPA: ").append(String.format("%.2f%%", student.calculateAverageGrade())).append("\n");
                content.append("Matched Field: ").append(match.getField()).append("\n");
                content.append("Matched Text: ").append(match.getHighlightedText()).append("\n");
                content.append("\n");
            }
            
            if (!result.getDistributionStats().isEmpty()) {
                content.append("───────────────────────────────────────────────────────────────\n");
                content.append("DISTRIBUTION STATISTICS\n");
                content.append("───────────────────────────────────────────────────────────────\n\n");
                
                for (Map.Entry<String, Integer> entry : result.getDistributionStats().entrySet()) {
                    content.append(entry.getKey()).append(": ").append(entry.getValue()).append(" students\n");
                }
            }
            
            content.append("\n═══════════════════════════════════════════════════════════════\n");
            content.append("End of Report\n");
            content.append("═══════════════════════════════════════════════════════════════\n");
            
            Files.write(outputFile, content.toString().getBytes());
            
            System.out.println("\n✓ Search results exported successfully!");
            System.out.println("File: " + outputFile.getFileName());
            System.out.println("Location: ./reports/");
            System.out.println("Matches exported: " + result.getMatches().size());
            
        } catch (IOException e) {
            System.out.println("\n✗ Error exporting search results: " + e.getMessage());
        }
    }

    // Validation methods
    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && NAME_PATTERN.matcher(name).matches();
    }

    private static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty() && PHONE_PATTERN.matcher(phone).matches() && phone.length() >= 7;
    }

    private static int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a valid integer: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                double input = Double.parseDouble(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a valid number: ");
            }
        }
    }
}