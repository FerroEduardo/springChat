package com.ferroeduardo.springchat.Usuario;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ADMIN_USER("ROLE_USER,ROLE_ADMIN");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getRole().equals(role)) {
                return userRole;
            }
        }
        return null;
    }
}
