# Employee Management System (EMS)

A fully functional desktop **Employee Management System** built with **Java Swing + MySQL**.

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| JDK         | 17 or 21 |
| MySQL       | 8.x |
| Maven       | 3.6+ |

---

## Database Setup

1. Open MySQL Workbench (or any MySQL client).
2. Run the provided SQL script:

```sql
SOURCE /path/to/database.sql;
```

Or paste the contents of `database.sql` directly into your client.

This creates:
- Database: `employee_management_system`
- Tables: `users`, `departments`, `employees`
- Sample data: 2 users, 5 departments, 8 employees

**Default login credentials:**

| Username | Password |
|----------|----------|
| admin    | admin123 |
| hr       | hr2024   |

---

## Configure Database Connection

Edit `src/main/java/database/DatabaseConnection.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/employee_management_system";
private static final String USER     = "root";      // ← your MySQL username
private static final String PASSWORD = "root";      // ← your MySQL password
```

---

## Build & Run

### Option A — Maven (Recommended)

```bash
# From the project root directory:
mvn compile

# Run directly with Maven
mvn exec:java -Dexec.mainClass="Main"

# Or package into a JAR first
mvn package
java -jar target/EmployeeManagementSystem-1.0.jar
```

### Option B — Manual Compilation (without Maven)

1. Download `mysql-connector-j-8.4.0.jar` from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/).
2. Compile all source files:

```bash
javac -cp ".;mysql-connector-j-8.4.0.jar" -d out ^
  src/main/java/Main.java ^
  src/main/java/model/*.java ^
  src/main/java/database/*.java ^
  src/main/java/util/*.java ^
  src/main/java/ui/*.java
```

3. Run:

```bash
java -cp ".;out;mysql-connector-j-8.4.0.jar" Main
```

---

## Project Structure

```
SCD project/
├── database.sql                          # DB schema + sample data
├── pom.xml                               # Maven build config
├── README.md
└── src/
    └── main/
        └── java/
            ├── Main.java                 # Entry point
            ├── model/
            │   ├── Employee.java
            │   ├── Department.java
            │   └── User.java
            ├── database/
            │   ├── DatabaseConnection.java   # Singleton JDBC connection
            │   ├── BaseDAO.java              # Abstract DAO
            │   ├── EmployeeDAO.java
            │   ├── DepartmentDAO.java
            │   └── UserDAO.java
            ├── util/
            │   ├── InputValidator.java
            │   └── ReportGenerator.java
            └── ui/
                ├── LoginFrame.java           # Login screen
                ├── DashboardFrame.java       # Main shell + CardLayout
                ├── HomePanel.java            # Dashboard stats & quick actions
                ├── EmployeeTablePanel.java   # View all employees (JTable)
                ├── EmployeeFormPanel.java    # Add / Edit employee form
                ├── SearchPanel.java          # Search by ID / name / department
                ├── DepartmentPanel.java      # Department CRUD
                └── ReportsPanel.java         # Dept summary + salary reports
```

---

## Features

| # | Feature | Status |
|---|---------|--------|
| 1 | Login with username/password authentication | ✅ |
| 2 | Add Employee with validation + duplicate ID check | ✅ |
| 3 | View All Employees — sortable JTable | ✅ |
| 4 | Update Employee — pre-filled edit form | ✅ |
| 5 | Delete Employee — confirmation dialog | ✅ |
| 6 | Search — by ID, name, designation, or department | ✅ |
| 7 | Department Management — add, edit, delete | ✅ |
| 8 | Reports — dept summary + salary range (print & export) | ✅ |
| 9 | Dashboard — live stats (employee count, dept count, latest hire) | ✅ |
| 10 | Input Validation — email, phone, salary, date, empty fields | ✅ |
| 11 | Exception Handling — all JDBC ops wrapped in try-catch | ✅ |
| 12 | Logout — session cleanup, returns to login screen | ✅ |

---

## OOP Concepts Used

- **Encapsulation** — private fields with getters/setters in all model classes
- **Inheritance** — `EmployeeDAO`, `DepartmentDAO`, `UserDAO` extend `BaseDAO<T, ID>`
- **Abstraction** — `BaseDAO` is abstract; all concrete DAOs implement its methods
- **Polymorphism** — method overriding in DAO subclasses; `searchEmployee()` overloaded (int vs String)
- **Constructors** — parameterized constructors in all model classes

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Communications link failure` | MySQL is not running or URL/port is wrong |
| `Access denied for user 'root'` | Wrong MySQL username/password in `DatabaseConnection.java` |
| `Unknown database` | Run `database.sql` first |
| UI looks plain | Ensure you are on Windows (System L&F is Windows-native) |
| Emoji not displaying in buttons | Use JDK 17+ and ensure a Unicode font is installed |
