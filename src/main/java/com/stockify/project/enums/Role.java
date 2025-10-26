package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_BROKER("ROLE_BROKER"),
    ROLE_USER("ROLE_USER");

    private final String roleName;
}
