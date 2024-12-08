package com.myproject.validate;

import com.myproject.validate.anotations.Password;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import static com.myproject.constant.SecurityConstant.PASSWORD_REGEXP;

public class PasswordValidate implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return password != null && password.matches(PASSWORD_REGEXP);
    }
}
