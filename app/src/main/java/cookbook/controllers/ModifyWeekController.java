package cookbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import cookbook.model.Recipe;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cookbook.DatabaseConnection;

/*
 * Controller class for the add to weeklylist pop up window from the RecipeView.
 */
public class ModifyWeekController {
    private Recipe recipe;

    @FXML
    private ListView<String> weekList;
    @FXML
    private Button goBackButton;
    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    public void initialize() {
        loadWeeks(); // Load weeks from the database on initialization
        populateDays(); // Populate days of the week
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    private void loadWeeks() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT week_id, start_date, end_date FROM Weeks ORDER BY start_date")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String week = rs.getInt("week_id") + ": " + rs.getDate("start_date") + " - " + rs.getDate("end_date");
                weekList.getItems().add(week);
            }
        } catch (SQLException e) {
            showAlert("Error Loading Weeks", "Failed to load weeks from the database.");
        }
    }

    private void populateDays() {
        dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    }

    private int dayOfWeekToInt(String dayOfWeek) {
      switch (dayOfWeek) {
          case "Monday": return 1;
          case "Tuesday": return 2;
          case "Wednesday": return 3;
          case "Thursday": return 4;
          case "Friday": return 5;
          case "Saturday": return 6;
          case "Sunday": return 7;
          default: return -1; // Invalid day
      }
  }  

    @FXML
    private void handleAddRecipeToWeek(ActionEvent event) {
        String selectedWeek = weekList.getSelectionModel().getSelectedItem();
        String selectedDay = dayComboBox.getSelectionModel().getSelectedItem();
        if (selectedWeek != null && !selectedWeek.isEmpty() && selectedDay != null) {
            int weekId = Integer.parseInt(selectedWeek.split(":")[0]);
            addRecipeToWeek(weekId, this.recipe.getId(), selectedDay);
        } else {
            showAlert("Selection Missing", "Please select a week and a day to add the recipe.");
        }
    }

    private void addRecipeToWeek(int weekId, int recipeId, String dayOfWeek) {
      int dayOfWeekInt = dayOfWeekToInt(dayOfWeek); // Convert to integer
      if (dayOfWeekInt == -1) {
          showAlert("Invalid Day", "The day of the week is not recognized.");
          return;
      }
  
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement("INSERT INTO WeekRecipes (week_id, recipe_id, day_of_week) VALUES (?, ?, ?)")) {
          stmt.setInt(1, weekId);
          stmt.setInt(2, recipeId);
          stmt.setInt(3, dayOfWeekInt);
          stmt.executeUpdate();
          showAlert("Success", "Recipe added to " + dayOfWeek + " successfully.");
      } catch (SQLException e) {
          showAlert("Database Error", "Failed to add the recipe to the selected day. Error: " + e.getMessage());
      }
  }  
  

    // Navigates back to the main page
  @FXML
  private void goBack() throws IOException {
    URL mainPageUrl = getClass().getResource("/mainpage.fxml");
    Parent mainPageRoot = FXMLLoader.load(mainPageUrl);
    Stage stage = (Stage) goBackButton.getScene().getWindow();
    Scene scene = new Scene(mainPageRoot);
    stage.setScene(scene);
  }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
