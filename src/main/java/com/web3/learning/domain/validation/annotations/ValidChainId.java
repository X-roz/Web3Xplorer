package com.web3.learning.domain.validation.annotations;

import com.web3.learning.domain.validation.ChainIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChainIdValidator.class)
@Documented
public @interface ValidChainId {

    String message() default "Unsupported Chain Id";

    Class<?>[] groups() default  {};

    Class<? extends Payload>[] payload() default {};

}
