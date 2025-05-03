package cookbook.model;

/*
 * Model class for Messages.
 */
public class Message {
    private int messageId;
    private String content;
    private Recipe recipe;
    private User sender;
    private User receiver;

    public Message() {
    }

    public Message(String content, Recipe recipe, User sender, User receiver) {
        this.content = content;
        this.recipe = recipe;
        this.sender = sender;
        this.receiver = receiver;
    }

    // Getters and setters for each field
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}