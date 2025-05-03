package cookbook.model;

/*
 * Model class for Users.
 */
public class User {
    private String username;
    private String displayName;
    private String password;
    private boolean isAdmin;
    private int id;
    private static int currentUserId;

    public User() {
    }

    public User(String username, String displayName, String password, boolean isAdmin, int id) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public int userId() {
        return this.id;
    }

    public int getUserId() {
        return this.id;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int currentUserId) {
        User.currentUserId = currentUserId;
    }
}