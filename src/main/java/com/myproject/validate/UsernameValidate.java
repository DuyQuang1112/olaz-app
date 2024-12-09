package com.myproject.validate;

import com.myproject.validate.anotations.Username;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.myproject.constant.SecurityConstant.USERNAME_REGEXP;

public class UsernameValidate implements ConstraintValidator<Username, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return username != null && username.matches(USERNAME_REGEXP);
    }
}

