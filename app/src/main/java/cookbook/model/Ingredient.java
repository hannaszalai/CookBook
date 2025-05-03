package cookbook.model;

/*
 * Model class for Ingredients.
 */
public class Ingredient {
  private int id;
  private String name;

  public Ingredient(){
  }

  public Ingredient(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public Ingredient(String name) {
    this.name = name;
}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
}