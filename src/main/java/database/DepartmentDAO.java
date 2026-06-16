package database;

import model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO
        extends BaseDAO<Department, Integer> {

    @Override
    public List<Department> getAll() {

        List<Department> departments =
                new ArrayList<>();

        String sql =
                "SELECT * FROM departments ORDER BY name";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                departments.add(
                        new Department(
                                rs.getInt("id"),
                                rs.getString("name")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return departments;
    }

    @Override
    public Department getById(Integer id) {

        String sql =
                "SELECT * FROM departments WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return new Department(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean save(Department department) {

        String sql =
                "INSERT INTO departments(name) VALUES(?)";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1,
                    department.getName());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean update(Department department) {

        String sql =
                "UPDATE departments SET name=? WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1,
                    department.getName());

            ps.setInt(2,
                    department.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {

        String check =
                "SELECT COUNT(*) total " +
                "FROM employees " +
                "WHERE department_id=?";

        String delete =
                "DELETE FROM departments WHERE id=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection()
        ) {

            PreparedStatement checkPs =
                    con.prepareStatement(check);

            checkPs.setInt(1, id);

            ResultSet rs =
                    checkPs.executeQuery();

            if (rs.next()) {

                if (rs.getInt("total") > 0) {
                    return false;
                }
            }

            PreparedStatement deletePs =
                    con.prepareStatement(delete);

            deletePs.setInt(1, id);

            return deletePs.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public List<Department> getAllDepartments() {
        return getAll();
    }
}