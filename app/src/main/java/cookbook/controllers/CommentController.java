package cookbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import cookbook.DatabaseConnection;
import cookbook.UserSession;
import cookbook.DAOs.CommentDao;
import cookbook.DAOs.RecipeDao;
import cookbook.model.Comment;
import cookbook.model.Recipe;
import cookbook.model.User;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/*
 * Controller class for the CommentController.
 */
public class CommentController extends RecipeDisplayController{
  private CommentDisplayController commentDisplayController;
  private CommentDao commentDao = new CommentDao();
  private RecipeDao recipeDao;
  private int user_id;
  private int recipe_id;
  private Recipe recipe;
  private User user;
  private String content;
  private Boolean editing;
  private Comment comment;

  @FXML
  private TextField commentField, commentFieldEdit;
  @FXML
  private Button btnAddCommentConfirm, btnSaveCommentConfirm, btnCancel;

  // Default constructor required by FXMLLoader
  public CommentController() {
      commentDao = new CommentDao();
      System.out.println("CommentController no-arg constructor called on instance: " + this.hashCode());
  }

  // Constructor with parameters
  public CommentController(CommentDisplayController commentDisplayController, int user_id, int recipe_id) {
      if (recipe_id <= 0) {
          throw new IllegalArgumentException("Recipe ID cannot be zero or negative");
      }
      this.commentDisplayController = commentDisplayController;
      this.commentDao = new CommentDao();
      this.user_id = user_id;
      this.recipe_id = recipe_id;
      System.out.println("Recipe ID is " + this.recipe_id);
      System.out.println("CommentController param constructor called on instance: " + this.hashCode());
  }

  public static CommentController createWithRecipeAndUser(Recipe recipe, User user) {
    CommentController controller = new CommentController();
    controller.setRecipe(recipe);
    controller.setUser(user);
    return controller;
  }

  public void setEditStatus(Boolean editing) {
    this.editing = editing;
  }

  public void setContent(String content) {
    this.content = content;
  }

      
  public void setUser(User user) {    
    if (user != null) {
        this.user = user;
        UserSession.getInstance(user);
        System.out.println("User set in CommentController: " + this.user);
    } else {
        System.out.println("User is null in CommentController");
    }
  }

  public void setCommentDisplayController(CommentDisplayController commentDisplayController) {
      this.commentDisplayController = commentDisplayController;
  }


  public void setRecipe(Recipe recipe) {
      if (recipe != null) {
          this.recipe = recipe;
      } else {
          System.out.println("Recipe is null");
      }
      System.out.println("Recipe set in c WRITE: " + this.recipe);
  }

  public void initialize() {
      System.out.println("CommentController initialized");
      System.out.println("commentFieldEdit is " + (commentFieldEdit == null ? "null" : "not null"));
      if (this.commentDisplayController != null) {
          this.user = this.commentDisplayController.getUser();
          if (this.user == null) {
              System.out.println("User is null in CommentController");
          } else {
              System.out.println("User set in CommentController: " + this.user);
          }
      } else {
          System.out.println("CommentDisplayController is null in CommentController");
      }
  }
  
  public void setComment(Comment comment) {
      this.comment = comment;
      if (comment != null && commentFieldEdit != null) {
          commentFieldEdit.setText(comment.getContent());
          System.out.println("Setting comment: " + comment.getContent());
      }
  }

  @FXML
  public void cancelButton() {
      try {
          // Load the CommentDisplay FXML file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentDisplay.fxml"));
          Parent root = loader.load();
      
          // Get the controller and set the recipe
          CommentDisplayController commentDisplayController = loader.getController();
          commentDisplayController.setRecipe(this.recipe);
      
          // Create a new stage and show it
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();

          // Get the current stage and close it
          Stage currentStage = (Stage) btnCancel.getScene().getWindow();
          currentStage.close();
      } catch (Exception e) {
          System.out.println("Error loading CommentDisplay: " + e.getMessage());
      }
  }

