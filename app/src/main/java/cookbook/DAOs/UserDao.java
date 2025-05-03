package cookbook.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import cookbook.model.User;

/*
 * Data Access Object (DAO) class for Users.
 */
public class UserDao {
    private Connection connection;
    private String username;
    private String displayName;
    private String password;
    private boolean isAdmin;
    private String userId;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public String getUsernameById(long userId) throws SQLException {
        String username = null;
        String query = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        }
        return username;
    }


    public List<User> getAllOtherUsers(int currentUserId) {
        List<User> allUsers = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE user_id != ?";
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, currentUserId);
            ResultSet rs = statement.executeQuery();
    
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("display_name"), rs.getString("password_hash"), rs.getBoolean("admin_flag"), rs.getInt("user_id"));
                allUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return allUsers;
    }


    public boolean userIdExists(int userId) {
        String query = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getString("username"),
                    rs.getString("display_name"),
                    rs.getString("password_hash"),
                    rs.getBoolean("admin_flag"),
                    rs.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    
    public int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the user is not found
    }
}
