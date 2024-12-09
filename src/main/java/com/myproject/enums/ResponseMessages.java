package com.myproject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessages {
    SUCCESS(200),
    BAD_REQUEST(400),
    INVALID_VALUE(400_001),
    SAVE_DATABASE_ERROR(400_002),
    LOGIN_FAILED(400_003),
    NOT_FOUND(404),
    ;

    private final int code;
}
