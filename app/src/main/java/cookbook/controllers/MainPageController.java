package cookbook.controllers;

import java.util.ArrayList;
import java.util.List;
import cookbook.UserSession;
import cookbook.DAOs.RecipeDao;
import cookbook.model.Recipe;
import cookbook.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.HashMap;
import java.util.Map;

/*
 * Controller class for the Main Page.
 */
public class MainPageController {
  private User user;
  private RecipeDao recipeDao;
  private List<Recipe> recipes;

  @FXML
  private VBox showAllRecipes;
  @FXML
  private ListView<String> recipeList;
  @FXML
  private TextField searchField;
  @FXML
  private Button button_search, searchButtonforRecipes, button_addrecipe, button_logout, weeklyDinnerButton, Helpcenter;
  @FXML
  private Button button_messages, button_shoppinglist, button_favourites;
  @FXML
  private ChoiceBox<String> selectSearch;
  @FXML
  private Label welcomeText;

  // Constructor
  public MainPageController(RecipeDao recipeDao) {
    this.recipeDao = recipeDao;
    this.recipes = new ArrayList<>();
  }

  // No-argument constructor
  public MainPageController() {
  }

  public void setUser(User user) {
    if (user != null) {
        UserSession.getInstance(user);
        this.user = UserSession.getInstance(user).getUser();
    } else {
        System.out.println("User is null");
    }
    System.out.println("User set in mainpage: " + this.user);
}

    // Transition to CreateRecipeController
    public void transitionToCreateRecipeController(User user, Recipe recipe) {
        CreateRecipeController createRecipeController = new CreateRecipeController();
        createRecipeController.setUser(user); // Set the user
        createRecipeController.setRecipe(recipe);
    }

    // Transition to RecipeDisplayController
    public void transitionToRecipeDisplayController(User user, Recipe recipe) {
        RecipeDisplayController recipeDisplayController = new RecipeDisplayController();
        recipeDisplayController.setUser(user); // Set the user
        recipeDisplayController.setRecipe(recipe);
    }

    // Transition to CommentDisplayController
    public void transitionToCommentDisplayController(User user, Recipe recipe) {
        CommentDisplayController commentDisplayController = new CommentDisplayController();
        commentDisplayController.setUser(user); // Set the user
        commentDisplayController.setRecipe(recipe);
    }

