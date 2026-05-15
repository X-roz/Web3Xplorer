package com.web3.learning.domain.validation.annotations;

import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEthAddress {

    String message() default  "Invalid Ethereum Address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[]  payload() default {};
}