  // Method to check if the recipe ID is valid
  private boolean isRecipeIdValid(int recipeId) {
      try (Connection connection = DatabaseConnection.getConnection()) {
          String sql = "SELECT COUNT(*) AS count FROM Recipes WHERE recipe_id = ?";
          PreparedStatement statement = connection.prepareStatement(sql);
          statement.setInt(1, recipeId);
          ResultSet resultSet = statement.executeQuery();
          if (resultSet.next()) {
              int count = resultSet.getInt("count");
              return count > 0;
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return false;
  }

  // Method to add a comment
  @FXML
  private void addComment(ActionEvent event) {
      String comment = commentField.getText();
      if (comment.isEmpty()) {
          System.out.println("Please enter a comment.");
          return;
      }

      if (user == null) {
          System.out.println("User is null. Cannot add comment.");
          return;
      }

      if (this.recipe == null) {
          // Initialize the recipe object
          this.recipe = recipeDao.getRecipeById(this.recipe_id);
      }

      // Check if the recipeId is valid
      try {
          if (!isRecipeIdValid(recipe.getId())) { // Use recipe.getId()
              Platform.runLater(() -> {
                  // Display an alert dialog for invalid recipe ID
                  Alert alert = new Alert(Alert.AlertType.ERROR);
                  alert.setTitle("Invalid Recipe ID");
                  alert.setHeaderText(null);
                  alert.setContentText("The provided recipe ID is invalid.");
                  alert.showAndWait();
              });
              return;
          }
      } catch (Exception ex) {
          System.out.println("Error checking recipe ID: " + ex.getMessage());
          return;
      }

      try (Connection connection = DatabaseConnection.getConnection()) {
          String sql = "INSERT INTO Comments (recipe_id, user_id, comment) VALUES (?, ?, ?)";
          PreparedStatement statement = connection.prepareStatement(sql);
          statement.setInt(1, recipe.getId()); // Use recipe.getId()
          statement.setInt(2, user.getUserId()); // Use user.getUserId()
          statement.setString(3, comment);
          int rowsInserted = statement.executeUpdate();
          if (rowsInserted > 0) {
              System.out.println("Comment added successfully.");
          
              // Show an alert message
              Platform.runLater(() -> {
                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                  alert.setTitle("Success");
                  alert.setHeaderText(null);
                  alert.setContentText("Comment added successfully.");
                  alert.show();
          
                  // Close the alert and the current window after 1 seconds
                  PauseTransition delay = new PauseTransition(Duration.seconds(1));
                  delay.setOnFinished(e -> {
                      alert.close();
                      // Close the current window
                      Node source = (Node) event.getSource();
                      Stage currentStage = (Stage) source.getScene().getWindow();
                      currentStage.close();
                  });
                  delay.play();
              });
          } else {
              System.out.println("Failed to add comment.");
          }
      } catch (Exception ex) {
          System.out.println("Database error: " + ex.getMessage());
      }
      try {
          // Load the CommentDisplay FXML file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentDisplay.fxml"));
          Parent root = loader.load();
      
          // Get the controller and set the recipe
          CommentDisplayController commentDisplayController = loader.getController();
          commentDisplayController.setRecipe(this.recipe);
      
          // Create a new stage and show it
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();
      
          // Close the current window
          Node source = (Node) event.getSource();
          Stage currentStage = (Stage) source.getScene().getWindow();
          currentStage.close();
      } catch (Exception e) {
          System.out.println("Error adding comment: " + e.getMessage());
      }

  }


  @FXML
  private void saveComment(ActionEvent event) {
      System.out.println("saveComment called on instance: (CommentController)" + this.hashCode());
      String newContent = commentFieldEdit.getText();
      System.out.println("saveComment called");
      System.out.println("commentFieldEdit is " + (commentFieldEdit == null ? "null" : "not null"));
      if (!newContent.isEmpty()) {
          comment.setContent(newContent);
          try {
              if (commentDao != null) {
                  commentDao.updateComment(comment);
              } else {
                  System.out.println("commentDao is null in saveComment method");
              }

              // Check if recipe is null
              if (this.recipe == null) {
                  System.out.println("recipe is null in saveComment method");
                  return;
              }

              // Load the CommentDisplay FXML file
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentDisplay.fxml"));
              Parent root = loader.load();

              // Get the controller and set the recipe
              CommentDisplayController commentDisplayController = loader.getController();
              commentDisplayController.setRecipe(this.recipe);

              // Create a new stage and show it
              Stage stage = new Stage();
              stage.setScene(new Scene(root));
              stage.show();

              // Close the current window
              Node source = (Node) event.getSource();
              Stage currentStage = (Stage) source.getScene().getWindow();
              currentStage.close();
          } catch (Exception e) {
              System.out.println("Error adding comment: " + e.getMessage());
          }
      }
  }
}