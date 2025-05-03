package cookbook.controllers;


import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import cookbook.DatabaseConnection;
import cookbook.DAOs.MessageDao;
import cookbook.DAOs.UserDao;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import cookbook.model.User;
import cookbook.model.Recipe;
import javafx.util.StringConverter;
import cookbook.UserSession;

/*
 * Controller class for sharing a recipe.
 */
public class ShareRecipeController {
    private UserDao userDao;
    private Connection connection;
    private MessageDao messageDao;
    private Recipe recipe;
    private User user;
    private List<User> users;
    private User selectedUser = null;
    private User loggedInUser;

    @FXML
    private ChoiceBox<User> userSelector;
    @FXML
    private TextArea messageContent;
    @FXML
    private Button cancelButton, sendButton;

    public ShareRecipeController() {
        this(new User());
    }

    public ShareRecipeController(User user) {
        this.user = user;
        try {
            this.connection = DatabaseConnection.getConnection();
            this.messageDao = new MessageDao(connection);
            this.userDao = new UserDao(connection);
            this.users = userDao.getAllOtherUsers(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set in ShareRecipeController: " + this.user);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;
        System.out.println("SelectedUser set in ShareRecipeController: " + this.selectedUser);
    }

    public void setRecipeAndUser(Recipe recipe, User user) {
        this.recipe = recipe;
        this.user = user;

        this.users = userDao.getAllOtherUsers(user.getUserId());

        userSelector.getItems().clear();
        for (User otherUser : users) {
            userSelector.getItems().add(otherUser);
        }
    }

    public void populateUserSelector() {
        List<User> users = userDao.getAllOtherUsers(user.getUserId());
        User currentUser = getCurrentUser();

    
        // Clear the ChoiceBox
        userSelector.getItems().clear();
    
        // Add users to the ChoiceBox
        for (User user : users) {
            if (user.getUserId()!= currentUser.getUserId()) {
            userSelector.getItems().add(user);
        }
    }
        // If you want to select the first user by default
        if (!users.isEmpty()) {
            userSelector.getSelectionModel().selectFirst();
        }
    }

    public void initialize() {
        // Set the converter to display the username
        userSelector.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getUsername() : "";
        }

            @Override
            public User fromString(String username) {
                return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
        }
    });

        // Populate the userSelector with users from the database
        populateUserSelector();

        // Set this.user to the current user
        this.user = getCurrentUser();

        // Select the first user by default
    if (!userSelector.getItems().isEmpty()) {
        userSelector.getSelectionModel().selectFirst();
    }

    this.userSelector.getSelectionModel().selectedIndexProperty().addListener(
        (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            selectedUser = userSelector.getItems().get(newValue.intValue());
            sendButton.setDisable(false);
    }
    );

    this.sendButton.setDisable(true);
    }

    public void CancelMessage(ActionEvent event) {
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }
    
    public void SendMessage(ActionEvent event) throws IOException, InterruptedException {
        if (this.user == null) {
            this.user = getCurrentUser();
        }

        if (!userDao.userIdExists(this.user.getUserId()) || !userDao.userIdExists(this.selectedUser.getUserId())) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Sender or receiver does not exist!");
            errorAlert.showAndWait();
            return;
        }

        if (this.user == null) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("You need to be logged in to send a message!");
            errorAlert.showAndWait();
            return;
        }

        if (this.selectedUser == null || this.recipe == null) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("You need to select a user to send the message to!");
            errorAlert.showAndWait();
            return;
        }

        if (this.selectedUser == null) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("You need to select a user to send the message to!");
            errorAlert.showAndWait();
            return;
        }

        messageDao.sendMessage(messageContent.getText(), recipe, user, selectedUser);

        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setHeaderText("Sent message to " + selectedUser.getUsername());
        successAlert.showAndWait();

        CancelMessage(event);
    }

    private User getCurrentUser() {
        return UserSession.getInstance(null).getUser();
    }
}