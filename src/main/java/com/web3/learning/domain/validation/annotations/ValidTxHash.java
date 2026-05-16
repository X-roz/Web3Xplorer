package com.web3.learning.domain.validation.annotations;

import com.web3.learning.domain.validation.EthereumTxHashValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.Valid;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EthereumTxHashValidator.class)
@Documented
public @interface ValidTxHash {

    String message() default  "Invalid Transaction Hash";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
