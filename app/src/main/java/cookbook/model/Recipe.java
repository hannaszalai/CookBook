package cookbook.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Model class for Recipes.
 */
public class Recipe {
  private int id;
  private String name;
  private String summary; // The short description
  private String description; // The long text
  private List<RecipeIngredient> ingredients; // The list of ingredients
  private List<Tag> tags;
  private List<Comment> comments;
  private User author;


  public Recipe(String name, String summary, String description, String classification, User author) {
    this.name = name;
    this.summary = summary;
    this.description = description;
    this.ingredients = new ArrayList<RecipeIngredient>();
    this.tags = new ArrayList<Tag>();
    this.comments = new ArrayList<Comment>();
    this.author = author;
  }

  // No argument constructor
  public Recipe() {
    this.ingredients = new ArrayList<RecipeIngredient>();
    this.tags = new ArrayList<Tag>();
    this.comments = new ArrayList<Comment>();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newName) {
    this.name = newName;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String newSummary) {
    this.summary = newSummary;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

  public RecipeIngredient[] getIngredients() {
    return this.ingredients.toArray(new RecipeIngredient[0]);
  }

  public void addIngredient(RecipeIngredient ingredient) {
    this.ingredients.add(ingredient);
  }

  public Tag[] getTags() {
    return this.tags.toArray(new Tag[0]);
  }

  public void addTag(Tag tag) {
    this.tags.add(tag);
  }

  public Comment[] getComments() {
    return this.comments.toArray(new Comment[0]);
  }

  public void addComment(Comment comment) {
    this.comments.add(comment);
  }

  public void removeComment(Comment comment) {
    this.comments.remove(comment);
  }

  public User getAuthor() {
    return this.author;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long recipeId() {
    return this.id;
  }
}

