# 📊 Student Grade Tracker

> A robust, console-based Java application for managing student records and calculating academic performance metrics in real-time.

[![Java](https://img.shields.io/badge/Java-8%2B-blue)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-green)]()
[![Build](https://img.shields.io/badge/Status-Passing-success)]()

## 📌 About This Project
This application provides a complete solution for tracking, managing, and analyzing student grades. Designed with **Object-Oriented Programming (OOP)** principles, it features a clean console interface, dynamic data storage, input validation, and automated statistical reporting. Ideal for academic submissions, coursework, or portfolio demonstrations.

## ✅ Requirement Mapping (All 5 Tasks Implemented)

| # | Task Requirement | Implementation Details |
|---|------------------|------------------------|
| **1** | Input & manage student grades | Interactive CLI with `Scanner`-driven workflows for adding students and assigning multiple grades per student. |
| **2** | Calculate average, highest & lowest scores | Real-time computation using `Collections.max()`, `Collections.min()`, and arithmetic averaging. Applies to both individual students and the entire class. |
| **3** | Use arrays or ArrayLists | Dynamic `ArrayList<Double>` for grades and `ArrayList<Student>` for student records. Eliminates fixed-size limitations and scales automatically. |
| **4** | Display summary report of all students | Formatted ASCII tabular report (Option 4) showing per-student metrics and aggregate class statistics. |
| **5** | Console-based or GUI interface | Polished, user-friendly **Console UI** with structured menus, clear prompts, and graceful input handling. |

## 🌟 Key Features
- 🔹 **Dynamic Data Storage:** Uses `ArrayList` for unlimited student & grade capacity.
- 🔹 **Real-Time Analytics:** Instantly calculates averages, highest, and lowest scores.
- 🔹 **Robust Input Validation:** Prevents crashes from non-numeric input, enforces `0.0–100.0` grade boundaries, and handles empty submissions gracefully.
- 🔹 **Clean Console UI:** Professionally formatted tables, clear `[Success]`/`[Error]` messages, and intuitive navigation.
- 🔹 **OOP Architecture:** Separated into `Model` (Student), `Controller` (GradeTracker), and `View` (Main App) for maintainability and scalability.

## 🏗️ Project Structure
student-grade-tracker/
├── 📁 src/
│   ├── 📄 StudentGradeTrackerApp.java   # 🖥️ Main UI, Menu Navigation & Input Validation
│   ├── 📄 GradeTracker.java             # 📊 Controller: Manages student list & class-wide stats
│   └── 📄 Student.java                  # 📝 Model: Encapsulates student data & grade calculations
├── 📄 README.md                         # 📖 Project documentation & setup guide
└── 📄 .gitignore                        # 🚫 Excludes compiled .class files & IDE configs
