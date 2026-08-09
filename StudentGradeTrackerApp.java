package intership;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class StudentGradeTrackerApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static GradeTracker tracker;
    private static final String DATA_FILE = "students.json";

    public static void main(String[] args) {
        System.out.println("\n🎓 Welcome to the Professional Student Grade Tracker v2.1!");
        System.out.println("   Track courses, scores, letter grades, and academic performance effortlessly.\n");

        tracker = GradeTracker.loadFromFile(DATA_FILE);
        if (tracker.getStudentCount() > 0) {
            System.out.println("📂 Loaded " + tracker.getStudentCount() + " students from " + DATA_FILE);
        }

        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = getUserInput("Choose an option").trim();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> addCourseGrades();
                case "3" -> editOrDeleteGrade();
                case "4" -> viewIndividualReport();
                case "5" -> searchByName();
                case "6" -> viewClassSummary();
                case "7" -> {
                    System.out.println("\n✨ Thank you for using the Grade Tracker. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\n⚠️ Invalid option. Please select 1-7.");
            }
        }
        // NEVER close scanner wrapping System.in
    }

    private static void displayMainMenu() {
        System.out.println("\n=========================================");
        System.out.println("     STUDENT GRADE TRACKER SYSTEM v2.1   ");
        System.out.println("=========================================");
        System.out.println("1. Add New Student");
        System.out.println("2. Add Course Grades");
        System.out.println("3. Edit / Delete Course Grade");
        System.out.println("4. View Individual Student Report");
        System.out.println("5. Search Student by Name");
        System.out.println("6. View Class Summary Report");
        System.out.println("7. Exit");
        System.out.println("=========================================");
    }

    private static void addStudent() {
        System.out.println("\n📝 --- Add New Student ---");
        String id = getNonEmptyInput("Enter Student ID");

        if (tracker.findStudentById(id) != null) {
            System.out.println("❌ Error: Student ID '" + id + "' already exists.");
            return;
        }

        String name = getNonEmptyInput("Enter Student Name");
        tracker.addStudent(new Student(id, name));
        saveData();
        System.out.println("✅ Success: Student '" + name + "' registered!");
    }

    private static void addCourseGrades() {
        Student student = findStudentInteractive("Add Course Grades");
        if (student == null) return;

        System.out.println("Enter course details for " + student.getName() + ".");
        System.out.println("Type 'done' or press Enter on empty course to finish.\n");

        boolean modified = false;
        while (true) {
            System.out.print("Course Name: ");
            String course = scanner.nextLine().trim();
            if (course.isEmpty() || course.equalsIgnoreCase("done")) break;

            try {
                double score = getValidScore();
                student.addCourseGrade(course, score);
                System.out.println("✅ Added: " + course + " = " + score +
                        " (" + LetterGrade.fromScore(score) + ")\n");
                modified = true;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage() + "\n");
            }
        }
        if (modified) saveData();
        System.out.println("📊 Finished adding grades for " + student.getName() + ".\n");
    }

    private static void editOrDeleteGrade() {
        Student student = findStudentInteractive("Edit/Delete Grade");
        if (student == null) return;

        List<CourseGrade> grades = student.getCourseGrades();
        if (grades.isEmpty()) {
            System.out.println("📭 No grades to edit or delete.");
            return;
        }

        System.out.println("\n📚 Current Grades for " + student.getName() + ":");
        for (int i = 0; i < grades.size(); i++) {
            CourseGrade cg = grades.get(i);
            System.out.printf("  [%d] %-25s | %6.2f (%s)%n",
                    i + 1, cg.courseName(), cg.score(), LetterGrade.fromScore(cg.score()));
        }

        int index = getIntInRange("Select grade number to modify", 1, grades.size()) - 1;
        CourseGrade selected = grades.get(index);

        System.out.println("\nSelected: " + selected.courseName() + " = " + selected.score());
        System.out.println("  [E]dit score  |  [D]elete  |  [C]ancel");
        String action = getUserInput("Action").trim().toUpperCase();

        switch (action) {
            case "E" -> {
                try {
                    double newScore = getValidScore();
                    student.updateCourseGrade(selected.courseName(), newScore);
                    System.out.println("✅ Updated: " + selected.courseName() + " → " +
                            newScore + " (" + LetterGrade.fromScore(newScore) + ")");
                    saveData();
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ " + e.getMessage());
                }
            }
            case "D" -> {
                student.removeCourseGrade(selected.courseName());
                System.out.println("🗑️ Deleted: " + selected.courseName());
                saveData();
            }
            default -> System.out.println("↩️ Cancelled.");
        }
    }

    private static void viewIndividualReport() {
        Student student = findStudentInteractive("Individual Report");
        if (student == null) return;

        System.out.println("\n─────────────────────────────────────────────────────");
        System.out.printf(" 👤 Student: %-20s | ID: %s%n", student.getName(), student.getId());
        System.out.println("─────────────────────────────────────────────────────");

        if (student.getCourseGrades().isEmpty()) {
            System.out.println(" 📭 No course grades recorded yet.");
        } else {
            System.out.println(" 📚 Course Breakdown:");
            System.out.printf("   %-25s | %6s | %5s%n", "Course", "Score", "Grade");
            System.out.println("   ----------------------------|--------|------");
            for (CourseGrade cg : student.getCourseGrades()) {
                System.out.printf("   %-25s | %6.2f | %5s%n",
                        cg.courseName(), cg.score(), LetterGrade.fromScore(cg.score()));
            }
            System.out.println("   ----------------------------|--------|------");
            StudentStats stats = student.getStats();
            System.out.printf(" 📊 Avg: %.2f (%s) | 📈 High: %.2f | 📉 Low: %.2f%n",
                    stats.average(), LetterGrade.fromScore(stats.average()),
                    stats.max(), stats.min());
        }
        System.out.println("─────────────────────────────────────────────────────\n");
    }

    private static void searchByName() {
        System.out.println("\n🔍 --- Search Student by Name ---");
        String query = getNonEmptyInput("Enter full or partial name");
        List<Student> results = tracker.searchByName(query);

        if (results.isEmpty()) {
            System.out.println("❌ No students found matching '" + query + "'.");
            return;
        }

        System.out.println("\n🎯 Found " + results.size() + " result(s):");
        System.out.println("─────────────────────────────────────────");
        for (Student s : results) {
            int count = s.getCourseGrades().size();
            String avgStr = count > 0 ? String.format("%.2f", s.getStats().average()) : "N/A";
            System.out.printf("  ID: %-8s | Name: %-20s | Courses: %d | Avg: %s%n",
                    s.getId(), s.getName(), count, avgStr);
        }
        System.out.println("─────────────────────────────────────────\n");
    }

    private static void viewClassSummary() {
        System.out.println("\n📊 --- Class Summary Report ---");
        List<Student> students = tracker.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("📭 No students registered yet.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-8s | %-18s | %-7s | %-8s | %-5s | %-8s | %-8s |%n",
                "ID", "Name", "Courses", "Average", "Grade", "Highest", "Lowest");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (Student s : students) {
            int count = s.getCourseGrades().size();
            if (count > 0) {
                StudentStats stats = s.getStats();
                System.out.printf("| %-8s | %-18s | %-7d | %-8.2f | %-5s | %-8.2f | %-8.2f |%n",
                        s.getId(), s.getName(), count, stats.average(),
                        LetterGrade.fromScore(stats.average()), stats.max(), stats.min());
            } else {
                System.out.printf("| %-8s | %-18s | %-7s | %-8s | %-5s | %-8s | %-8s |%n",
                        s.getId(), s.getName(), "0", "N/A", "N/A", "N/A", "N/A");
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------");
        ClassStats cs = tracker.getClassStats();
        System.out.printf(" 🏫 Class Stats → Students: %d | Avg: %.2f (%s) | Top: %.2f | Bottom: %.2f%n",
                students.size(), cs.average(), LetterGrade.fromScore(cs.average()),
                cs.max(), cs.min());
        System.out.println("----------------------------------------------------------------------------------------------------------\n");
    }

    // ================= INPUT HELPERS =================
    private static String getUserInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }

    private static String getNonEmptyInput(String prompt) {
        while (true) {
            String input = getUserInput(prompt).trim();
            if (!input.isEmpty()) return input;
            System.out.println("⚠️ This field cannot be empty. Please try again.");
        }
    }

    private static double getValidScore() {
        while (true) {
            String raw = getUserInput("Score (0.0 - 100.0)").trim();
            if (raw.isEmpty()) throw new IllegalArgumentException("Score entry cancelled.");
            try {
                double score = Double.parseDouble(raw);
                if (score >= 0.0 && score <= 100.0) return score;
                System.out.println("❌ Score must be between 0.0 and 100.0.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number format. Please enter a numeric score.");
            }
        }
    }

    private static int getIntInRange(String prompt, int min, int max) {
        while (true) {
            String raw = getUserInput(prompt).trim();
            try {
                int val = Integer.parseInt(raw);
                if (val >= min && val <= max) return val;
                System.out.println("❌ Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number.");
            }
        }
    }

    private static Student findStudentInteractive(String contextLabel) {
        System.out.println("\n📋 --- " + contextLabel + " ---");
        String id = getUserInput("Enter Student ID").trim();
        Student student = tracker.findStudentById(id);
        if (student == null) {
            System.out.println("❌ Error: Student with ID '" + id + "' not found.");
        }
        return student;
    }

    private static void saveData() {
        tracker.saveToFile(DATA_FILE);
    }
}

