package cookbook.controllers;

import cookbook.DatabaseConnection;
import cookbook.UserSession;
import cookbook.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cookbook.model.Recipe;

/*
 * Controller class for creating a new recipe.
 */
public class CreateRecipeController {
    private User user;
    private Recipe recipe;

    @FXML
    private TextArea recipeName, shortDescription, detailedDescription;
    @FXML
    private Button createRecipeButton, goBackButton;
    @FXML
    private ComboBox<String> chooseIngredient;
    @FXML
    private TextField amount, unit;
    @FXML
    private ListView<String> listOfIngredients;

    public void setUser(User user) {
    if (user != null) {
        this.user = user;
    } else {
        this.user = UserSession.getInstance(null).getUser();
    }
    System.out.println("User set in createrecipe: " + this.user);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @FXML
    public void initialize() {
        populateIngredientsComboBox();
    }

    // Fetch ingredients from the database and populate the ComboBox
    private void populateIngredientsComboBox() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT name FROM Ingredients";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                chooseIngredient.getItems().add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    // Method to display pop-up notification
    private void displayNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void createRecipe() {
        // Get the text from the TextAreas
        String recipeNameText = recipeName.getText();
        String shortDescriptionText = shortDescription.getText();
        String detailedDescriptionText = detailedDescription.getText();

        displayNotification("Recipe added!");

        // Connect to the database and insert the new recipe
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Recipes (name, short_description, detailed_description, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, recipeNameText);
            statement.setString(2, shortDescriptionText);
            statement.setString(3, detailedDescriptionText);
            statement.setInt(4, 1); // Replace 1 with the actual UserID
            statement.executeUpdate();

            // After the recipe is created and the pop-up window is shown, reset the fields
            resetFields();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            // Load the main page's FXML file
            URL mainPageUrl = getClass().getResource("/mainpage.fxml");
            Parent mainPageRoot = FXMLLoader.load(mainPageUrl);

            // Set the current scene to the main page
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            Scene scene = new Scene(mainPageRoot);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addIngredient() {
        // Get selected ingredient, amount, and unit
        String selectedIngredient = chooseIngredient.getSelectionModel().getSelectedItem();
        String ingredientAmount = amount.getText();
        String ingredientUnit = unit.getText();

        // Add ingredient to the ListView
        listOfIngredients.getItems().add(selectedIngredient + " - " + ingredientAmount + " " + ingredientUnit);

        // Connect to the database and insert the new ingredient into the RecipeIngredients table
        try {
            Connection connection = DatabaseConnection.getConnection();
            String selectIngredientID = "SELECT ingredient_id FROM Ingredients WHERE name = ?";
            PreparedStatement ingredientStatement = connection.prepareStatement(selectIngredientID);
            ingredientStatement.setString(1, selectedIngredient);
            ResultSet resultSet = ingredientStatement.executeQuery();
            int ingredientID = 0;
            if (resultSet.next()) {
                ingredientID = resultSet.getInt("ingredient_id");
            }

            String recipeIDQuery = "SELECT MAX(recipe_id) AS max_recipe_id FROM Recipes";
            PreparedStatement recipeIDStatement = connection.prepareStatement(recipeIDQuery);
            ResultSet rs = recipeIDStatement.executeQuery();
            int recipeID = 0;
            if (rs.next()) {
                recipeID = rs.getInt("max_recipe_id");
            }

            String sql = "INSERT INTO RecipeIngredients (recipe_id, ingredient_id, quantity, unit) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, recipeID);
            statement.setInt(2, ingredientID);
            statement.setString(3, ingredientAmount);
            statement.setString(4, ingredientUnit);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear the fields after adding the ingredient
        chooseIngredient.getSelectionModel().clearSelection();
        amount.clear();
        unit.clear();
    }

    // Reset all fields after recipe has been added
    public void resetFields() {
        recipeName.setText("");
        detailedDescription.setText("");
        shortDescription.setText("");
        amount.setText("");
        unit.setText("");
        listOfIngredients.getItems().clear();
    }
}
