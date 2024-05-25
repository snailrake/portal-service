package ru.intership.portalservice.config.context;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserContext {

    private final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<String> userNameHolder = new ThreadLocal<>();
    private final ThreadLocal<Set<String>> userRolesHolder = new ThreadLocal<>();

    public void setUserRoles(Set<String> userRoles) {
        userRolesHolder.set(userRoles);
    }

    public void setUserId(String userId) {
        userIdHolder.set(userId);
    }

    public void setUserName(String userName) {
        userNameHolder.set(userName);
    }

    public String getUserId() {
        return userIdHolder.get();
    }

    public String getUserName() {
        return userNameHolder.get();
    }

    public Set<String> getUserRoles() {
        return userRolesHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
        userNameHolder.remove();
        userRolesHolder.remove();
    }
}
