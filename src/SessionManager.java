public class SessionManager {

    private static String username;
    private static String role;
    private static boolean loggedIn = false;

    public static void login(String user, String userRole) {
        username = user;
        role = userRole;
        loggedIn = true;
    }

    public static void clearSession() {
        username = null;
        role = null;
        loggedIn = false;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }
}
