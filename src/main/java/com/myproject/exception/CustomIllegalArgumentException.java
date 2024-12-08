package com.myproject.exception;

public class CustomIllegalArgumentException extends RuntimeException{
    public CustomIllegalArgumentException(String message) {
        super(message);
    }
}
