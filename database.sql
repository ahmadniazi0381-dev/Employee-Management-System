CREATE DATABASE IF NOT EXISTS employee_management_system;

USE employee_management_system;

-- ─── Users ───────────────────────────────────────────────────────────────────
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ─── Departments ─────────────────────────────────────────────────────────────
CREATE TABLE departments (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- ─── Employees ───────────────────────────────────────────────────────────────
CREATE TABLE employees (
    id            INT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    department_id INT          NOT NULL,
    designation   VARCHAR(100) NOT NULL,
    salary        DECIMAL(10,2) NOT NULL,
    hire_date     DATE         NOT NULL,
    email         VARCHAR(100) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,

    CONSTRAINT fk_dept
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
        ON DELETE RESTRICT
);

-- ─── Sample Data ─────────────────────────────────────────────────────────────
INSERT INTO users (username, password) VALUES
    ('admin', 'admin123'),
    ('hr',    'hr2024');

INSERT INTO departments (name) VALUES
    ('Human Resources'),
    ('Information Technology'),
    ('Finance'),
    ('Marketing'),
    ('Operations');

INSERT INTO employees (id, name, department_id, designation, salary, hire_date, email, phone) VALUES
    (1001, 'Ali Khan',       2, 'Software Engineer',    85000.00, '2024-01-15', 'ali.khan@company.com',       '03001234567'),
    (1002, 'Ahmed Raza',     1, 'HR Officer',           60000.00, '2024-02-20', 'ahmed.raza@company.com',     '03007654321'),
    (1003, 'Usman Tariq',    3, 'Accountant',           70000.00, '2024-03-10', 'usman.tariq@company.com',    '03111234567'),
    (1004, 'Sara Malik',     4, 'Marketing Manager',    95000.00, '2024-01-05', 'sara.malik@company.com',     '03219876543'),
    (1005, 'Fatima Noor',    2, 'Junior Developer',     55000.00, '2024-04-01', 'fatima.noor@company.com',    '03331122334'),
    (1006, 'Hassan Siddiqui',5, 'Operations Lead',      80000.00, '2023-11-12', 'hassan.siddiqui@company.com','03451230987'),
    (1007, 'Zara Ahmed',     1, 'Recruitment Specialist',62000.00,'2024-05-18', 'zara.ahmed@company.com',     '03561234567'),
    (1008, 'Bilal Aslam',    3, 'Finance Analyst',      72000.00, '2024-02-28', 'bilal.aslam@company.com',    '03671234567');