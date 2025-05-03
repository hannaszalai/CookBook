package cookbook.DAOs;

import cookbook.DatabaseConnection;
import cookbook.model.Comment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/*
 * Data Access Object (DAO) for Comments.
 */
public class CommentDao {
    public List<Comment> getAllComments() throws Exception {
        List<Comment> comments = new ArrayList<>();
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT Comments.comment_id, Comments.recipe_id, Comments.user_id, Comments.comment, Comments.created_at, Users.username " +
                         "FROM Comments " +
                         "JOIN Users ON Comments.user_id = Users.user_id";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                int commentId = resultSet.getInt("comment_id");
                int recipeId = resultSet.getInt("recipe_id");
                String commentText = resultSet.getString("comment");
                String username = resultSet.getString("username");
                int parentCommentId = resultSet.getInt("parent_comment_id");
    
                Comment comment = new Comment(commentId, commentText, username, recipeId, parentCommentId);
            comments.add(comment);
            }
        }
    
        return comments;
    }

    // Method to get comments by recipeId
    public List<Comment> getCommentsByRecipeId(int recipeId) throws Exception {
        List<Comment> comments = new ArrayList<>();
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT Comments.comment_id, Comments.recipe_id, Comments.user_id, Comments.comment, Comments.created_at, Users.username, Comments.parent_comment_id " +
             "FROM Comments " +
             "JOIN Users ON Comments.user_id = Users.user_id " +
             "WHERE Comments.recipe_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, recipeId);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                int commentId = resultSet.getInt("comment_id");
                String commentText = resultSet.getString("comment");
                String username = resultSet.getString("username");
                int parentCommentId = resultSet.getInt("parent_comment_id");
    
                Comment comment = new Comment(commentId, commentText, username, recipeId, parentCommentId);
            comments.add(comment);
            }
        }
    
        return comments;
    }

    public int getCommentId(String content) throws Exception {
        int commentId = -1;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT comment_id FROM Comments WHERE comment = ?");
        ) {
            statement.setString(1, content);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    commentId = resultSet.getInt("comment_id");
                }
            }
        }
        return commentId;
    }

    public void removeComment(Comment comment) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM Comments WHERE comment_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, comment.getCommentId());
            statement.executeUpdate();
        }
    }
    
    public void updateComment(Comment comment) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Comments SET comment = ? WHERE comment_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, comment.getContent());
            statement.setInt(2, comment.getCommentId());
            statement.executeUpdate();
        }
    }

    public void addComment(int userId, int recipeId, Integer parentCommentId, String comment) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql;
            PreparedStatement statement;
    
            if (parentCommentId != null) {
                sql = "INSERT INTO Comments (user_id, recipe_id, parent_comment_id, comment) VALUES (?, ?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, recipeId);
                statement.setInt(3, parentCommentId);
                statement.setString(4, comment);
            } else {
                sql = "INSERT INTO Comments (user_id, recipe_id, comment) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, recipeId);
                statement.setString(3, comment);
            }
    
            statement.executeUpdate();
        }
    }   
}