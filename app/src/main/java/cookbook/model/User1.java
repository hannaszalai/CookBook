package cookbook.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * Model class for AdminPanel, Users.
 */
public class User1 {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private SimpleBooleanProperty isAdmin = new SimpleBooleanProperty();

    public User1(String id, String name, String username, String password, boolean isAdmin) {
        setId(id);
        setName(name);
        setUsername(username);
        setPassword(password);
        setAdmin(isAdmin);
    }

    // Property getters
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public SimpleBooleanProperty isAdminProperty() { return isAdmin; }

    // Regular getters
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getUsername() { return username.get(); }
    public String getPassword() { return password.get(); }
    public boolean isAdmin() { return isAdmin.get(); }

    // Regular setters
    public void setId(String id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setUsername(String username) { this.username.set(username); }
    public void setPassword(String password) { this.password.set(password); }
    public void setAdmin(boolean isAdmin) { this.isAdmin.set(isAdmin); }

    @Override
    public String toString() {
        return "User{" +
               "id=" + getId() +
               ", name='" + getName() + '\'' +
               ", username='" + getUsername() + '\'' +
               ", password='" + getPassword() + '\'' +
               ", isAdmin=" + isAdmin() +
               '}';
    }
}
