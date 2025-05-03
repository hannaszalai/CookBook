package cookbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import cookbook.UserSession;
import cookbook.model.User;

/*
 * Controller class for the Login page.
 */
public class LoginController {
  @FXML
  private Button loginButton;
  @FXML
  private TextField usernameTextField;
  @FXML
  private PasswordField enterPasswordField;
  @FXML
  private Label loginMessageLabel;
  @FXML
  private CheckBox adminCheckBox;
  
  @FXML
  public void loginButtonOnAction(ActionEvent event) throws IOException {
    boolean isAdminRequest = adminCheckBox.isSelected();
    User user = validateLogin(isAdminRequest);
    if (user != null) {
        // Set the current user in the UserSession
        UserSession.getInstance(user).loginUser(user);
      if (isAdminRequest) {
        loginMessageLabel.setText("Admin login successful!");
        loadScene("/adminpanel.fxml", user);
      } else {
        loginMessageLabel.setText("User login successful!");
        loadScene("/mainpage.fxml", user);
      }
    } else {
      loginMessageLabel.setText("Invalid login. Try again.");
    }
  }




  
  @FXML
  public void enterPasswordFieldOnKeyPressed(KeyEvent keyEvent) throws IOException {
      if (keyEvent.getCode() == KeyCode.ENTER) {
          loginButtonOnAction(null);
      }
  }

   // Validate the login credentials
   public User validateLogin(boolean isAdminRequest) {
    return null;
  }

  private void loadScene(String fxmlPath, User user) {
    return;
  }

  
}
