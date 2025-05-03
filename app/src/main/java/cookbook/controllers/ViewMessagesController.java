package cookbook.controllers;

import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.sql.Connection;
import cookbook.DAOs.MessageDao;
import cookbook.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import cookbook.DatabaseConnection;
import cookbook.model.Message;
import cookbook.model.Recipe;

/*
 * Controller class for viewing messages.
 */
public class ViewMessagesController {
    private MessageDao messageDao;
    private User user; 
    private ViewMessagesController viewMessagesController;
    private ObservableList<Message> observableMessages;

    @FXML
    private Label Sender;
    @FXML
    private ListView<Message> ViewMessage;
    @FXML
    private Button deleteButton, backbutton, recipeButton;
    @FXML
    private TextArea content;

    public void setCurrentUser(User user) {
        this.user = user;
        System.out.println("User set in inbox: " + this.user);
        fetchAndDisplayMessages();
    }

    public void setViewMessagesController(ViewMessagesController viewMessagesController) {
        this.viewMessagesController = viewMessagesController;
    }

    public void transitionToRecipeDisplayController(Recipe recipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecipeView.fxml"));
            Parent root = loader.load();

            RecipeDisplayController recipeDisplayController = loader.getController();
            recipeDisplayController.setRecipe(recipe);
            recipeDisplayController.setViewMessagesController(this);


            Stage stage = (Stage) recipeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        // Initialize the observable list
        observableMessages = FXCollections.observableArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            messageDao = new MessageDao(connection);
            fetchAndDisplayMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add action to recipeButton
        recipeButton.setOnAction(event -> {
            // Get the selected message
            Message selectedMessage = ViewMessage.getSelectionModel().getSelectedItem();

            // Check if a message is selected and it has a linked recipe
            if (selectedMessage != null && selectedMessage.getRecipe() != null) {
                // Open the recipe
                openRecipe(selectedMessage.getRecipe());
            }
        });

        // Clear the ListView
        ViewMessage.getItems().clear();

        // Fetch and display messages
        fetchAndDisplayMessages();
    }


    private void deleteSelectedMessage() {
        // Get the selected message
        Message selectedMessage = ViewMessage.getSelectionModel().getSelectedItem();

        // Check if a message is selected
        if (selectedMessage != null) {
            // Delete the message from the database
            messageDao.deleteMessage(selectedMessage);

            // Remove the message from the observable list
            observableMessages.remove(selectedMessage);

 
            // Remove the message from the ListView
            ViewMessage.getItems().remove(selectedMessage);
        } else {
            System.out.println("No message selected.");
        }
    }

    private void fetchAndDisplayMessages() {
        System.out.println("Current user: " + user);

        List<Message> messages = messageDao.getMessagesByReceiver(user);
        System.out.println("Fetched messages: " + messages);

        // Reverse the list of messages
        Collections.reverse(messages);

        ObservableList<Message> observableMessages = FXCollections.observableArrayList(messages);
        System.out.println("Observable messages: " + observableMessages);

        ViewMessage.setItems(observableMessages);
    
        ViewMessage.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSender().getUsername() + ": " + item.getContent());
                }
            }
        });
    
        // Add event listener to ListView items
        ViewMessage.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Display the full content of the clicked message in the TextArea
            content.setText(newValue.getContent());
        });
    }

    public void handleRecipeButtonAction(ActionEvent event) {
        // Get the selected message
        Message selectedMessage = ViewMessage.getSelectionModel().getSelectedItem();
    
        // Check if a message is selected and it has a linked recipe
        if (selectedMessage != null && selectedMessage.getRecipe() != null) {
            // Open the recipe view
            openRecipe(selectedMessage.getRecipe());
        }
    }

    private void openRecipe(Recipe recipe) {
        try {
            // Load the recipe view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecipeView.fxml"));
            Scene recipeViewScene = new Scene(loader.load());
    
            // Get the controller and pass the recipe to it
            RecipeDisplayController controller = loader.getController();
            controller.setRecipe(recipe);
            controller.setUser(user);

    
            // Get the current stage
            Stage stage = (Stage) recipeButton.getScene().getWindow();
    
            // Change the scene to the recipe view
            stage.setScene(recipeViewScene);
            System.out.println("Recipe in openRecipe: " + recipe);
            System.out.println("User in openRecipe: " + user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonAction(ActionEvent event) {
        deleteSelectedMessage();
    }

    @FXML
    public void handleBackButtonAction() {
        try {
            // Load the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
            Scene mainPageScene = new Scene(loader.load());
    
            // Get the controller and initialize it
            MainPageController controller = loader.getController();
            controller.initialize();
            controller.setUser(user);

    
            // Get the current stage
            Stage stage = (Stage) backbutton.getScene().getWindow();
    
            // Change the scene to the main page
            stage.setScene(mainPageScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
