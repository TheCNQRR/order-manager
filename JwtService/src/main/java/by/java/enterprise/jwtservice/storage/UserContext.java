package by.java.enterprise.jwtservice.storage;

import java.util.UUID;

public class UserContext {

    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> userRoleHolder = new ThreadLocal<>();

    public static UUID getUserId() {
        return userIdHolder.get();
    }

    public static void setUserId(UUID userId) {
        userIdHolder.set(userId);
    }

    public static String getUserRole() {
        return userRoleHolder.get();
    }

    public static void setUserRole(String userRole) {
        userRoleHolder.set(userRole);
    }

    public static void clear() {
        userIdHolder.remove();
        userRoleHolder.remove();
    }
}
