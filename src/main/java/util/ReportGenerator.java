package util;

import database.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportGenerator {

    public DefaultTableModel getDepartmentSummary() {

        DefaultTableModel model =
                new DefaultTableModel();

        model.addColumn("Department");
        model.addColumn("Employee Count");

        String sql =
                "SELECT d.name, COUNT(e.id) AS total " +
                "FROM departments d " +
                "LEFT JOIN employees e " +
                "ON d.id = e.department_id " +
                "GROUP BY d.name";

        try (
                Connection con =
                        DatabaseConnection
                                .getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("total")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }

    public DefaultTableModel getSalaryRangeReport(
            double minSalary,
            double maxSalary) {

        DefaultTableModel model =
                new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Designation");
        model.addColumn("Salary");

        String sql =
                "SELECT * FROM employees " +
                "WHERE salary BETWEEN ? AND ?";

        try (
                Connection con =
                        DatabaseConnection
                                .getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setDouble(1, minSalary);
            ps.setDouble(2, maxSalary);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("designation"),
                        rs.getDouble("salary")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }
}