package cookbook.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import cookbook.DatabaseConnection;
import cookbook.model.User1;

/*
 * Data Access Object (DAO) class for Admin operations.
 */
public class AdminDao {
  @FXML
  private TableView<User1> userlst;
  @FXML
  private TextField txtDisplayName, txtUserName, txtPassword;
  @FXML
  private CheckBox adminCheckbox;


  // Method to retrieve users from the database
  public static List<User1> getUsers() {
    List<User1> allUsers = new ArrayList<>();
    String query = "SELECT user_id, display_name, username, password_hash, admin_flag FROM users;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement sqlStatement = conn.prepareStatement(query);
        ResultSet result = sqlStatement.executeQuery()) {
      while (result.next()) {
        allUsers.add(new User1(
            Integer.toString(result.getInt("user_id")),
            result.getString("display_name"),
            result.getString("username"),
            result.getString("password_hash"),
            result.getBoolean("admin_flag")));
      }
    } catch (Exception e) {
      System.err.println("Database operation failed: " + e.getMessage());
    }
    return allUsers;
  }

  // Method to add a new user to the database
  public void addUser(String displayName, String username, String password, boolean isAdmin) {
    String query = "INSERT INTO users (username, password_hash, display_name, admin_flag) VALUES (?, ?, ?, ?);";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement sqlStatement = conn.prepareStatement(query)) {
      sqlStatement.setString(1, username);
      sqlStatement.setString(2, password);
      sqlStatement.setString(3, displayName);
      sqlStatement.setBoolean(4, isAdmin);
      sqlStatement.executeUpdate();
    } catch (Exception e) {
      System.err.println("Error adding user: " + e.getMessage());
    }
  }

  // Method to update an existing user in the database
  public void editUser(String displayName, String username, String password, int userId, boolean isAdmin) {
    String updateQuery = "UPDATE users SET display_name=?, username=?, password_hash=?, admin_flag=? WHERE user_id=?;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement sqlStatement = conn.prepareStatement(updateQuery)) {
      sqlStatement.setString(1, displayName);
      sqlStatement.setString(2, username);
      sqlStatement.setString(3, password);
      sqlStatement.setBoolean(4, isAdmin);
      sqlStatement.setInt(5, userId);
      sqlStatement.executeUpdate();
    } catch (Exception e) {
      System.err.println("Error updating user: " + e.getMessage());
    }
  }

  // Method to delete a user from the database
  public void deleteUser(String displayName, String username, String password) {
    String deleteQuery = "DELETE FROM users WHERE display_name=? AND username=? AND password_hash=?;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement sqlStatement = conn.prepareStatement(deleteQuery)) {
      sqlStatement.setString(1, displayName);
      sqlStatement.setString(2, username);
      sqlStatement.setString(3, password);
      sqlStatement.executeUpdate();
    } catch (Exception e) {
      System.err.println("Error deleting user: " + e.getMessage());
    }
  }
}
