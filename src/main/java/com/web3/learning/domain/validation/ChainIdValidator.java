package com.web3.learning.domain.validation;

import com.web3.learning.config.RpcProvidersConfig;
import com.web3.learning.domain.validation.annotations.ValidChainId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChainIdValidator implements ConstraintValidator<ValidChainId, Long> {

    private final RpcProvidersConfig rpcProvidersConfig;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return rpcProvidersConfig.getSupportedChainIds().contains(value);
    }
}
