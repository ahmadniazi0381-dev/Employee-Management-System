package model;

import java.sql.Date;

public class Employee {

    private int id;
    private String name;
    private int departmentId;
    private String designation;
    private double salary;
    private Date hireDate;
    private String email;
    private String phone;

    public Employee() {
    }

    public Employee(int id, String name, int departmentId,
                    String designation, double salary,
                    Date hireDate, String email, String phone) {

        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
        this.designation = designation;
        this.salary = salary;
        this.hireDate = hireDate;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}