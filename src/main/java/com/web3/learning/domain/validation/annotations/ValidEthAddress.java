package com.web3.learning.domain.validation.annotations;

import com.web3.learning.domain.validation.EthereumAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EthereumAddressValidator.class)
@Documented
public @interface ValidEthAddress {

    String message() default  "Invalid Ethereum Address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[]  payload() default {};
}
