package org.example;// Main.java
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = studentManager.getGradeManager();

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+\\d\\s-()]+$");

    public static void main(String[] args) {
        displayMainMenu();
    }

    private static void displayMainMenu() {
        while (true) {
            System.out.println("Bernison");
            System.out.println("\n==========================================");
            System.out.println("    STUDENT GRADE MANAGEMENT - MAIN MENU");
            System.out.println("==========================================");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Record Grade");
            System.out.println("4. View Grade Report");
            System.out.println("5. Export Grade Report");
            System.out.println("6. Calculate Student GPA");
            System.out.println("7. Bulk Import Grades");
            System.out.println("8. View Class Statistics");
            System.out.println("9. Search Students");
            System.out.println("1o. Exit");
            System.out.print("\nEnter choice: ");


            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    recordGrade();
                    break;
                case 4:
                    viewGradeReport();
                    break;
                case 5:

                    //export grade report
                case 6:
                System.out.println("6. Calculate Student GPA");
                break;
                case 7:
                System.out.println("7. Bulk Import Grades");
                break;
                case 8:
                System.out.println("8. View Class Statistics");
                break;
                case 9:
                System.out.println("9. Search Students");
                break;
                case 10:
                System.out.println("10. Exit");
                    exitApplication();
                System.out.print("\nEnter choice: ");
                return;
                default:
                    System.out.println("Invalid choice! Please enter 1-5.");
            }

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine(); 
        }
    }

    private static void addStudent() {
        System.out.println("\nADD STUDENT");
        System.out.println();

        String name;
        while (true) {
            System.out.print("Enter student name: ");
            name = scanner.nextLine().trim();
            if (isValidName(name)) {
                break;
            } else {
                System.out.println("Invalid name! Name should contain only letters and spaces.");
            }
        }

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

        String email;
        while (true) {
            System.out.print("Enter student email: ");
            email = scanner.nextLine().trim();
            if (isValidEmail(email)) {
                break;
            } else {
                System.out.println("Invalid email! Email must contain @ symbol and valid domain.");
            }
        }

        String phone;
        while (true) {
            System.out.print("Enter student phone: ");
            phone = scanner.nextLine().trim();
            if (isValidPhone(phone)) {
                break;
            } else {
                System.out.println("Invalid phone number! Use format like +1-555-1234 or (555) 123-4567.");
            }
        }

        System.out.println("\nStudent type:");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
        System.out.print("\nSelect type (1-2): ");

        int typeChoice = getIntInput();

        Student student;
        if (typeChoice == 1) {
            student = new RegularStudent(name, age, email, phone);
        } else if (typeChoice == 2) {
            student = new HonorsStudent(name, age, email, phone);
        } else {
            System.out.println("Invalid choice! Student not added.");
            return;
        }

        studentManager.addStudent(student);

        System.out.println("\n✓ Student added successfully!");
        student.displayStudentDetails();
    }

    private static void viewStudents() {
        studentManager.viewAllStudents();
    }

    private static void recordGrade() {
        System.out.println("\nRECORD GRADE");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        Student student = studentManager.findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

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
                case 1: subject = new CoreSubject("Mathematics", "MATH101"); break;
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
                case 3: subject = new ElectiveSubject("Physical Education", "PE101"); break;
                default:
                    System.out.println("Invalid choice!");
                    return;
            }
        } else {
            System.out.println("Invalid choice!");
            return;
        }

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
            gradeManager.addGrade(grade);
            student.addGrade(grade);
            System.out.println("\n✓ Grade recorded successfully!");
        } else {
            System.out.println("Grade recording cancelled.");
        }
    }

    private static void viewGradeReport() {
        System.out.println("\nVIEW GRADE REPORT");
        System.out.println();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        Student student = studentManager.findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");
        System.out.println("Passing Grade: " + student.getPassingGrade() + "%");

        Grade[] allGrades = gradeManager.getGrades();
        boolean hasGrades = false;

        for (int i = 0; i < gradeManager.getGradeCount(); i++) {
            if (allGrades[i] != null && allGrades[i].getStudentId().equals(studentId)) {
                hasGrades = true;
                break;
            }
        }

        if (!hasGrades) {
            System.out.println("\nNo grades recorded for this student.");
        } else {
            System.out.printf("Current Average: %.1f%%\n", student.calculateAverageGrade());
            System.out.println("Status: " + (student.isPassing() ? "PASSING ✓" : "FAILING"));

            gradeManager.viewGradesByStudent(studentId);

            double coreAvg = gradeManager.calculateCoreAverage(studentId);
            double electiveAvg = gradeManager.calculateElectiveAverage(studentId);
            double overallAvg = gradeManager.calculateOverallAverage(studentId);

            System.out.println("\nTotal Grades: " + countStudentGrades(studentId));
            System.out.printf("Core Subjects Average: %.1f%%\n", coreAvg);
            System.out.printf("Elective Subjects Average: %.1f%%\n", electiveAvg);
            System.out.printf("Overall Average: %.1f%%\n", overallAvg);

            System.out.println("\nPerformance Summary:");
            if (student.isPassing()) {
                System.out.println("✓ Meeting passing grade requirement (" + student.getPassingGrade() + "%)");
            } else {
                System.out.println("✗ Not meeting passing grade requirement (" + student.getPassingGrade() + "%)");
            }

            if (coreAvg >= student.getPassingGrade()) {
                System.out.println("✓ Passing all core subjects");
            } else {
                System.out.println("✗ Not passing all core subjects");
            }
        }
    }

    private static int countStudentGrades(String studentId) {
        int count = 0;
        Grade[] allGrades = gradeManager.getGrades();
        for (int i = 0; i < gradeManager.getGradeCount(); i++) {
            if (allGrades[i] != null && allGrades[i].getStudentId().equals(studentId)) {
                count++;
            }
        }
        return count;
    }

    private static void exitApplication() {
        System.out.println("\nThank you for using Student Grade Management System!");
        System.out.println("Goodbye!");
    }

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