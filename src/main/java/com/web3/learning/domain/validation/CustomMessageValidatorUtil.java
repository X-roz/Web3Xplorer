package com.web3.learning.domain.validation;

import jakarta.validation.ConstraintValidatorContext;

public class CustomMessageValidatorUtil {

    public static boolean validationMessage(ConstraintValidatorContext ctx, String message) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }

}
