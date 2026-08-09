# 🎓 Student Grade Tracker Pro

[![Java](https://img.shields.io/badge/Java-16%2B-orange?logo=openjdk&logoColor=white)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Architecture](https://img.shields.io/badge/Pattern-MVC-green)]()
[![Status](https://img.shields.io/badge/Status-Production--Ready-brightgreen)]()

> A production-grade Java application for tracking student academic performance. Demonstrates immutable data modeling, single-pass stream optimization, zero-dependency JSON persistence, and defensive MVC architecture using modern Java 16+ features.

## ✅ Project Requirements Fulfillment

This project was built to satisfy the following specifications:

| Requirement | Implementation | Location |
| :--- | :--- | :--- |
| **Input and manage student grades** | Full CRUD: add students, add/edit/delete course grades with model-layer validation | `StudentGradeTrackerApp.java`, `Student.java` |
| **Calculate average, highest, and lowest scores** | Single-pass `DoubleSummaryStatistics` computes all three metrics simultaneously per student and per class | `Student.getStats()`, `GradeTracker.getClassStats()` |
| **Use arrays or ArrayLists to store and manage data** | `ArrayList<CourseGrade>` inside each `Student`; `ArrayList<Student>` inside `GradeTracker`; exposed as unmodifiable lists for safety | `Student.java`, `GradeTracker.java` |
| **Display a summary report of all students** | Formatted console table showing ID, name, course count, average, letter grade, highest, and lowest for every student plus class-wide aggregates | `viewClassSummary()` in `StudentGradeTrackerApp.java` |
| **Console-based or GUI-based interface** | Console-based interactive menu with emoji indicators, aligned tables, input validation loops, and safe `System.in` handling | `StudentGradeTrackerApp.java` |

## ✨ Features Beyond Requirements

| Feature | Description |
| :--- | :--- |
| **Letter Grades** | Automatic A/B/C/D/F mapping displayed in all reports and summaries |
| **Fuzzy Search** | Case-insensitive partial name matching across the entire student roster |
| **JSON Persistence** | Auto-save/load to `students.json` on every mutation — no external libraries |
| **Edit & Delete** | Modify or remove individual course grades without re-entering all data |
| **Model Validation** | Score bounds & duplicate checks enforced at model layer; invalid state impossible |
| **Safe I/O** | Never closes `System.in`; robust input loops with type-safe parsing |

## 🏗️ Architecture

```text
┌─────────────────────────────────┐
│   StudentGradeTrackerApp (UI)   │ ← Console I/O, Menu Routing, Input Helpers
├─────────────────────────────────┤
│      GradeTracker (Manager)     │ ← ArrayList Storage, Search, Analytics, Persistence
├─────────────────────────────────┤
│  Student (Model)                │ ← ArrayList<CourseGrade>, validated mutations
│  CourseGrade (Record)           │ ← Immutable validated value object
│  StudentStats / ClassStats      │ ← Computed aggregate records (avg/high/low)
│  LetterGrade (Enum)             │ ← Score-to-grade threshold mapping
└─────────────────────────────────┘
