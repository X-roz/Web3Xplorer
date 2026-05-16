package com.web3.learning.domain.validation.annotations;


import com.web3.learning.domain.validation.EthereumSignedTxHexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EthereumSignedTxHexValidator.class)
@Documented
public @interface ValidSignedTxHex {

    String message() default "Invalid Signed Transaction Hex";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
