package cookbook.model;

/*
 * Model class for Ingredients.
 */
public class Ingredient2 {
    private String name;
    private String quantity;
    private String unit;

    public Ingredient2(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Ingredient2(String name) {
        this.name = name;
      }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
}