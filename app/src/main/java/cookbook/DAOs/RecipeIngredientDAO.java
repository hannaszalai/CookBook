package cookbook.DAOs;

import cookbook.model.RecipeIngredient;
import cookbook.DatabaseConnection;
import cookbook.model.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

/*
 * Data Access Object (DAO) for Recipe Ingredients.
 */
public class RecipeIngredientDAO {
  private Connection connection;

  public RecipeIngredientDAO() {
    try {
      this.connection = DatabaseConnection.getConnection();
  } catch (Exception e) {
      System.out.println("Error while establishing database connection: " + e.getMessage());
  }
  }

  public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
    List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    String query = "SELECT i.ingredient_id, i.name, ri.quantity, ri.unit " +
                   "FROM Ingredients i " +
                   "JOIN RecipeIngredients ri ON i.ingredient_id = ri.ingredient_id " +
                   "WHERE ri.recipe_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setInt(1, recipeId);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(resultSet.getInt("ingredient_id"));
            ingredient.setName(resultSet.getString("name"));

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setAmount(resultSet.getFloat("quantity"));
            recipeIngredient.setUnit(resultSet.getString("unit"));

            recipeIngredients.add(recipeIngredient);
        }
      } catch (SQLException e) {
        System.out.println("Error while getting ingredients for recipe: " + e.getMessage());
    }
    return recipeIngredients;
  }

    public Ingredient findIngredientByName(String name) {
    Ingredient result = null;
    String query = "SELECT i.ingredient_id, i.name, ri.quantity, ri.unit " +
                   "FROM Ingredients i " +
                   "JOIN RecipeIngredients ri ON i.ingredient_id = ri.ingredient_id " +
                   "WHERE i.name = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
            result = new Ingredient();
            result.setId(resultSet.getInt("ingredient_id"));
            result.setName(resultSet.getString("name"));
        }
        
    } catch (Exception e) {
        System.out.println("---\n" + e.getMessage());
        e.printStackTrace();
    }

    return result;
    }

    public Ingredient findIngredientById(Long id) {
        Ingredient result = null;
        String query = "SELECT i.ingredient_id, i.name, ri.quantity, ri.unit " +
                        "FROM Ingredients i " +
                        "JOIN RecipeIngredients ri ON i.ingredient_id = ri.ingredient_id " +
                        "WHERE i.ingredient_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                result = new Ingredient();
                result.setId(resultSet.getInt("ingredient_id"));
                result.setName(resultSet.getString("name"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}