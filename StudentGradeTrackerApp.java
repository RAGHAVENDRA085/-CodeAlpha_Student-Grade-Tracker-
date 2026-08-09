package intership;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application Class (View / UI Layer)
 * Handles user interaction, menu navigation, and input validation.
 */
public class StudentGradeTrackerApp {
    private static Scanner scanner = new Scanner(System.in);
    private static GradeTracker tracker = new GradeTracker();

    public static void main(String[] args) {
        System.out.println("\nWelcome to the Professional Student Grade Tracker!");
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = getUserInput("Choose an option");

            switch (choice) {
                case "1": addStudent(); break;
                case "2": addGrades(); break;
                case "3": viewIndividualReport(); break;
                case "4": viewClassSummary(); break;
                case "5": 
                    System.out.println("\nThank you for using the Grade Tracker. Goodbye!");
                    running = false; 
                    break;
                default: 
                    System.out.println("\n[Error] Invalid option. Please select 1-5.");
            }
        }
        scanner.close();
    }

    // --- UI & Menu Methods ---

    private static void displayMainMenu() {
        System.out.println("\n=========================================");
        System.out.println("       STUDENT GRADE TRACKER SYSTEM      ");
        System.out.println("=========================================");
        System.out.println("1. Add New Student");
        System.out.println("2. Add Grades for a Student");
        System.out.println("3. View Individual Student Report");
        System.out.println("4. View Class Summary Report");
        System.out.println("5. Exit");
        System.out.println("=========================================");
    }

    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");
        String id = getUserInput("Enter Student ID");
        
        // Check for duplicate IDs
        if (tracker.findStudentById(id) != null) {
            System.out.println("[Error] A student with ID '" + id + "' already exists.");
            return;
        }
        
        String name = getUserInput("Enter Student Name");
        tracker.addStudent(new Student(id, name));
        System.out.println("[Success] Student '" + name + "' added successfully!");
    }

    private static void addGrades() {
        System.out.println("\n--- Add Grades ---");
        String id = getUserInput("Enter Student ID");
        Student student = tracker.findStudentById(id);

        if (student == null) {
            System.out.println("[Error] Student not found.");
            return;
        }

        System.out.println("Enter grades for " + student.getName() + " (0.0 - 100.0).");
        // UPDATED: Clearer instructions for the user
        System.out.println("Type 'done' or just press [Enter] to finish.");

        while (true) {
            System.out.print("Enter grade: ");
            String input = scanner.nextLine().trim().toLowerCase();

            // FIX: If the user just presses Enter (empty string) or types 'done', exit the loop
            if (input.isEmpty() || input.equals("done")) {
                break;
            }

            try {
                double grade = Double.parseDouble(input);
                if (grade >= 0.0 && grade <= 100.0) {
                    student.addGrade(grade);
                    System.out.println("  -> Grade " + grade + " added.");
                } else {
                    System.out.println("[Error] Grade must be between 0.0 and 100.0.");
                }
            } catch (NumberFormatException e) {
                // UPDATED: Better error message guiding the user
                System.out.println("[Error] Invalid input. Please enter a number, 'done', or press Enter to finish.");
            }
        }
        // UPDATED: Confirmation message
        System.out.println("[Success] Finished adding grades for " + student.getName() + ".");
    }

    private static void viewIndividualReport() {
        System.out.println("\n--- Individual Student Report ---");
        String id = getUserInput("Enter Student ID");
        Student student = tracker.findStudentById(id);

        if (student == null) {
            System.out.println("[Error] Student not found.");
            return;
        }

        System.out.println("\n-----------------------------------------");
        System.out.printf(" Student: %-15s ID: %s\n", student.getName(), student.getId());
        System.out.println("-----------------------------------------");
        
        if (student.getGrades().isEmpty()) {
            System.out.println(" No grades recorded yet.");
        } else {
            System.out.printf(" Grades Recorded : %d\n", student.getGrades().size());
            System.out.printf(" Average Score   : %.2f\n", student.getAverage());
            System.out.printf(" Highest Score   : %.2f\n", student.getHighest());
            System.out.printf(" Lowest Score    : %.2f\n", student.getLowest());
        }
        System.out.println("-----------------------------------------");
    }

    private static void viewClassSummary() {
        System.out.println("\n--- Class Summary Report ---");
        List<Student> students = tracker.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("[Info] No students in the system.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-8s | %-8s | %-8s | %-8s |\n", 
                "ID", "Name", "Grades", "Average", "Highest", "Lowest");
        System.out.println("---------------------------------------------------------------------------------");

        for (Student s : students) {
            if (!s.getGrades().isEmpty()) {
                System.out.printf("| %-10s | %-20s | %-8d | %-8.2f | %-8.2f | %-8.2f |\n",
                        s.getId(), s.getName(), s.getGrades().size(), 
                        s.getAverage(), s.getHighest(), s.getLowest());
            } else {
                System.out.printf("| %-10s | %-20s | %-8s | %-8s | %-8s | %-8s |\n",
                        s.getId(), s.getName(), "0", "N/A", "N/A", "N/A");
            }
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf(" Class Statistics -> Total Students: %d | Class Average: %.2f | Class Highest: %.2f | Class Lowest: %.2f\n", 
                students.size(), tracker.getClassAverage(), tracker.getClassHighest(), tracker.getClassLowest());
        System.out.println("---------------------------------------------------------------------------------");
    }

    // --- Utility Methods ---

    private static String getUserInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
}

/**
 * Manager Class (Controller Layer)
 * Manages the collection of students and calculates overall class statistics.
 */
class GradeTracker {
    private List<Student> students;

    public GradeTracker() {
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public double getClassAverage() {
        List<Double> allGrades = new ArrayList<>();
        for (Student s : students) allGrades.addAll(s.getGrades());
        if (allGrades.isEmpty()) return 0.0;
        
        double sum = 0;
        for (double g : allGrades) sum += g;
        return sum / allGrades.size();
    }

    public double getClassHighest() {
        List<Double> allGrades = new ArrayList<>();
        for (Student s : students) allGrades.addAll(s.getGrades());
        return allGrades.isEmpty() ? 0.0 : Collections.max(allGrades);
    }

    public double getClassLowest() {
        List<Double> allGrades = new ArrayList<>();
        for (Student s : students) allGrades.addAll(s.getGrades());
        return allGrades.isEmpty() ? 0.0 : Collections.min(allGrades);
    }
}

/**
 * Model Class (Data Layer)
 * Represents a single student and encapsulates their grade data and calculations.
 */
class Student {
    private String id;
    private String name;
    private List<Double> grades;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Double> getGrades() { return grades; }

    public void addGrade(double grade) {
        grades.add(grade);
    }

    public double getAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (double g : grades) sum += g;
        return sum / grades.size();
    }

    public double getHighest() {
        return grades.isEmpty() ? 0.0 : Collections.max(grades);
    }

    public double getLowest() {
        return grades.isEmpty() ? 0.0 : Collections.min(grades);
    }
}
