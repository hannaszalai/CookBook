package cookbook.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import cookbook.model.Recipe;
import cookbook.model.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import cookbook.DAOs.RecipeDao;
import cookbook.DAOs.TagDao;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/*
 * Controller class for the Tag Popup.
 */
public class TagPopupController extends RecipeDisplayController {
  private int recipeId;
  public static List<String> customTags = new ArrayList<>();
  private TagDao tagDao = new TagDao();
  private OnTagAddedListener onTagAddedListener;

  // Interface to listen for tag added events
  public interface OnTagAddedListener {
    void onTagAdded(Tag newTag);
  }

  @FXML
  private ChoiceBox<String> selectTag;
  @FXML
  private TextField EnterTagField;
  @FXML
  private Button btnAddTagConfirm;


  public void setRecipeId(int recipeId) {
    this.recipeId = recipeId;
  }

  public void setOnTagAddedListener(OnTagAddedListener onTagAddedListener) {
    this.onTagAddedListener = onTagAddedListener;
  }

  public void initialize() {
      // Populate the ChoiceBox with predefined choices
      List<String> choices = new ArrayList<>(Arrays.asList("Vegetarian", "Vegan", "Lactose Free", "Gluten Free", "Starter", "Main Course", "Dessert and Sweets"));
      choices.addAll(customTags); // Add the custom tags to the choices
      choices.add("+ CUSTOM");
      selectTag.setItems(FXCollections.observableArrayList(choices));

      // Add a listener to the ChoiceBox
      selectTag.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
          if (newValue.equals("+ CUSTOM")) {
            EnterTagField.setDisable(false);
          } else {
            EnterTagField.setDisable(true);
          }
      });

  // Add an action to the button
    btnAddTagConfirm.setOnAction(event -> {
        String tag;
        if (EnterTagField.isDisable()) {
            tag = selectTag.getValue();
        } else {
            tag = EnterTagField.getText();
            customTags.add(tag);
        }
        setRecipeId(this.recipeId);
        addTag(tag, this.recipeId);
    });
  }

  @FXML
  public void handleAddTagConfirmButton(MouseEvent event) {
      String tagName = EnterTagField.getText().trim();
      if (!tagName.isEmpty()) {
          setRecipeId(this.recipeId);
          addTag(tagName, this.recipeId);
          EnterTagField.clear();
      }
  }

  public boolean recipeExists(int recipeId) {
    RecipeDao recipeDao = new RecipeDao();
    Recipe recipe = recipeDao.searchRecipeById(recipeId);
    return recipe != null;
  }

  private void addTag(String tag, int recipeId) {
    Tag newTag = new Tag();
    newTag.setName(tag);

    // Check if the tag is predefined
    List<String> predefinedTags = Arrays.asList("Vegetarian", "Vegan", "Lactose Free", "Gluten Free", "Starter", "Main Course", "Dessert and Sweets");
    if (predefinedTags.contains(tag)) {
        newTag.setPredefinedTagFlag(true);
    }

    try {
        // Check if a tag with the same id already exists
        Tag existingTag = tagDao.getTagById(newTag.getId());
        if (existingTag != null) {
            throw new Exception("A tag with the name " + newTag.getName() + " already exists.");
        }
        tagDao.addTag(recipeId, newTag);
        showPopup("Tag added successfully");
        if (onTagAddedListener != null) {
            onTagAddedListener.onTagAdded(newTag);
        }
    } catch (Exception e) {
        showPopup("Error while adding tag: " + e.getMessage());
    }
  }

  // Method to show a popup message
  private void showPopup(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}