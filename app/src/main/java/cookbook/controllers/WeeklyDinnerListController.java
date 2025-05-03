package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import cookbook.DatabaseConnection;
import java.io.IOException;
import java.net.URL;

/*
 * Controller class for the Weekly Dinner List.
 */
public class WeeklyDinnerListController {
    @FXML
    private ListView<String> weekList;
    @FXML
    private ListView<String> lstMonday;
    @FXML
    private ListView<String> lstTuesday;
    @FXML
    private ListView<String> lstWednesday;
    @FXML
    private ListView<String> lstThursday;
    @FXML
    private ListView<String> lstFriday;
    @FXML
    private ListView<String> lstSaturday;
    @FXML
    private ListView<String> lstSunday;
    @FXML
    private Button goBackButton, ViewRecipeButton, AddWeeks;

    public void initialize() {
        loadWeeks();
        weekList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadRecipesForWeek(newSelection);
            }
        });
        AddWeeks.setOnAction(e -> addWeeks());
        setRecipeDoubleClickHandler(lstMonday);
        setRecipeDoubleClickHandler(lstTuesday);
        setRecipeDoubleClickHandler(lstWednesday);
        setRecipeDoubleClickHandler(lstThursday);
        setRecipeDoubleClickHandler(lstFriday);
        setRecipeDoubleClickHandler(lstSaturday);
        setRecipeDoubleClickHandler(lstSunday);
    }

    private void setRecipeDoubleClickHandler(ListView<String> listView) {
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedRecipe = listView.getSelectionModel().getSelectedItem();
                if (selectedRecipe != null) {
                    openRecipeView(selectedRecipe);
                }
            }
        });
    }

    private void openRecipeView(String recipeName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecipeView.fxml"));
            Parent root = loader.load();

            // Pass the recipe name to the RecipeDisplayController
            RecipeDisplayController controller = loader.getController();
            controller.setRecipeName(recipeName);

            // Get the current stage from any node in the current scene
            Stage stage = (Stage) weekList.getScene().getWindow();

            // Create a new scene with the recipe view root
            Scene scene = new Scene(root);

            // Set the new scene to the stage
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWeeks() {
        try {
            // Clear the ListView
            weekList.getItems().clear();

            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Weeks");

            while (resultSet.next()) {
                String week = resultSet.getString("start_date") + " - " + resultSet.getString("end_date");
                weekList.getItems().add(week);
            }
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

    private void loadRecipesForWeek(String week) {
        try {
            // Split the week string to get the start and end dates
            String[] dates = week.split(" - ");
            String startDate = dates[0];
            String endDate = dates[1];

            // Clear the list views
            lstMonday.getItems().clear();
            lstTuesday.getItems().clear();
            lstWednesday.getItems().clear();
            lstThursday.getItems().clear();
            lstFriday.getItems().clear();
            lstSaturday.getItems().clear();
            lstSunday.getItems().clear();

            // Get the week id from the Weeks table
            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT week_id FROM Weeks WHERE start_date = '" + startDate + "' AND end_date = '" + endDate + "'");

            if (resultSet.next()) {
                int weekId = resultSet.getInt("week_id");

                // Load the recipes for each day of the week
                loadRecipesForDay(lstMonday, "1", weekId);
                loadRecipesForDay(lstTuesday, "2", weekId);
                loadRecipesForDay(lstWednesday, "3", weekId);
                loadRecipesForDay(lstThursday, "4", weekId);
                loadRecipesForDay(lstFriday, "5", weekId);
                loadRecipesForDay(lstSaturday, "6", weekId);
                loadRecipesForDay(lstSunday, "7", weekId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load the recipes for a specific day of the week
    private void loadRecipesForDay(ListView<String> listView, String dayOfWeek, int weekId) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Recipes.name FROM Recipes JOIN WeekRecipes ON Recipes.recipe_id = WeekRecipes.recipe_id WHERE WeekRecipes.day_of_week = '" + dayOfWeek + "' AND WeekRecipes.week_id = " + weekId);

            while (resultSet.next()) {
                String recipe = resultSet.getString("name");
                listView.getItems().add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addWeeks() {
        try {
            // Load the AddWeekPopup FXML file
            URL addWeekPopupUrl = getClass().getResource("/AddWeekPopup.fxml");
            FXMLLoader loader = new FXMLLoader(addWeekPopupUrl);

            Parent addWeekPopupRoot = loader.load();

            // Get the controller of the AddWeekPopup
            AddWeeksPopupController controller = loader.getController();

            // Pass a reference to this controller
            controller.setWeeklyDinnerListController(this);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            Scene scene = new Scene(addWeekPopupRoot);
            popupStage.setScene(scene);

            // Set the title
            popupStage.setTitle("Add Week");

            // Set the icon
            popupStage.getIcons().add(new Image("/images/logo.png"));

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
