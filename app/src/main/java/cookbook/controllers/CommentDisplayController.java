package cookbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import cookbook.model.Comment;
import cookbook.model.Recipe;
import cookbook.DAOs.CommentDao;
import cookbook.model.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javafx.scene.Node;
import cookbook.UserSession;

/*
 * Controller class for the Comment Display.
 */
public class CommentDisplayController {
  private User user;
  private Recipe recipe;
  private CommentDao commentDao;
  private CommentController commentController;
  private Comment comment;

  @FXML
  private ListView<Comment> commentListView;
  @FXML
  private Button addComment, buttonEdit, buttonRemove, goBackBtn;

  public CommentDisplayController() {
      commentDao = new CommentDao();
  }

  public User getUser() {
      return this.user;
  }

  public void setUser(User user) {    
      if (user != null) {
          this.user = user;
          UserSession.getInstance(user); // Initialize UserSession with user
          System.out.println("User set in CommentDisplayController: " + this.user);
      } else {
          System.out.println("User is null in CommentDisplayController");
      }
  }

  public void setRecipe(Recipe recipe) {
      if (recipe != null) {
          this.recipe = recipe;
          loadComments(); // Load comments after recipe is set
      } else {
          System.out.println("Recipe set in comment normal: " + this.recipe);
      }
  }

  public void transitionToCommentController(User user, Recipe recipe) {
      CommentController commentController = new CommentController(this, user.getUserId(), recipe.getId());
      setCommentController(commentController);
  }

  public void setCommentController(CommentController commentController) {
      this.commentController = commentController;
      if (this.commentController != null) {
          this.commentController.setUser(this.user);
      }
  }

  // Load comments for the recipe
  private void loadComments() {
      if (recipe != null) {
          try {
              commentListView.getItems().addAll(commentDao.getCommentsByRecipeId(recipe.getId()));
          } catch (Exception e) {
              e.printStackTrace();
          }
      } else {
          System.out.println("Recipe is null in the load comments method");
      }
  }

  @FXML
  public void initialize() {
      setupButtons();
      if (goBackBtn != null) {
          goBackBtn.setOnAction(this::goBackBtnClick);
      } else {
          System.out.println("goBackBtn is null in initialize method");
      }
      this.user = UserSession.getInstance(this.user).getUser();
      if (this.user == null) {
          System.out.println("User is null in CommentDisplayController");
      } else {
          System.out.println("User set in CommentDisplayController: " + this.user);
      }
      if (commentDao == null) {
          System.out.println("commentDao is null in initialize method");
      }
  }

  // Set up the buttons
  private void setupButtons() {
      if (buttonRemove != null) {
          buttonRemove.setOnAction(event -> {
              Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
              if (selectedComment != null) {
                  try {
                      if (commentDao != null) {
                          commentDao.removeComment(selectedComment);
                          commentListView.getItems().remove(selectedComment);
                      } else {
                          System.out.println("commentDao is null in setupButtons method");
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });
      } else {
          System.out.println("buttonRemove is null in setupButtons method");
      }
  
      if (buttonEdit != null) {
          buttonEdit.setOnAction(this::editComment);
      } else {
          System.out.println("buttonEdit is null in setupButtons method");
      }
  }

  @FXML
  public void removeComment(ActionEvent event) {
      Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
      if (selectedComment != null) {
          try {
              commentDao.removeComment(selectedComment);
              commentListView.getItems().remove(selectedComment);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }

  @FXML
  private void addCommentClick(ActionEvent event) {
      addComment.setOnAction(this::addCommentClick);
      try {
          // Load the CommentWrite.fxml file
          URL mainPageUrl = getClass().getResource("/CommentWrite.fxml");
          if (mainPageUrl == null) {
              throw new FileNotFoundException("FXML file not found");
          }
          FXMLLoader loader = new FXMLLoader(mainPageUrl);

          // Set the controller factory
          loader.setControllerFactory(c -> {
              CommentController controller = new CommentController();
              controller.setRecipe(this.recipe);
              controller.setUser(this.user);
              controller.setCommentDisplayController(this); // Set the CommentDisplayController
              return controller;
          });

          Parent mainPageRoot = loader.load();

          // Create a new scene with the loaded parent
          Scene scene = new Scene(mainPageRoot);

          // Create a new stage for the pop-up
          Stage popupStage = new Stage();
          popupStage.setScene(scene); // Set the new scene to the stage
          popupStage.setTitle("The Cookbook - Write Comment");
          popupStage.show(); // Show the pop-up stage

          // Close the current window
          Node source = (Node) event.getSource();
          Stage currentStage = (Stage) source.getScene().getWindow();
          currentStage.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @FXML
  private void goBackBtnClick(ActionEvent event) {
      // Get the current stage
      Stage stage = (Stage) goBackBtn.getScene().getWindow();

      // Close the stage
      stage.close();
  }
  
  @FXML
  private void editComment(ActionEvent event) {
      Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
      if (selectedComment != null) {
          try {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentEdit.fxml"));
              Parent root = loader.load();
              CommentController commentController = loader.getController();
              System.out.println("Retrieved CommentController: " + commentController);
              commentController.setComment(selectedComment);
              commentController.setRecipe(this.recipe);
              System.out.println("Passed comment to CommentController: " + selectedComment.getContent());

              // Create a new scene with the loaded parent
              Scene scene = new Scene(root);

              // Create a new stage for the pop-up
              Stage popupStage = new Stage();
              popupStage.setScene(scene); // Set the new scene to the stage
              popupStage.setTitle("The Cookbook - Edit Comment");
              popupStage.show(); // Show the pop-up stage

              // Close the current window
              Node source = (Node) event.getSource();
              Stage currentStage = (Stage) source.getScene().getWindow();
              currentStage.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

  @FXML
  public void replyOnComment(ActionEvent event) {
      Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
      if (selectedComment != null) {
          try {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentReply.fxml"));

              // Set the controller factory
              loader.setControllerFactory(c -> {
                  CommentReplyController controller = new CommentReplyController();
                  controller.setComment(selectedComment);
                  controller.setRecipe(this.recipe);
                  controller.setUser(this.user);
                  return controller;
              });

              Parent root = loader.load();

              // Create a new scene with the loaded parent
              Scene scene = new Scene(root);

              // Create a new stage for the pop-up
              Stage popupStage = new Stage();
              popupStage.setScene(scene); // Set the new scene to the stage
              popupStage.setTitle("The Cookbook - Reply Comment");
              popupStage.show(); // Show the pop-up stage

              // Close the current window
              Node source = (Node) event.getSource();
              Stage currentStage = (Stage) source.getScene().getWindow();
              currentStage.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

  public void displayComment(Comment comment) {
      this.comment = comment;
  
      try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentEdit.fxml"));
          Parent root = loader.load();
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();
  
          CommentController commentController = loader.getController();
          commentController.setComment(comment);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}