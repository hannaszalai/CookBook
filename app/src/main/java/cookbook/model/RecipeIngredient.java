package cookbook.model;

/*
 * Model class for Recipe Ingredients.
 */
public class RecipeIngredient {
  private Long id;
  private Ingredient ingredient;
  private Recipe recipe;
  private float amount; 
  private String unit;

  public RecipeIngredient(){}

  public RecipeIngredient(Ingredient ingredient, float amount, String unit, Recipe recipe) {
    this.ingredient = ingredient;
    this.amount = amount;
    this.unit = unit;
    this.recipe = recipe;
  }

  public RecipeIngredient(String ingredientName, String amount, String unit) {
    this.ingredient = new Ingredient(ingredientName);
    this.amount = Float.parseFloat(amount);
    this.unit = unit;
}

  
  public float getAmount() {
    return this.amount;
  }

  public String getFormatted() {
    return String.format("%s %s", this.amount, this.unit);
  }

  public String getUnit() {
    return this.unit;
  }

  public Ingredient getIngredient() {
    return this.ingredient;
  }

  public void setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public void doubleAmount(int ogAmount) {
    this.amount = amount / ogAmount;
    this.amount = amount * (ogAmount + 2);
  }

  public void halfAmount(int ogAmount) {
    this.amount = amount / ogAmount;
    this.amount = amount * (ogAmount - 2);
  }

  public String getName() {
    return this.ingredient.getName();
  }

  public float getQuantity() {
    return this.getAmount();
  }
}

