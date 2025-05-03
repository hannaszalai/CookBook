package cookbook.model;

/*
 * Model class for Comments.
 */
public class Comment {
    private int id;
    private String content;
    private String username;
    private int recipeId;
    private int parentCommentId;

    public Comment(int id, String content, String username, int recipeId, int parentCommentId) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.recipeId = recipeId;
        this.parentCommentId = parentCommentId;
    }

    public Comment(int commentId, int recipeId, String commentText, String username, int parentCommentId) {
        this.id = commentId;
        this.recipeId = recipeId;
        this.content = commentText;
        this.username = username;
        this.parentCommentId = parentCommentId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return this.username + ": " + this.content;
    }

    public int getCommentId() {
        return id;
    }

    public int getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(int parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
