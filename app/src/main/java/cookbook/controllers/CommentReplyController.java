package cookbook.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import cookbook.UserSession;
import cookbook.DAOs.CommentDao;
import cookbook.model.Recipe;
import cookbook.model.User;
import cookbook.model.Comment;
import javafx.util.Duration;

/*
 * Controller class for Comment Reply extra feature.
 */
public class CommentReplyController extends CommentDisplayController{
    private CommentDisplayController commentDisplayController;
    private CommentDao commentDao = new CommentDao();
    private Comment parentComment;
    private Recipe recipe;
    private User user;

    @FXML
    private TextField commentFieldReply;
    @FXML
    private Button btnCancel, btnSaveCommentConfirm;

    // No-arg constructor
    public CommentReplyController() {
    }

    // Constructor with CommentDisplayController parameter
    public CommentReplyController(CommentDisplayController commentDisplayController) {
        this.commentDisplayController = commentDisplayController;
        this.commentDao = new CommentDao();
        System.out.println("CommentReplyController no-arg constructor called on instance: " + this.hashCode());
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void setComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void setRecipe(Recipe recipe) {
        if (recipe != null) {
            this.recipe = recipe;
        } else {
            System.out.println("Recipe is null");
        }
        System.out.println("Recipe set in commentReply: " + this.recipe);
    }

    public void setUser(User user) {    
        if (user != null) {
            this.user = user;
            UserSession.getInstance(user);
            System.out.println("User set in CommentReplyController: " + this.user);
        } else {
            System.out.println("User is null in CommentReplyController");
        }
    }

    @FXML
    private void submitReply(ActionEvent event) {
        String replyContent = commentFieldReply.getText();
        if (replyContent.isEmpty()) {
            System.out.println("Please enter a reply.");
            return;
        }

        // Check if the recipe is null before trying to use it
        if (this.recipe == null) {
            System.out.println("Recipe is null. Cannot add reply.");
            return;
        }

        // Check if the user is null before trying to use it
        if (this.user == null) {
            System.out.println("User is null. Cannot add reply.");
            return;
        }

        // Check if the parentComment is null before trying to use it
        if (this.parentComment == null) {
            System.out.println("Parent comment is null. Cannot add reply.");
            return;
        }

        try {
            if (commentDao != null) {
                commentDao.addComment(user.getUserId(), this.recipe.getId(), this.parentComment.getId(), replyContent); // Pass the appropriate fields to addComment
                System.out.println("Reply added successfully.");
    
                // Show an alert message
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Reply added successfully.");
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
            } else {
                System.out.println("commentDao is null in submitReply method");
            }
        } catch (Exception e) {
            System.out.println("Error adding reply: " + e.getMessage());
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
            
            if (this.recipe != null) {
                commentDisplayController.setRecipe(this.recipe);
            } else {
                // Handle the situation where this.recipe is null
                // For example, you could print an error message:
                System.out.println("Error: recipe is null");
            }
        
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
}