  // Initialize the main page with different features
  public void initialize() {
    Helpcenter.setOnAction(e -> showHelpcenter(e));
    button_addrecipe.setOnAction(e -> showCreateRecipe(e));
    selectSearch.getItems().addAll("Name", "Tag", "Ingredients");
    weeklyDinnerButton.setOnAction(e -> showWeeklyDinnerList(e));
    button_shoppinglist.setOnAction(this::loadShoppingList);
    button_favourites.setOnAction(e -> handleButtonFavoritesAction(e));

    // Populate the recipeList with all recipes
    RecipeDao recipeDao = new RecipeDao();
    Recipe[] allRecipes = recipeDao.showAllRecipes();
    for (Recipe recipe : allRecipes) {
        recipeList.getItems().add(recipe.getName());
    }

    // Create a map to store the short descriptions of the recipes (for the hoverin over)
    Map<String, String> recipeDescriptions = new HashMap<>();
    for (Recipe recipe : allRecipes) {
        recipeDescriptions.put(recipe.getName(), recipe.getSummary());
    }

    // Simple/Double click search function
    recipeList.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2) {
            // Load RecipeView.fxml
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RecipeView.fxml"));
                Parent root = (Parent) fxmlLoader.load();

                // Get the selected recipe
                String selectedRecipeName = recipeList.getSelectionModel().getSelectedItem();
                RecipeDao recipeDaoInsideLambda = new RecipeDao();
                Recipe[] recipes = recipeDaoInsideLambda.searchRecipesByName(selectedRecipeName);
                Recipe selectedRecipe = recipes[0];

                // Get the button's scene
                Scene buttonScene = recipeList.getScene();

                // Replace the root of the current scene with the loaded FXML content
                buttonScene.setRoot(root);

                // Get the controller and pass the selected recipe to it
                RecipeDisplayController controller = fxmlLoader.getController();
                controller.initialize(selectedRecipe);
                controller.setUser(user); // Pass the user object to the RecipeDisplayController

            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if (event.getClickCount() == 1) {
            // Single click: Copy the selected recipe to the search field
            String selectedRecipe = recipeList.getSelectionModel().getSelectedItem();
            searchField.setText(selectedRecipe);
        }
    });

    // Set a custom cell factory for the ListView (for the hoverin over)
    recipeList.setCellFactory(lv -> new ListCell<String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            if (item != null) {
                // Set a tooltip with the short description of the recipe
                Tooltip tooltip = new Tooltip(recipeDescriptions.get(item));
                setTooltip(tooltip);
            }
        }
    });

    // Change the prompt text based on the selected search method
    selectSearch.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
      switch (newValue) {
          case "Name":
              searchField.setPromptText("Enter recipe name...");
              break;
          case "Tag":
              searchField.setPromptText("Enter tag1, tag2...");
              break;
          case "Ingredients":
              searchField.setPromptText("Enter ingredient1, ingredient2...");
              break;
      }
    });

    // Search button action
    button_search.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String searchText = searchField.getText();
        String searchType = selectSearch.getValue();
        Recipe[] recipes;

        if (searchType == null) {
          searchType = "Name";
        }
        switch (searchType) {
            case "Name":
                recipes = recipeDao.searchRecipesByName(searchText);
                break;
            case "Tag":
                recipes = recipeDao.searchRecipesByTagName(searchText);
                break;
            case "Ingredients":
                recipes = recipeDao.searchRecipesByIngredientName(searchText);
                break;
            default:
                recipes = new Recipe[0];
        }

        // Clear the recipeList and add the search results
        recipeList.getItems().clear();
        if (recipes.length == 0) {
            // If no recipes found, show an alert
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Search Results");
            alert.setHeaderText(null);
            alert.setContentText("No results found for \"" + searchText + "\"");

            // Set the icon
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("/images/logo.png"));

            alert.showAndWait();
        } else {
            // If recipes found, add them to the ListView
            for (Recipe recipe : recipes) {
                recipeList.getItems().add(recipe.getName());
            }
        }
      }
    });
    // Enter key action for search field
    searchField.setOnKeyPressed(event -> {
        switch (event.getCode()) {
            case ENTER:
                button_search.fire();
                break;
            default:
                break;
        }
    });
  }

  // Goes to the Createrecipe page
  public void showCreateRecipe(ActionEvent event) {
    try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CreateRecipe.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = (Stage) button_addrecipe.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("The Cookbook - Create Recipe");
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  // Goes to the WeeklyDinnerList page
  public void showWeeklyDinnerList(ActionEvent event) {
    try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WeeklyDinnerList.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = (Stage) weeklyDinnerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("The Cookbook - Weekly Dinner List");
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
    public void showHelpcenter(ActionEvent event) {
    try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/HelpCenter.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = (Stage) Helpcenter.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
 
    // Goes to the ViewMessages page
    public void showMessages(ActionEvent event) {
    try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/viewMessages.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = (Stage) button_messages.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("View Messages");

        // Get the controller and pass the user to it
        ViewMessagesController controller = fxmlLoader.getController();
        controller.setCurrentUser(this.user);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    // Goes to the ShoppingList page
    public void loadShoppingList(ActionEvent event) {
        try {
            Parent shoppingListParent = FXMLLoader.load(getClass().getResource("/ShoppingList.fxml"));
            Scene shoppingListScene = new Scene(shoppingListParent);
            
            // This line gets the Stage information
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            
            window.setScene(shoppingListScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  // Goes to the Favorites page
  @FXML
  private void handleButtonFavoritesAction(ActionEvent event) {
    try {
        // Load the FavoriteRecipes.fxml file
        Parent favoriteRecipesParent = FXMLLoader.load(getClass().getResource("/favoritesPage.fxml"));

        // Create a new scene with the loaded FXML
        Scene favoriteRecipesScene = new Scene(favoriteRecipesParent);

        // Get the current stage
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the new scene on the stage
        window.setScene(favoriteRecipesScene);
        window.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

    // Handles the log out button - Goes to the login page
    @FXML
    private void handleLogoutButtonAction(ActionEvent event) throws IOException {
        // Reset the UserSession instance
        UserSession.resetInstance();

        Stage stage = (Stage) button_logout.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/loginpage.fxml")); 
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}