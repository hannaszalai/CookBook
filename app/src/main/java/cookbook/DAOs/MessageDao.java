package cookbook.DAOs;

import cookbook.model.Message;
import cookbook.model.User;
import cookbook.model.Recipe;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * Data Access Object (DAO) class for Messages.
 */
public class MessageDao {
    private Connection connection;
    private UserDao userDao;
    private RecipeDao recipeDao;
    private List<Message> messages = new ArrayList<>();
    

    public MessageDao(Connection connection) {
        this.connection = connection;
        this.userDao = new UserDao(connection);
        this.recipeDao = new RecipeDao();
    }

    public void sendMessage(String content, Recipe recipe, User sender, User receiver) {
        String query = "INSERT INTO Messages (content, recipe_id, sender_id, receiver_id) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);
    
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, content);
            preparedStatement.setInt(2, recipe.getId());
            preparedStatement.setInt(3, sender.getUserId());
            preparedStatement.setInt(4, receiver.getUserId());
    
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch(SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Message> getMessagesByReceiver(User receiver) {
        List<Message> messages = new ArrayList<>();
        if (receiver == null) {
            return messages;
        }

        // Select only messages where deleted is false
        String sql = "SELECT * FROM Messages WHERE deleted = 0";
    {

        int receiverId = receiver.getUserId();
        String query = "SELECT * FROM Messages WHERE receiver_id = ? AND deleted = 0";
        System.out.println("Receiver ID: " + receiverId);
        System.out.println("SQL query: " + query);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                    rs.getString("content"),
                    recipeDao.searchRecipeById(rs.getInt("recipe_id")),
                    userDao.getUserById(rs.getInt("sender_id")),
                    receiver
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    }

    public void deleteMessage(Message message) {
        String sql = "UPDATE Messages SET deleted = 1 WHERE id = ?";

        try {
            // Check if auto-commit is enabled
            if (!connection.getAutoCommit()) {
                // If not, enable it
                connection.setAutoCommit(true);
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, message.getMessageId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}