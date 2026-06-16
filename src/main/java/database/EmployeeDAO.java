package database;

import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO extends BaseDAO<Employee, Integer> {

    @Override
    public List<Employee> getAll() {

        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees ORDER BY id";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                employees.add(
                        new Employee(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getInt("department_id"),
                                rs.getString("designation"),
                                rs.getDouble("salary"),
                                rs.getDate("hire_date"),
                                rs.getString("email"),
                                rs.getString("phone")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public Employee getById(Integer id) {

        String sql =
                "SELECT * FROM employees WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("department_id"),
                        rs.getString("designation"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean save(Employee employee) {

        String sql =
                "INSERT INTO employees " +
                "(id,name,department_id,designation,salary,hire_date,email,phone) " +
                "VALUES(?,?,?,?,?,?,?,?)";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, employee.getId());
            ps.setString(2, employee.getName());
            ps.setInt(3, employee.getDepartmentId());
            ps.setString(4, employee.getDesignation());
            ps.setDouble(5, employee.getSalary());
            ps.setDate(6, employee.getHireDate());
            ps.setString(7, employee.getEmail());
            ps.setString(8, employee.getPhone());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean update(Employee employee) {

        String sql =
                "UPDATE employees SET " +
                "name=?, " +
                "department_id=?, " +
                "designation=?, " +
                "salary=?, " +
                "hire_date=?, " +
                "email=?, " +
                "phone=? " +
                "WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, employee.getName());
            ps.setInt(2, employee.getDepartmentId());
            ps.setString(3, employee.getDesignation());
            ps.setDouble(4, employee.getSalary());
            ps.setDate(5, employee.getHireDate());
            ps.setString(6, employee.getEmail());
            ps.setString(7, employee.getPhone());
            ps.setInt(8, employee.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {

        String sql =
                "DELETE FROM employees WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean addEmployee(Employee employee) {
        return save(employee);
    }

    public boolean updateEmployee(Employee employee) {
        return update(employee);
    }

    public boolean deleteEmployee(int id) {
        return delete(id);
    }

    public List<Employee> getAllEmployees() {
        return getAll();
    }

    public boolean employeeExists(int id) {

        String sql =
                "SELECT id FROM employees WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            return rs.next();

        } catch (Exception e) {

            return false;
        }
    }

    public Employee searchEmployee(int id) {

        return getById(id);
    }

    public List<Employee> searchEmployee(String keyword) {

        List<Employee> employees =
                new ArrayList<>();

        String sql =
                "SELECT * FROM employees " +
                "WHERE name LIKE ? " +
                "OR designation LIKE ?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            String search =
                    "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                employees.add(
                        new Employee(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getInt("department_id"),
                                rs.getString("designation"),
                                rs.getDouble("salary"),
                                rs.getDate("hire_date"),
                                rs.getString("email"),
                                rs.getString("phone")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> searchByDepartment(
            int departmentId) {

        List<Employee> employees =
                new ArrayList<>();

        String sql =
                "SELECT * FROM employees " +
                "WHERE department_id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, departmentId);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                employees.add(
                        new Employee(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getInt("department_id"),
                                rs.getString("designation"),
                                rs.getDouble("salary"),
                                rs.getDate("hire_date"),
                                rs.getString("email"),
                                rs.getString("phone")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public int getEmployeeCount() {

        String sql =
                "SELECT COUNT(*) total FROM employees";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getLatestEmployeeName() {

        String sql =
                "SELECT name FROM employees " +
                "ORDER BY id DESC LIMIT 1";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "N/A";
    }
}