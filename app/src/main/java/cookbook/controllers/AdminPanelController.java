// package cookbook.controllers;

// import javafx.fxml.FXML;
// import javafx.fxml.FXMLLoader;
// import javafx.fxml.Initializable;
// import javafx.scene.Node;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.CheckBox;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableView;
// import javafx.scene.control.TextField;
// import javafx.scene.control.cell.PropertyValueFactory;
// import javafx.stage.Stage;
// import java.net.URL;
// import java.util.ResourceBundle;

// import cookbook.UserSession;
// import cookbook.DAOs.AdminDao;
// import cookbook.model.User;
// import cookbook.model.User1;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.event.ActionEvent;
// import java.io.IOException;
// import javafx.scene.control.Label;

// /*
//  * Controller class for the AdminPanel.
//  */
// public class AdminPanelController implements Initializable {
//   private AdminDao adminDao = new AdminDao();

//   @FXML
//   private TableView<User1> userlst;
//   @FXML
//   private Button modifyuser, deleteuser, createuser, back;
//   @FXML
//   private TextField txtUserName, txtDisplayName, txtPassword;
//   @FXML
//   private CheckBox adminCheckbox;
//   @FXML
//   private Label errorLabel;

//   /**
//    * Initializes the controller after its root element has been completely
//    * processed.
//    */
//   @Override
//   public void initialize(URL location, ResourceBundle resources) {
//     if (UserSession.isUserSet()) {
//         User currentUser = UserSession.getInstance(null).getUser();
//         System.out.println("Current user in AdminPanel: " + currentUser.getUsername());
//     } else {
//         System.out.println("No user is currently set in AdminPanel.");
//     }
//     // Only add columns if they haven't been added yet
//     if (userlst.getColumns().isEmpty()) {
//       // Initialize the user list table and load user data
//       TableColumn<User1, String> idCol = createTableColumn("ID", "id");
//       TableColumn<User1, String> nameCol = createTableColumn("Display Name", "name");
//       TableColumn<User1, String> usernameCol = createTableColumn("Username", "username");
//       TableColumn<User1, Boolean> adminCol = createTableColumn("Admin", "admin");
  
//       // Add columns one by one
//       userlst.getColumns().add(idCol);
//       userlst.getColumns().add(nameCol);
//       userlst.getColumns().add(usernameCol);
//       userlst.getColumns().add(adminCol);
//     }
//     refreshUserList();
//   }

//   // Refreshes the data displayed in the admin panel.
//   public void refreshData() {
//     try {
//       ObservableList<User1> users = FXCollections.observableArrayList(AdminDao.getUsers());
//       userlst.setItems(users);
//     } catch (Exception e) { // Catch the more general exception
//       showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
//     }
//   }

//   /**
//    * Displays alert messages.
//    * This method can be called by other methods to show alerts for different
//    * purposes.
//    *
//    * @param title   the title of the alert dialog.
//    * @param content the message content of the alert.
//    * @param type    the type of alert, which defines the icon and behavior.
//    */
//   public void showAlert(String title, String content, Alert.AlertType type) {
//     Alert alert = new Alert(type);
//     alert.setTitle(title);
//     alert.setHeaderText(null);
//     alert.setContentText(content);
//     alert.showAndWait();
//   }

//   // Helper method to create a table column.
//   public <T> TableColumn<User1, T> createTableColumn(String label, String property) {
//     TableColumn<User1, T> column = new TableColumn<>(label);
//     column.setCellValueFactory(new PropertyValueFactory<>(property));
//     return column;
//   }

//   @FXML
//   public void createUser(ActionEvent event) {
//     String displayName = txtDisplayName.getText();
//     String username = txtUserName.getText();
//     String password = hashPassword(txtPassword.getText());
//     boolean isAdmin = adminCheckbox.isSelected(); // Checks if the admin checkbox is selected

//     try {
//       // Call addUser on the adminDao instance
//       adminDao.addUser(displayName, username, password, isAdmin);
//       refreshUserList();
//       errorLabel.setText("User created successfully.");
//     } catch (Exception e) {
//       errorLabel.setText("Error creating user: " + e.getMessage());
//       e.printStackTrace();
//     }
//   }

//   // Method to refresh the user list and update the TableView
//   public void refreshUserList() {
//     try {
//       userlst.getItems().clear();
//       // Call getUsers() on the adminDao instance
//       ObservableList<User1> users = FXCollections.observableArrayList(adminDao.getUsers());
//       userlst.setItems(users);
//     } catch (Exception e) {
//       errorLabel.setText("Error loading user: " + e.getMessage());
//       e.printStackTrace();
//     }
//   }

//   @FXML
//   public void modifyUser(ActionEvent event) {
//     User1 selectedUser = userlst.getSelectionModel().getSelectedItem();
//     if (selectedUser != null) {
//       String newDisplayName = txtDisplayName.getText();
//       String newUsername = txtUserName.getText();
//       String newPassword = hashPassword(txtPassword.getText());
//       boolean isAdmin = adminCheckbox.isSelected();

//       try {
//         // Call editUser on the adminDao instance
//         adminDao.editUser(newDisplayName, newUsername, newPassword, Integer.parseInt(selectedUser.getId()), isAdmin);
//         refreshUserList();
//         errorLabel.setText("User modified successfully.");
//       } catch (Exception e) {
//         errorLabel.setText("Error modifying user: " + e.getMessage());
//         e.printStackTrace();
//       }
//     } else {
//       errorLabel.setText("No user selected for deletion.");
//     }
//   }

//   // FXML action to delete a user
//   @FXML
//   public void adminDeleteUser(ActionEvent event) {
//     User1 selectedUser = userlst.getSelectionModel().getSelectedItem();
//     if (selectedUser != null) {
//       try {
//         // Call deleteUser on the adminDao instance
//         adminDao.deleteUser(selectedUser.getName(), selectedUser.getUsername(), selectedUser.getPassword());
//         refreshUserList();
//         errorLabel.setText("User deleted successfully.");
//       } catch (Exception e) {
//         errorLabel.setText("Error deleting user: " + e.getMessage());
//         e.printStackTrace();
//       }
//     } else {
//       errorLabel.setText("No user selected for deletion.");
//     }
//   }

//   // FXML action to go back to the previous page
//   @FXML
//   public void backButton(ActionEvent event) {
//     LoginController loginController = new LoginController();
//     loginController.handleLogout();

//     try {
//       FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
//       Parent root = loader.load();
//       Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//       Scene scene = new Scene(root);
//       stage.setScene(scene);
//       stage.show();
//     } catch (IOException e) {
//       errorLabel.setText("Error loading the login page: " + e.getMessage());
//       e.printStackTrace();
//     }
//   }

//   // Utility method for password hashing
//   private static String hashPassword(String password) {
//     return password;
//   }

//   // @FXML
//   // public void logout(ActionEvent event) {
//   //   LoginController loginController = new LoginController();
//   //   loginController.handleLogout();
//   // }
// }
