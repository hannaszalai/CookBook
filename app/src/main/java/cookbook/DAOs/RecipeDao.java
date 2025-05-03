package cookbook.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cookbook.DatabaseConnection;
import cookbook.model.Recipe;

/*
 * Data Access Object (DAO) class for Recipes.
 */
public class RecipeDao {
  public Recipe[] showAllRecipes() {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection()) {
        String sql = "SELECT * FROM Recipes";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Recipe recipe = new Recipe();
            recipe.setId(resultSet.getInt("recipe_id"));
            recipe.setName(resultSet.getString("name"));
            recipe.setSummary(resultSet.getString("short_description"));
            recipe.setDescription(resultSet.getString("detailed_description"));
            recipes.add(recipe);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return recipes.toArray(new Recipe[0]);
  }
  

  public Recipe[] searchRecipesByName(String name) {
    List<Recipe> recipes = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection()) {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Recipes WHERE name LIKE ?");
        statement.setString(1, "%" + name + "%");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Recipe recipe = new Recipe();
            recipe.setId(resultSet.getInt("recipe_id"));
            recipe.setName(resultSet.getString("name"));
            recipe.setSummary(resultSet.getString("short_description"));
            recipe.setDescription(resultSet.getString("detailed_description"));
            recipes.add(recipe);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return recipes.toArray(new Recipe[0]);
  }


  
  public Recipe[] searchRecipesByTagName(String tags) {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Split the input string into individual tags
        String[] tagArray = tags.split(",\\s*");
        
        // Create a string of question marks for the IN clause
        String inClause = String.join(", ", Collections.nCopies(tagArray.length, "?"));
        String sql = "SELECT r.*, COUNT(DISTINCT t.name) as tag_count " +
                     "FROM Recipes r " +
                     "JOIN RecipeTags rt ON r.recipe_id = rt.recipe_id " +
                     "JOIN Tags t ON rt.tag_id = t.tag_id " +
                     "WHERE t.name IN (" + inClause + ") " +
                     "GROUP BY r.recipe_id " +
                     "HAVING tag_count = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set each tag as a separate parameter
        for (int i = 0; i < tagArray.length; i++) {
            statement.setString(i + 1, tagArray[i]);
        }
        statement.setInt(tagArray.length + 1, tagArray.length);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Recipe recipe = new Recipe();
            recipe.setId(resultSet.getInt("recipe_id"));
            recipe.setName(resultSet.getString("name"));
            recipe.setSummary(resultSet.getString("short_description"));
            recipe.setDescription(resultSet.getString("detailed_description"));
            recipes.add(recipe);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return recipes.toArray(new Recipe[0]);
}



  public Recipe[] searchRecipesByIngredientName(String ingredients) {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Split the input string into individual ingredients
        String[] ingredientArray = ingredients.split(",\\s*");
        
        // Create a string of question marks for the IN clause
        String inClause = String.join(", ", Collections.nCopies(ingredientArray.length, "?"));
        String sql = "SELECT r.*, COUNT(DISTINCT i.name) as ingredient_count " +
                     "FROM Recipes r " +
                     "JOIN RecipeIngredients ri ON r.recipe_id = ri.recipe_id " +
                     "JOIN Ingredients i ON ri.ingredient_id = i.ingredient_id " +
                     "WHERE i.name IN (" + inClause + ") " +
                     "GROUP BY r.recipe_id " +
                     "HAVING ingredient_count = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set each ingredient as a separate parameter
        for (int i = 0; i < ingredientArray.length; i++) {
            statement.setString(i + 1, ingredientArray[i]);
        }
        statement.setInt(ingredientArray.length + 1, ingredientArray.length);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Recipe recipe = new Recipe();
            recipe.setId(resultSet.getInt("recipe_id"));
            recipe.setName(resultSet.getString("name"));
            recipe.setSummary(resultSet.getString("short_description"));
            recipe.setDescription(resultSet.getString("detailed_description"));
            recipes.add(recipe);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return recipes.toArray(new Recipe[0]);
    }

  public Recipe searchRecipeById(int id) {
    Recipe recipe = null;
    try (Connection connection = DatabaseConnection.getConnection()) {
        String sql = "SELECT * FROM Recipes WHERE recipe_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            recipe = new Recipe();
            recipe.setId(resultSet.getInt("recipe_id"));
            recipe.setName(resultSet.getString("name"));
            recipe.setSummary(resultSet.getString("short_description"));
            recipe.setDescription(resultSet.getString("detailed_description"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return recipe;
  }

    // Method to get a Recipe object by its ID
    public Recipe getRecipeById(int id) {
        Recipe recipe = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Recipes WHERE recipe_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Create a new Recipe object using the result
                recipe = new Recipe();
                recipe.setId(resultSet.getInt("recipe_id"));
                recipe.setName(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }
}
