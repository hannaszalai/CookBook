package cookbook.DAOs;

import cookbook.DatabaseConnection;
import cookbook.model.Tag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.sql.SQLException;

/*
 * Data Access Object (DAO) class for Tags.
 */
public class TagDao {
  private Connection connection;

  public TagDao() {
      try {
          this.connection = DatabaseConnection.getConnection();
      } catch (Exception e) {
          System.out.println("Error while establishing database connection: " + e.getMessage());
      }
  }

  public Tag[] getAllTags(int recipeId) {
    List<Tag> tags = new ArrayList<>();
    String query = "SELECT Tags.* " +
                    "FROM Tags " +
                    "INNER JOIN RecipeTags ON Tags.tag_id = RecipeTags.tag_id " +
                    "WHERE RecipeTags.recipe_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setInt(1, recipeId);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Tag tag = new Tag();
            tag.setId(resultSet.getInt("tag_id"));
            tag.setName(resultSet.getString("name"));
            tags.add(tag);
        }

    } catch (SQLException e) {
        System.out.println("Error while getting tags for recipe: " + e.getMessage());
    }

    return tags.toArray(new Tag[0]);
    }


    public void addTag(int recipeId, Tag tag) {
        String checkTagExistsSql = "SELECT tag_id FROM Tags WHERE name = ?";
        String addTagSql = "INSERT INTO Tags(name, predefined_tag_flag) VALUES(?, ?)";
        String addTagToRecipeSql = "INSERT INTO RecipeTags(recipe_id, tag_id) VALUES(?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkTagExistsStmt = connection.prepareStatement(checkTagExistsSql)) {

            checkTagExistsStmt.setString(1, tag.getName());
            ResultSet resultSet = checkTagExistsStmt.executeQuery();

            int tagId;
            if (resultSet.next()) {
                tagId = resultSet.getInt("tag_id");
            } else {
                try (PreparedStatement addTagStmt = connection.prepareStatement(addTagSql, Statement.RETURN_GENERATED_KEYS)) {
                    addTagStmt.setString(1, tag.getName());
                    addTagStmt.setBoolean(2, tag.isPredefinedTagFlag());
                    addTagStmt.executeUpdate();

                    try (ResultSet generatedKeys = addTagStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            tagId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Creating tag failed, no ID obtained.");
                        }
                    }
                }
            }

            try (PreparedStatement addTagToRecipeStmt = connection.prepareStatement(addTagToRecipeSql)) {
                addTagToRecipeStmt.setInt(1, recipeId);
                addTagToRecipeStmt.setInt(2, tagId);
                addTagToRecipeStmt.executeUpdate();
            }

        } catch (Exception e) {
            System.out.println("Error while adding tag to recipe: " + e.getMessage());
        }
    }


  // Remove a tag from a recipe
  public void removeTagFromRecipe(int recipeId, String tagName) {
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Get the tag id
        String sql = "SELECT tag_id FROM Tags WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, tagName);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int tagId = resultSet.getInt("tag_id");

            // Delete the tag from the recipe
            sql = "DELETE FROM RecipeTags WHERE recipe_id = ? AND tag_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, recipeId);
            statement.setInt(2, tagId);
            statement.executeUpdate();

            // Delete the tag from the Tags table
            sql = "DELETE FROM Tags WHERE tag_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, tagId);
            statement.executeUpdate();
        }
    } catch (Exception e) {
        System.out.println("Error while removing tag from recipe: " + e.getMessage());
    }
}


  public Tag getTagByName(String name) {
    Tag tag = null;
    try {
      String query = "SELECT * FROM Tags WHERE name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, name);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        tag = new Tag(rs.getInt("tag_id"), rs.getString("name"), rs.getBoolean("predefined_tag_flag"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tag;
}


    public Tag getTagById(int id) {
        Tag tag = null;
        try {
        String query = "SELECT * FROM Tags WHERE tag_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            tag = new Tag(rs.getInt("tag_id"), rs.getString("name"), rs.getBoolean("predefined_tag_flag"));
        }
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return tag;
    }


    public Tag createCustomTag(String name) {
        String query = "INSERT INTO Tags (name, predefined_tag_flag) VALUES (?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Tag(generatedKeys.getInt(1), name, false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public boolean deleteCustomTag(int id) {
        String query = "SELECT predefined_tag_flag FROM Tags WHERE tag_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getBoolean("predefined_tag_flag") == false) {
                    query = "DELETE FROM Tags WHERE tag_id = ?";
                    try (PreparedStatement deleteStmt = connection.prepareStatement(query)) {
                        deleteStmt.setInt(1, id);
                        deleteStmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
