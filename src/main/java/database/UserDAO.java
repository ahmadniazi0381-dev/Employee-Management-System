package database;

import model.User;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class UserDAO extends BaseDAO<User, String> {

    @Override
    public List<User> getAll() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public User getById(String username) {

        String sql =
                "SELECT * FROM users WHERE username=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean save(User user) {

        String sql =
                "INSERT INTO users(username,password) VALUES(?,?)";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean update(User user) {

        String sql =
                "UPDATE users SET password=? WHERE username=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, user.getPassword());
            ps.setString(2, user.getUsername());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean delete(String username) {

        String sql =
                "DELETE FROM users WHERE username=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean authenticate(
            String username,
            String password) {

        String sql =
                "SELECT * FROM users " +
                "WHERE username=? AND password=?";

        try (
                Connection con =
                        DatabaseConnection.getInstance()
                                .getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database error during authentication", e);
        }
    }
}