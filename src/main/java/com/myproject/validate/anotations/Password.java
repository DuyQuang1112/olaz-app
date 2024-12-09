package com.myproject.validate.anotations;

import com.myproject.validate.PasswordValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.myproject.constant.ErrorMessage.PASSWORD_INVALID;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidate.class)
public @interface Password {
    String message() default PASSWORD_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

