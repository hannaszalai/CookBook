package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import cookbook.model.Recipe;
import cookbook.model.RecipeIngredient;
import cookbook.model.Tag;
import cookbook.model.User;
import cookbook.DatabaseConnection;
import cookbook.UserSession;
import cookbook.DAOs.RecipeDao;
import cookbook.DAOs.RecipeIngredientDAO;
import cookbook.DAOs.TagDao;
import cookbook.DAOs.UserDao;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;

/*
 * Controller class for the Recipe Display page.
 */
public class RecipeDisplayController {
  private Recipe recipe;
  private Recipe selectedRecipe;
  private Connection connection;
  private List<RecipeIngredient> ingredList = new ArrayList<>();
  private User user;
  private User selectedUser;
  private User currentUser;
  private TagDao tagDao = new TagDao();
  private TagDao tagDAO;
  private ViewMessagesController viewMessagesController;
  private CommentDisplayController commentDisplayController;

  @FXML
  private Button backButton, decreaseButton, increaseButton, addToWeeklyListButton, favoriteBox, btnAddTag;
  @FXML
  private Label recipeNametext, errorLabel;
  @FXML
  private Text shortDescText;
  @FXML
  private TextField servingText, quantityTextField;
  @FXML
  private TableView<RecipeIngredient> ingredientsTable;
  @FXML
  private TableColumn<RecipeIngredient, String> nameColumn;
  @FXML
  private TableColumn<RecipeIngredient, String> quantityColumn;
  @FXML
  private TableColumn<RecipeIngredient, String> unitColumn;
  @FXML
  private TextArea recipeDescriptionTextArea, longDescText;
  @FXML
  private ListView<String> tagList;
  @FXML
  private ListView<String> ingredientsList;
  @FXML
  private Button btnRemoveTag, btnAddComment, shareButton;

  // Define the ObservableList at the class level
  private ObservableList<String> tagNames = FXCollections.observableArrayList();

  public RecipeDisplayController() {
      this.tagDAO = new TagDao();
  }

  public void setUser(User user) {
      if (user != null) {
          this.user = user;
      } else {
          this.user = UserSession.getInstance(null).getUser();
      }
      System.out.println("User set in recipedisplay: " + this.user);
  }

