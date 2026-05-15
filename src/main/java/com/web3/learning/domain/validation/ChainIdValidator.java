package com.web3.learning.domain.validation;

import com.web3.learning.config.Web3jConfig;
import com.web3.learning.domain.validation.annotations.ValidChainId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChainIdValidator implements ConstraintValidator<ValidChainId, Long> {

    private final Web3jConfig web3jConfig;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return web3jConfig.getSupportedChainIds().contains(value);
    }
}
