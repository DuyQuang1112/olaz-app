package com.myproject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER"),
    HOST("HOST"),
    MEMBER("MEMBER");

    private final String value;
}