// ================= MANAGER LAYER =================
class GradeTracker {
    private final List<Student> students = new ArrayList<>();

    public void addStudent(Student student) { students.add(student); }

    public Student findStudentById(String id) {
        return students.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    public List<Student> searchByName(String query) {
        String lower = query.toLowerCase();
        return students.stream()
                .filter(s -> s.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Student> getAllStudents() { return Collections.unmodifiableList(students); }
    public int getStudentCount() { return students.size(); }

    public ClassStats getClassStats() {
        DoubleSummaryStatistics dss = students.stream()
                .flatMap(s -> s.getCourseGrades().stream())
                .mapToDouble(CourseGrade::score)
                .summaryStatistics();
        return new ClassStats(
                dss.getCount() == 0 ? 0.0 : dss.getAverage(),
                dss.getCount() == 0 ? 0.0 : dss.getMax(),
                dss.getCount() == 0 ? 0.0 : dss.getMin()
        );
    }

    // ================= FILE PERSISTENCE =================
    public void saveToFile(String filename) {
        try {
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                sb.append("  {\"id\":\"").append(escapeJson(s.getId()))
                  .append("\",\"name\":\"").append(escapeJson(s.getName()))
                  .append("\",\"grades\":[");
                List<CourseGrade> grades = s.getCourseGrades();
                for (int j = 0; j < grades.size(); j++) {
                    CourseGrade cg = grades.get(j);
                    sb.append("{\"course\":\"").append(escapeJson(cg.courseName()))
                      .append("\",\"score\":").append(cg.score()).append("}");
                    if (j < grades.size() - 1) sb.append(",");
                }
                sb.append("]}");
                if (i < students.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
            Files.writeString(Path.of(filename), sb.toString());
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not save data: " + e.getMessage());
        }
    }

    public static GradeTracker loadFromFile(String filename) {
        GradeTracker gt = new GradeTracker();
        Path path = Path.of(filename);
        if (!Files.exists(path)) return gt;

        try {
            String json = Files.readString(path);
            int idx = 0;
            while ((idx = json.indexOf("\"id\"", idx)) != -1) {
                String id = extractJsonString(json, idx + 4);
                int nameIdx = json.indexOf("\"name\"", idx);
                String name = extractJsonString(json, nameIdx + 6);
                Student student = new Student(id, name);

                int gradesStart = json.indexOf("\"grades\"", nameIdx);
                int arrStart = json.indexOf('[', gradesStart);
                int arrEnd = findMatchingBracket(json, arrStart);
                String gradesBlock = json.substring(arrStart + 1, arrEnd);

                int gIdx = 0;
                while ((gIdx = gradesBlock.indexOf("\"course\"", gIdx)) != -1) {
                    String course = extractJsonString(gradesBlock, gIdx + 8);
                    int scoreIdx = gradesBlock.indexOf("\"score\"", gIdx);
                    int colonPos = gradesBlock.indexOf(':', scoreIdx + 7);
                    int commaOrEnd = gradesBlock.indexOf(',', colonPos);
                    if (commaOrEnd == -1) commaOrEnd = gradesBlock.indexOf('}', colonPos);
                    double score = Double.parseDouble(gradesBlock.substring(colonPos + 1, commaOrEnd).trim());
                    student.addCourseGrade(course, score);
                    gIdx = commaOrEnd;
                }
                gt.addStudent(student);
                idx = arrEnd;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load data: " + e.getMessage());
        }
        return gt;
    }

    private static String extractJsonString(String json, int keyEnd) {
        int q1 = json.indexOf('"', keyEnd);
        int q2 = json.indexOf('"', q1 + 1);
        return json.substring(q1 + 1, q2);
    }

    private static int findMatchingBracket(String s, int openPos) {
        int depth = 1;
        for (int i = openPos + 1; i < s.length(); i++) {
            if (s.charAt(i) == '[') depth++;
            else if (s.charAt(i) == ']') { depth--; if (depth == 0) return i; }
        }
        return s.length() - 1;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

// ================= MODEL LAYER =================
/**
 * FIXED: Student is now a regular class, NOT a record.
 * Records cannot hold mutable state alongside canonical constructor params.
 */
final class Student {
    private final String id;
    private final String name;
    private final List<CourseGrade> courseGrades = new ArrayList<>();

    public Student(String id, String name) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public List<CourseGrade> getCourseGrades() {
        return Collections.unmodifiableList(courseGrades);
    }

    public void addCourseGrade(String courseName, double score) {
        if (score < 0.0 || score > 100.0)
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        if (hasCourse(courseName))
            throw new IllegalArgumentException("Course '" + courseName + "' already exists. Use Edit instead.");
        courseGrades.add(new CourseGrade(courseName, score));
    }

    public void updateCourseGrade(String courseName, double newScore) {
        if (newScore < 0.0 || newScore > 100.0)
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        for (int i = 0; i < courseGrades.size(); i++) {
            if (courseGrades.get(i).courseName().equalsIgnoreCase(courseName)) {
                courseGrades.set(i, new CourseGrade(courseName, newScore));
                return;
            }
        }
        throw new IllegalArgumentException("Course '" + courseName + "' not found.");
    }

    public void removeCourseGrade(String courseName) {
        courseGrades.removeIf(cg -> cg.courseName().equalsIgnoreCase(courseName));
    }

    public boolean hasCourse(String courseName) {
        return courseGrades.stream()
                .anyMatch(cg -> cg.courseName().equalsIgnoreCase(courseName));
    }

    public StudentStats getStats() {
        DoubleSummaryStatistics dss = courseGrades.stream()
                .mapToDouble(CourseGrade::score)
                .summaryStatistics();
        return new StudentStats(
                dss.getCount() == 0 ? 0.0 : dss.getAverage(),
                dss.getCount() == 0 ? 0.0 : dss.getMax(),
                dss.getCount() == 0 ? 0.0 : dss.getMin()
        );
    }
}

// These CAN be records since they are pure immutable data holders
record CourseGrade(String courseName, double score) {
    public CourseGrade {
        if (score < 0.0 || score > 100.0)
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
    }
}

record StudentStats(double average, double max, double min) {}
record ClassStats(double average, double max, double min) {}

enum LetterGrade {
    A(90.0), B(80.0), C(70.0), D(60.0), F(0.0);

    private final double threshold;
    LetterGrade(double threshold) { this.threshold = threshold; }

    public static String fromScore(double score) {
        for (LetterGrade lg : values()) {
            if (score >= lg.threshold) return lg.name();
        }
        return "F";
    }
}
