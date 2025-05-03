package cookbook;

import cookbook.model.User;


/*
 * Class for passing the User credentials between classes.
 */
public class UserSession {
      private static UserSession instance;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstance(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    public void loginUser(User user) {
      UserSession.getInstance(user);
    }

    public User getUser() {
        return user;
    }

    public void cleanUserSession() {
        user = null;
        instance = null;
    }

    public static void resetInstance() {
        instance = null;
    }

    public static boolean isUserSet() {
        return instance != null && instance.user != null;
    }
}
