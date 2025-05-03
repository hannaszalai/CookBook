package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

/*
 * Controller class for the Help Page.
 */
public class HelpController {

    @FXML
    private Button goBackButton, searchButton;
    @FXML
    private ChoiceBox<String> selectSearch;
    @FXML
    private Text welcomeText;
    @FXML
    private ListView<String> selectables;
    @FXML
    private TextArea display;
    @FXML
    private TextField searchField;
    
    public void initialize() {
        // Enable text wrapping in the TextArea
        display.setWrapText(true);

        // Populate the ListView
        selectables.getItems().addAll("CreateRecipe", "ViewRecipe", "FavouriteRecipe", "Comment", "Login", "MainPage", "WeeklyDinner", "ShoppingList", "AdminPanel");

        // Handle the selection of each option
        selectables.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    // Other cases...
                    case "AdminPanel":
                        adminPanel();
                        break;
                    case "CreateRecipe":
                        createRecipe();
                        break;
                    case "ViewRecipe":
                        viewRecipe();
                        break;
                    case "FavouriteRecipe":
                        favouriteRecipe();
                        break;
                    case "Comment":
                        comment();
                        break;
                    case "Login":
                        login();
                        break;
                    case "MainPage":
                        mainPage();
                        break;
                    case "WeeklyDinner":
                        weeklyDinner();
                        break;
                    case "ShoppingList":
                        shoppingList();
                        break;
                }
            }
        // Enter key action for search field
        searchField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    searchButton.fire();
                    break;
                default:
                    break;
            }
        });
        });
    }
    
    public void comment() {
        display.setText("    Adding a Note: Imagine you're writing a note about a recipe you tried. You type what you want to say in a box and then click a button to save it.\r\n" + //
                        "\r\n" + //
                        "    Editing a Note: If you want to change something in your note, you can select it and click a button to edit. It opens a new page where you can make changes and save them.\r\n" + //
                        "\r\n" + //
                        "    Deleting a Note: If you decide you don't want a note anymore, you can select it and click a button to remove it.\r\n" + //
                        "\r\n" + //
                        "    Seeing Your Notes: All your notes show up in a list, like a list of messages on your phone. You can see what you wrote and make changes if you want.");
    }
    
    public void login() {
        display.setText("    Logging In: This page allows users to log into the app. Users enter their username and password, then click the \"Login\" button.\r\n" + //
                        "\r\n" + //
                        "    Admin Access: There's also an option to log in as an admin. If the \"Admin\" checkbox is selected, it means the user wants to log in with admin privileges.\r\n" + //
                        "\r\n" + //
                        "    Validation: When the user clicks the \"Login\" button, the app checks if the entered username and password are correct. If the login is successful, it takes the user to the appropriate page based on whether they are an admin or a regular user.\r\n" + //
                        "\r\n" + //
                        "    Loading Scenes: If the login is successful, the app loads the main page for regular users or the admin panel for admins. It switches to the corresponding scene accordingly.\r\n" + //
                        "\r\n" + //
                        "    Handling Enter Key: Users can also press the \"Enter\" key while typing their password to log in. This is a convenient alternative to clicking the \"Login\" button.");
    }
    
    public void mainPage() {
        display.setText("    Displaying Recipes: The main page displays a list of recipes. It fetches all the recipes from the database and shows them in a list on the screen.\r\n" + //
                        "\r\n" + //
                        "    Searching for Recipes: Users can search for recipes by name, tag, or ingredient. They enter their search query and choose the search criteria from a dropdown menu. Then, they click the search button to see the results.\r\n" + //
                        "\r\n" + //
                        "    Viewing Recipe Details: Users can view the details of a recipe by clicking on it in the list. This opens a new window where they can see the full recipe, including ingredients and instructions.\r\n" + //
                        "\r\n" + //
                        "    Adding New Recipes: There's a button to add a new recipe. When users click it, they're taken to a page where they can create a new recipe.\r\n" + //
                        "\r\n" + //
                        "    Weekly Dinner List: Users can view their weekly dinner list by clicking a button. This shows them a list of recipes they've planned for the week.\r\n" + //
                        "\r\n" + //
                        "    Help Center: There's a button to access the help center. Clicking this button opens a page where users can find information and support for using the app.\r\n" + //
                        "\r\n" + //
                        "    Logging Out: Users can log out of the app by clicking a button. This takes them back to the login page.");
    }
    
    public void weeklyDinner() {
        display.setText("    Displaying Weekly Dinner Lists: The controller displays a list of weeks, each containing a range of dates, in a ListView.\r\n" + //
                        "\r\n" + //
                        "    Loading Weeks: When the controller is initialized, it loads the weeks from the database and populates the ListView with them.\r\n" + //
                        "\r\n" + //
                        "    Selecting a Week: Users can select a week from the list. When a week is selected, the controller loads the recipes planned for each day of that week and displays them in separate ListViews for each day of the week.\r\n" + //
                        "\r\n" + //
                        "    Adding Weeks: Users can add new weeks by clicking the \"Add Weeks\" button. This opens a popup window where they can enter the start and end dates for the new week.\r\n" + //
                        "\r\n" + //
                        "    Viewing Recipes: There's a button to view recipes. However, the corresponding action method is currently commented out.\r\n" + //
                        "\r\n" + //
                        "    Navigating Back: Users can navigate back to the main page by clicking the \"Go Back\" button.");
    }
    
    public void shoppingList() {
        display.setText("    Viewing Weeks: The user sees a list of weeks, each representing a period. When a week is selected, the associated dishes for that week are displayed.\r\n" + //
                        "\r\n" + //
                        "    Viewing Dishes: Upon selecting a week, the user sees a list of dishes planned for that week. Each dish is a recipe they intend to cook.\r\n" + //
                        "\r\n" + //
                        "    Viewing Ingredients: After selecting a dish, the user sees a list of ingredients required to prepare that dish.\r\n" + //
                        "\r\n" + //
                        "    Navigation: There's a \"Back\" button to return to the main page of the app.");
    }

    @FXML
    public void goBack() {
    try {
        // Load the main page
        Parent root = FXMLLoader.load(getClass().getResource("/mainpage.fxml"));

        // Create a new scene with the main page
        Scene scene = new Scene(root);

        // Get the current stage
        Stage stage = (Stage) goBackButton.getScene().getWindow();

        // Set the new scene on the current stage
        stage.setScene(scene);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void createRecipe() {
        display.setText("Adding Ingredients: You have a box where you can pick ingredients from a list, like flour, sugar, or eggs. You can also type how much of each ingredient you need, like \"1 cup\" or \"2 teaspoons\".\r\n" + //
                        "\r\n" + //
                        "Writing the Recipe: There are big blank spaces where you can write the name of your recipe and describe how to make it. There's also a smaller space where you can give a quick description.\r\n" + //
                        "\r\n" + //
                        "Creating the Recipe: When you're done writing everything, you click a button that says \"Create Recipe\". This saves your recipe so you can find it later.\r\n" + //
                        "\r\n" + //
                        "Seeing Your Recipe: After you create your recipe, it's like putting it in a recipe book. You can always go back and look at it.\r\n" + //
                        "\r\n" + //
                        "Adding More Ingredients: If you forgot to add an ingredient while writing the recipe, there's a button for that too. You pick the ingredient from the list, type how much you need, and it gets added to your recipe.\r\n" + //
                        "\r\n" + //
                        "Going Back: If you want to leave the recipe page and go back to the main page of the app, there's a button for that as well. It's like closing a book and going back to the library.\r\n" + //
                        "\r\n" + //
                        "So, the app helps you organize your recipes by letting you write them down, add ingredients, and then save them to look at later. And if you need to go back to the main page, you can do that easily too.");
    }
    
    public void viewRecipe() {
        display.setText("    Viewing a Recipe: This page lets you look at a recipe. It shows you the name of the recipe, a short description, and a longer description. It also lists the ingredients you need.\r\n" + //
                        "\r\n" + //
                        "    Changing Servings: You can adjust the number of servings for the recipe. If you want to make more or fewer servings, you can click buttons to increase or decrease the number.\r\n" + //
                        "\r\n" + //
                        "    Adding Comments: There's a button to add comments about the recipe. When you click it, you can write your thoughts or suggestions.\r\n" + //
                        "\r\n" + //
                        "    Sharing and Saving: You have buttons to share the recipe with others or save it to your weekly list if you like it.\r\n" + //
                        "\r\n" + //
                        "    Adding Tags: You can add tags to the recipe, like \"easy\" or \"healthy\", to help you organize and find recipes later.\r\n" + //
                        "\r\n" + //
                        "    Navigating Back: If you want to go back to the main page of the app, there's a button for that too.\r\n" + //
                        "\r\n" + //
                        "Overall, this page is all about viewing a recipe, adjusting servings, leaving comments, organizing recipes with tags, and sharing or saving recipes. It's designed to be easy to use and helpful for anyone who loves cooking.");
    }
    
    public void favouriteRecipe() {
        display.setText("    Viewing Favorites: Upon opening the favorites page, the user sees a list of recipes they have marked as favorites.\r\n" + //
                        "\r\n" + //
                        "    Removing Recipes: The user can select a recipe from the favorites list and remove it by clicking the \"Remove\" button.\r\n" + //
                        "\r\n" + //
                        "    Navigation: There's a \"Back\" button to return to the main page of the app.\r\n" + //
                        "\r\n" + //
                        "Important Things to Remember:\r\n" + //
                        "\r\n" + //
                        "    Viewing Favorites: Simply open the favorites page to view all your favorite recipes.\r\n" + //
                        "\r\n" + //
                        "    Removing Recipes: Select a recipe from the list and click \"Remove\" to delete it from your favorites.\r\n" + //
                        "\r\n" + //
                        "    Navigating Back: Use the \"Back\" button to return to the main page.");
    }

    public void adminPanel() {
        display.setText("    User Management: The controller manages user data, including creating, modifying, and deleting users.\r\n" + //
                        "\r\n" + //
                        "    Displaying Users: It displays users in a TableView, showing their display name, username, and whether they are admins.\r\n" + //
                        "\r\n" + //
                        "    Initialization: The initialize() method is called when the controller is initialized. It populates the TableView with existing users.\r\n" + //
                        "\r\n" + //
                        "    Creating Users: The adminCreateUser() method is called when the \"Create User\" button is clicked. It retrieves the input data from text fields and checkboxes, adds a new user to the database, and refreshes the user list.\r\n" + //
                        "\r\n" + //
                        "    Modifying Users: The modifyUser() method is called when the \"Modify User\" button is clicked. It retrieves the selected user from the TableView, updates the user's data in the database based on the input fields, and refreshes the user list.\r\n" + //
                        "\r\n" + //
                        "    Deleting Users: The adminDeleteUser() method is called when the \"Delete User\" button is clicked. It retrieves the selected user from the TableView and deletes the user from the database.\r\n" + //
                        "\r\n" + //
                        "    Navigating Back: The backButton() method is called when the \"Back\" button is clicked. It navigates back to the login page.\r\n" + //
                        "\r\n" + //
                        "    Database Operations: Database operations are handled using JDBC. SQL queries are executed to interact with the users table in the database.\r\n" + //
                        "\r\n" + //
                        "    Password Hashing: A simple password hashing method hashPassword() is provided for hashing user passwords. (Note: In a real-world scenario, a more secure hashing mechanism should be used.)");
    }

    @FXML
    public void searchHelp() {
        String searchText = searchField.getText().toLowerCase();
    
        if (searchText.contains("admin") || searchText.contains("panel") || searchText.contains("user") || searchText.contains("manage")) {
            adminPanel();
            selectables.getSelectionModel().select("AdminPanel");
        } else if (searchText.contains("create") || searchText.contains("recipe") || searchText.contains("make") || searchText.contains("cook")) {
            createRecipe();
            selectables.getSelectionModel().select("CreateRecipe");
        } else if (searchText.contains("view") || searchText.contains("recipe") || searchText.contains("see") || searchText.contains("look")) {
            viewRecipe();
            selectables.getSelectionModel().select("ViewRecipe");
        } else if (searchText.contains("favourite") || searchText.contains("recipe") || searchText.contains("like")) {
            favouriteRecipe();
            selectables.getSelectionModel().select("FavouriteRecipe");
        } else if (searchText.contains("comment") || searchText.contains("feedback") || searchText.contains("opinion") || searchText.contains("thought") || searchText.contains("review") || searchText.contains("critique")) {
            comment();
            selectables.getSelectionModel().select("Comment");
        } else if (searchText.contains("login") || searchText.contains("sign") || searchText.contains("in") || searchText.contains("access") || searchText.contains("enter") || searchText.contains("start")) {
            login();
            selectables.getSelectionModel().select("Login");
        } else if (searchText.contains("main") || searchText.contains("page") || searchText.contains("home") || searchText.contains("start") || searchText.contains("begin") || searchText.contains("welcome")) {
            mainPage();
            selectables.getSelectionModel().select("MainPage");
        } else if (searchText.contains("weekly") || searchText.contains("dinner") || searchText.contains("meal") || searchText.contains("plan") || searchText.contains("schedule") || searchText.contains("calendar")) {
            weeklyDinner();
            selectables.getSelectionModel().select("WeeklyDinner");
        } else if (searchText.contains("shopping") || searchText.contains("list") || searchText.contains("buy") || searchText.contains("purchase") || searchText.contains("grocery") || searchText.contains("market")) {
            shoppingList();
            selectables.getSelectionModel().select("ShoppingList");
        } else {
            display.setText("No help topic found for your search.");
        }
    }
}