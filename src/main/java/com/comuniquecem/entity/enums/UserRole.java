package com.comuniquecem.entity.enums;

/**
 * Enum para tipos de usuário no sistema
 */
public enum UserRole {
    STUDENT("Estudante"),
    TEACHER("Professor"), 
    ADMIN("Administrador"),
    SUPER_ADMIN("Super Administrador");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
