package com.myproject.validate.anotations;

import com.myproject.validate.UsernameValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.myproject.constant.ErrorMessage.USERNAME_INVALID;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidate.class)
public @interface Username {
    String message() default USERNAME_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

