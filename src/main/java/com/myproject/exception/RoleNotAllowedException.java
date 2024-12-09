package com.myproject.exception;

import org.springframework.security.access.AccessDeniedException;

public class RoleNotAllowedException extends AccessDeniedException {
    public RoleNotAllowedException(String msg) {
        super(msg);
    }
}