  // Set recipe method when the user comes from the Messages and clicks on the Sent Recipe
  public void setRecipe(Recipe recipe) {
      RecipeIngredientDAO recipeIngredientDAO = new RecipeIngredientDAO();
      this.tagDAO = new TagDao();

      this.recipe = recipe;

      ingredList.clear();
      ingredList.addAll(recipeIngredientDAO.getIngredientsForRecipe(this.recipe.getId()));
      recipeNametext.setText(this.recipe.getName());
      shortDescText.setText(this.recipe.getSummary());
      longDescText.setText(this.recipe.getDescription());
      longDescText.setEditable(false);

      // Name column
      TableColumn<RecipeIngredient, String> nameColumn = new TableColumn<>("Ingredient");
      nameColumn.setMinWidth(15);
      nameColumn.setCellValueFactory(ri -> new SimpleStringProperty(ri.getValue().getIngredient().getName()));

      // Amount column
      TableColumn<RecipeIngredient, Float> quantityColumn = new TableColumn<>("Amount");
      quantityColumn.setMinWidth(35);
      quantityColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

      // Measurement column
      TableColumn<RecipeIngredient, String> unitColumn = new TableColumn<>("Unit");
      unitColumn.setMinWidth(60);
      unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

      ObservableList<RecipeIngredient> observableList = FXCollections.observableArrayList(ingredList);

      // Fill table with data
      ingredientsTable.getColumns().clear();
      ingredientsTable.getColumns().add(nameColumn);
      ingredientsTable.getColumns().add(quantityColumn);
      ingredientsTable.getColumns().add(unitColumn);
      ingredientsTable.setItems(observableList);

      // Fetch the tags for the recipe from the database
      Tag[] tags = tagDAO.getAllTags(this.recipe.getId());

      // Add the names of the tags to the tagNames ObservableList
      tagNames.clear();
      for (Tag tag : tags) {
          tagNames.add(tag.getName());
      }
      tagList.setItems(tagNames);

      tagList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
          if (newSelection != null) {
              errorLabel.setVisible(false);
          }
      });
  }

  public void setRecipeName(String recipeName) {
      recipeNametext.setText(recipeName);
      // Fetch and display the recipe details based on the recipe name
      displayRecipeDetails(recipeName);
  }

  private void displayRecipeDetails(String recipeName) {
      // Logic to fetch the recipe details from the database or a predefined list
      RecipeDao recipeDao = new RecipeDao();
      Recipe recipe = recipeDao.searchRecipesByName(recipeName)[0];

      // Set the recipe details
      setRecipe(recipe);
  }

  // Transition to the ShareRecipeController
  public void transitionToShareRecipeController(User user, Recipe recipe) {
      ShareRecipeController shareRecipeController = new ShareRecipeController();
      shareRecipeController.setUser(user);
      shareRecipeController.setRecipe(recipe);
  }

  public void setRecipeId(int recipeId) {
      if (this.recipe == null) {
          this.recipe = new Recipe();
      }
      this.recipe.setId(recipeId);
  }

  public void setViewMessagesController(ViewMessagesController viewMessagesController) {
      this.viewMessagesController = viewMessagesController;
  }

  public void setCommentDisplayController(CommentDisplayController commentDisplayController) {
      this.commentDisplayController = commentDisplayController;
      this.commentDisplayController.setUser(this.user);
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public Recipe getDisplayedRecipe() {
      return this.recipe;
  }

  public void setSelectedRecipe(Recipe recipe) {
      this.selectedRecipe = recipe;
  }

  public void setSelectedUser(User user) {
      this.selectedUser = user;
  }


  public void initialize(Recipe recipe) {
      RecipeIngredientDAO recipeIngredientDAO = new RecipeIngredientDAO();
      this.tagDAO = new TagDao();

      this.recipe = recipe;

      ingredList.addAll(recipeIngredientDAO.getIngredientsForRecipe(this.recipe.getId()));
      recipeNametext.setText(this.recipe.getName());
      shortDescText.setText(this.recipe.getSummary());
      longDescText.setText(this.recipe.getDescription());
      longDescText.setEditable(false);
      favoriteBox.setOnAction(e -> addToFavorites());

      // Name column
      TableColumn<RecipeIngredient, String> nameColumn = new TableColumn<>("Ingredient");
      nameColumn.setMinWidth(15);
      nameColumn.setCellValueFactory(ri -> new SimpleStringProperty(ri.getValue().getIngredient().getName()));

      // Amount column
      TableColumn<RecipeIngredient, Float> quantityColumn = new TableColumn<>("Amount");
      quantityColumn.setMinWidth(35);
      quantityColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

      // Measurement column
      TableColumn<RecipeIngredient, String> unitColumn = new TableColumn<>("Unit");
      unitColumn.setMinWidth(60);
      unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

      ObservableList<RecipeIngredient> observableList = FXCollections.observableArrayList(ingredList);

      // Fill table with data
      ingredientsTable.getColumns().clear();
      ingredientsTable.getColumns().add(nameColumn);
      ingredientsTable.getColumns().add(quantityColumn);
      ingredientsTable.getColumns().add(unitColumn);
      ingredientsTable.setItems(observableList);

      // Fetch the tags for the recipe from the database
      Tag[] tags = tagDAO.getAllTags(this.recipe.getId());

      // Add the names of the tags to the tagNames ObservableList
      for (Tag tag : tags) {
          tagNames.add(tag.getName());
      }
      tagList.setItems(tagNames);

      tagList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
          if (newSelection != null) {
              errorLabel.setVisible(false);
          }
      });
  }

  // Method to set user data
  public void setUserData(User user) {
      try {
          UserDao userDao = new UserDao(connection);

          // Fetch username from database using user ID
          userDao.getUsernameById(user.getUserId());
      } catch (SQLException e) {
          // Handle database exception
          e.printStackTrace();
      } catch (NumberFormatException e) {
          // Handle invalid user ID format
          e.printStackTrace();
      }
  }

  @FXML
  public void changeServingButton(ActionEvent event) {
      int servings = Integer.parseInt(this.servingText.getText());
      Iterator<RecipeIngredient> var3;
      RecipeIngredient ingred;
      if (event.getSource().equals(this.increaseButton)) {
          if (servings != 8) {  // Change the maximum servings to 8
              servings += 2;
              this.servingText.setText(Integer.toString(servings));
              var3 = this.ingredList.iterator();

              while (var3.hasNext()) {
                  ingred = var3.next();
                  ingred.doubleAmount(servings - 2);
              }

              this.ingredientsTable.setItems(FXCollections.observableArrayList(this.ingredList));
              this.ingredientsTable.refresh();
          }
      } else if (servings != 2) {
          servings -= 2;
          this.servingText.setText(Integer.toString(servings));
          var3 = this.ingredList.iterator();

          while (var3.hasNext()) {
              ingred = var3.next();
              ingred.halfAmount(servings + 2);
          }

          this.ingredientsTable.setItems(FXCollections.observableArrayList(this.ingredList));
          this.ingredientsTable.refresh();
      }
  }

  @FXML
  private void handleCommentsButtonClick(MouseEvent event) {
    try {
        // Load the CommentDisplay.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentDisplay.fxml"));
        Parent commentDisplayParent = loader.load();
        
        // Get the controller
        CommentDisplayController commentDisplayController = loader.getController();
        
        // Pass the user ID to CommentDisplayController
        commentDisplayController.setUser(this.user);
        commentDisplayController.setRecipe(this.recipe);
        
        // Create a new scene with the loaded parent
        Scene scene = new Scene(commentDisplayParent);
        
        // Get the stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        // Set the scene to the stage
        stage.setScene(scene);
        stage.setTitle("Comment Display");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

@FXML
private void btnAddCommentOnAction(ActionEvent event) {
  try {
      // Load the CommentDisplay.fxml file
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentDisplay.fxml"));
      Parent commentDisplayParent = loader.load();
      Scene scene = new Scene(commentDisplayParent); // Create a new scene with the loaded parent

          // Check if user is null before getting user ID
          if (user != null) {
              System.out.println("User ID passed to CommentDisplayController: " + this.user);
          } else {
              System.out.println("User is null");
          }

          // Create a new stage for the pop-up
          Stage popupStage = new Stage();
          popupStage.setScene(scene); // Set the new scene to the stage
          popupStage.setTitle("The Cookbook - Comment View");
          popupStage.show(); // Show the pop-up stage

          // Get the controller
          CommentDisplayController controller = loader.getController();

          // Set the recipe ID immediately after loading the controller
          controller.setRecipe(this.recipe);

      // Pass any necessary data to the CommentDisplayController if needed
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @FXML
  private void showSharePopup(ActionEvent event) {
      try {
          // Load the ShareRecipePopup.fxml file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShareRecipePopup.fxml"));
          Parent shareRecipePopupParent = loader.load();

          ShareRecipeController controller = loader.getController();
          controller.setUser(currentUser); // Set the user before showing the popup
          controller.setSelectedUser(selectedUser); // Pass the selected user

          // Get the currently displayed recipe
          Recipe displayedRecipe = getDisplayedRecipe();
          controller.setRecipe(displayedRecipe); // Pass the displayed recipe

          Stage stage = new Stage();
          stage.setScene(new Scene(shareRecipePopupParent));
          stage.setTitle("The Cookbook - Share Recipe");
          stage.show();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public void onRecipeSelected(Recipe selectedRecipe) {
      setSelectedRecipe(selectedRecipe);
  }

  @FXML
  private void handleAddToWeeklyListButtonClick(ActionEvent event) {
      try {
          // Load the ModifyWeek FXML file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyWeek.fxml"));
          Parent root = loader.load();

          ModifyWeekController modifyWeekController = loader.getController();
          modifyWeekController.setRecipe(this.recipe);

          // Show in the new stage or replace the current scene content
          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
          stage.setScene(new Scene(root));
          stage.show();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @FXML
  private void handleAddTagButtonClick(ActionEvent event) {
      try {
          // Load the FXML file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/TagAddPopup.fxml"));
          Scene tagAddPopupScene = new Scene(loader.load());

          // Get the controller
          TagPopupController tagPopupController = loader.getController();

          // Set the recipe id
          tagPopupController.setRecipeId(this.recipe.getId());

          // Set the listener
          tagPopupController.setOnTagAddedListener((newTag) -> {
              // Add the name of the new tag
              tagNames.add(newTag.getName());
          });

          // Create a new stage
          Stage tagAddPopupStage = new Stage();
          tagAddPopupStage.initModality(Modality.APPLICATION_MODAL);
          tagAddPopupStage.setResizable(false);
          tagAddPopupStage.setTitle("Add Tag");

          // Set the icon of the stage
          Image icon = new Image("/images/logo.png");
          tagAddPopupStage.getIcons().add(icon);

          // Set the scene to the stage
          tagAddPopupStage.setScene(tagAddPopupScene);

          // Show the stage and wait until it is closed
          tagAddPopupStage.showAndWait();

      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @FXML
  private void handleRemoveTagButton(ActionEvent event) {
      if (tagList.getSelectionModel().getSelectedItem() == null) {
          errorLabel.setText("You need to select a tag to remove it.");
          errorLabel.setVisible(true);
      } else {
          // Get the selected tag name
          String selectedTagName = tagList.getSelectionModel().getSelectedItem();

          // Get the selected tag object
          Tag selectedTag = tagDao.getTagByName(selectedTagName);

          // Check if the tag is predefined
          if (selectedTag.isPredefinedTagFlag()) {
              errorLabel.setText("You aren't allowed to delete predefined tags");
              errorLabel.setVisible(true);
              return;
          }

          // Update the database to remove the tag from the recipe
          tagDao.removeTagFromRecipe(this.recipe.getId(), selectedTagName);

          // Remove the tag from the customTags list
          TagPopupController.customTags.remove(selectedTagName);

          // Fetch the updated list of tags for the recipe from the database
          Tag[] tags = tagDao.getAllTags(this.recipe.getId());

          // Clear the tagList and add the updated list of tags
          tagList.getItems().clear();
          for (Tag tag : tags) {
              tagList.getItems().add(tag.getName());
          }

          // Display success message
          errorLabel.setText("Tag removed successfully");
          errorLabel.setVisible(true);

          // Create a Timeline to hide the label after 2 seconds
          Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ae -> errorLabel.setVisible(false)));
          timeline.play();
      }
  }

  @FXML
  public void addToFavorites() {
      try {
          Connection connection = DatabaseConnection.getConnection();
          String sql = "INSERT INTO FavoriteRecipes (user_id, recipe_id) VALUES (?, ?)";
          PreparedStatement statement = connection.prepareStatement(sql);
          statement.setInt(1, this.user.getUserId());
          statement.setInt(2, this.recipe.getId());
          statement.executeUpdate();

          // Display a pop-up message
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Information");
          alert.setHeaderText(null);
          alert.setContentText("Recipe added to favorites.");
          alert.showAndWait(); // This will wait for the user to close the alert before continuing execution

      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  @FXML
  public void handleBackButtonAction() {
      try {
          FXMLLoader loader;
          Parent root;

          if (this.viewMessagesController != null) {
              loader = new FXMLLoader(getClass().getResource("/ViewMessages.fxml"));
              root = loader.load();

              ViewMessagesController viewMessagesController = loader.getController();
              viewMessagesController.setCurrentUser(this.user);
          } else {
              loader = new FXMLLoader(getClass().getResource("/MainPage.fxml"));
              root = loader.load();

              MainPageController mainPageController = loader.getController();
              mainPageController.setUser(this.user);
          }

          Stage stage = (Stage) backButton.getScene().getWindow();
          stage.setScene(new Scene(root));
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
