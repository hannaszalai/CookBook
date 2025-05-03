package cookbook.controllers;

import cookbook.DatabaseConnection;
import cookbook.model.RecipeIngredient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Controller class for the shopping list page.
 */
public class ShoppingListController {
    @FXML
    private Button backButton, removeIngredientButton;
    @FXML
    private ListView<String> weeksList;
    @FXML
    private TableView<RecipeIngredient> ingredientsTable;
    @FXML
    private TableColumn<RecipeIngredient, String> ingredientColumn;
    @FXML
    private TableColumn<RecipeIngredient, String> quantityColumn;
    @FXML
    private TableColumn<RecipeIngredient, String> unitColumn;

    // Initialize the shopping list page
    public void initialize() {
        try {
            // Load weeks
            loadWeeks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ingredientColumn.setCellValueFactory(new PropertyValueFactory<RecipeIngredient, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<RecipeIngredient, String>("quantity"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<RecipeIngredient, String>("unit"));
    
        weeksList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Clear the ingredients table
                ingredientsTable.getItems().clear();
                // Get the dishes for the selected week
                List<String> dishes = getDishesForWeek(newValue);
                // Load the ingredients for each dish
                for (String dish : dishes) {
                    loadIngredientsForDish(dish);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        removeIngredientButton.setOnAction(this::handleRemoveIngredientButtonAction);
    }

    // Load weeks into weeksList
    private void loadWeeks() throws SQLException {
        List<String> weeks = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT start_date, end_date FROM Weeks";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String startDate = resultSet.getString("start_date");
                        String endDate = resultSet.getString("end_date");
                        weeks.add(startDate + " - " + endDate);
                    }
                }
            }
        }
        weeksList.getItems().addAll(weeks);
    }

  // Load ingredients into ingredientsList for the selected dish
  private void loadIngredientsForDish(String dish) throws SQLException {
    // Clear the ingredientsTable before loading new ingredients

    try (Connection connection = DatabaseConnection.getConnection()) {
        // Prepare SQL statement to fetch ingredients for the selected dish
        String sql = "SELECT i.name, sl.quantity, sl.unit FROM Ingredients i " +
                     "INNER JOIN RecipeIngredients ri ON i.ingredient_id = ri.ingredient_id " +
                     "INNER JOIN Recipes r ON ri.recipe_id = r.recipe_id " +
                     "INNER JOIN ShoppingList sl ON i.ingredient_id = sl.ingredient_id " +
                     "WHERE r.name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dish);
            try (ResultSet resultSet = statement.executeQuery()) {
                // Iterate through the result set and add ingredients to ingredientsTable
                while (resultSet.next()) {
                    String ingredient = resultSet.getString("name");
                    String quantity = resultSet.getString("quantity");
                    String unit = resultSet.getString("unit");

                    // Create a new Ingredient object
                    RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, quantity, unit);

                    // Add the Ingredient object to the ingredientsTable
                    ingredientsTable.getItems().add(recipeIngredient);
                }
            }
        }
    }
  }

  // Get dishes for the selected week
  private List<String> getDishesForWeek(String week) throws SQLException {
      List<String> dishes = new ArrayList<>();
      String[] dates = week.split(" - ");
      String startDate = dates[0];
      String endDate = dates[1];
  
      try (Connection connection = DatabaseConnection.getConnection()) {
          String sql = "SELECT r.name FROM Recipes r " +
                        "INNER JOIN WeekRecipes wr ON r.recipe_id = wr.recipe_id " +
                        "INNER JOIN Weeks w ON wr.week_id = w.week_id " +
                        "WHERE w.start_date = ? AND w.end_date = ?";
          try (PreparedStatement statement = connection.prepareStatement(sql)) {
              statement.setString(1, startDate);
              statement.setString(2, endDate);
              try (ResultSet resultSet = statement.executeQuery()) {
                  while (resultSet.next()) {
                      dishes.add(resultSet.getString("name"));
                  }
              }
          }
      }
      return dishes;
  }

  // Handle the "Remove Ingredient" button action
  @FXML
  private void handleRemoveIngredientButtonAction(ActionEvent event) {
      // Get the selected ingredient
      RecipeIngredient selectedIngredient = ingredientsTable.getSelectionModel().getSelectedItem();

      // Check if an ingredient is selected
      if (selectedIngredient != null) {
          // Remove the selected ingredient from the table
          ingredientsTable.getItems().remove(selectedIngredient);
      }
  }

  // Handle the "Back" button action
  @FXML
  private void handleBackButtonAction(ActionEvent event) {
    try {
        // Load the main page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Scene mainPageScene = new Scene(loader.load());

        // Get the current stage
        Stage stage = (Stage) backButton.getScene().getWindow();

        // Change the scene to the main page
        stage.setScene(mainPageScene);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
