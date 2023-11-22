package ro.ase.costin.ecomback.security;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN("Admin"),
    SALES("Sales"),
    EDITOR("Editor"),
    SHIPPER("Shipper"),
    ASSISTANT("Assistant");

    private final String name;

    UserType(String name) {
        this.name = name;
    }
}
