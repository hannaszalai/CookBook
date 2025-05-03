package cookbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import cookbook.DatabaseConnection;
import cookbook.UserSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/*
 * Controller class for the Favorites Page.
 */
public class FavoritesPageController {

    @FXML
    private Button backButton;
    @FXML
    private ListView<String> favoritesList;

    // Initialize the favorites page
    public void initialize() throws Exception {
        // Load favorite recipes and populate the favoritesList
        favoritesList.getItems().clear();
        favoritesList.getItems().addAll(getFavoriteRecipesByUserId());
    }

    // Handle the "Back to Main Page" button action
    @FXML
    private void backToMainPage() {
        // Close the favorites page and go back to the main page
        Stage stage = (Stage) favoritesList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleBackButtonAction() {
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

    @FXML
    public void removeRecipe(ActionEvent actionEvent) {
        String selectedRecipe = favoritesList.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            // Remove the selected recipe from the favoritesList
            favoritesList.getItems().remove(selectedRecipe);
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "DELETE FROM FavoriteRecipes WHERE user_id = ? AND recipe_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, UserSession.getInstance(null).getUser().getUserId());
                statement.setInt(2, getRecipeIdFromRecipeName(selectedRecipe));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // If no item is selected, show a message or handle it according to your requirement
            System.out.println("No recipe selected to remove.");
        }
    }

    private List<String> getFavoriteRecipesByUserId() throws Exception {
        List<String> recipeList = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnection();
        String query = "select r.name from FavoriteRecipes f join " +
        "Recipes r on f.recipe_id = r.recipe_id where f.user_id = ? order by r.name ";
    PreparedStatement stm = connection.prepareStatement(query);
    stm.setInt(1, UserSession.getInstance(null).getUser().getUserId());
    ResultSet rs = stm.executeQuery();
    
        while(rs.next()) {
            String recipe = rs.getString(1);
            recipeList.add(recipe);
        }
        return recipeList;
    }

    // Get the recipe ID from the recipe name
    private int getRecipeIdFromRecipeName(String recipeName) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        String query = "select recipe_id from Recipes where name = '"+recipeName+"'";
        PreparedStatement stm = connection.prepareStatement(query);
        ResultSet rs = stm.executeQuery(query);
        int recipeId = 0;

        while(rs.next()) {
            recipeId = rs.getInt(1);
        }
        return recipeId;
    }
}
