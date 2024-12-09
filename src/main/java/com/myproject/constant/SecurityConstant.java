package com.myproject.constant;

public class SecurityConstant {
    public static final String[] PUBLIC_ENDPOINT = {"/**/register", "/**/login", "/resources/**"};
    public static final String DEFAULT_AVATAR = "http://res.cloudinary.com/dfxkmlyuh/image/upload/v1732306542/iwnptq4p2ji2526fcpv6.jpg";
    public static final String USERNAME_REGEXP = "^[a-zA-Z][a-zA-Z0-9_-]{2,50}$";
    public static final String PASSWORD_REGEXP = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,20}$";
}
