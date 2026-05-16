package com.web3.learning.domain.validation;

import com.web3.learning.domain.validation.annotations.ValidTxHash;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.web3j.utils.Numeric;

import static com.web3.learning.domain.validation.CustomMessageValidatorUtil.validationMessage;

public class EthereumTxHashValidator implements ConstraintValidator<ValidTxHash, String> {

    @Override
    public boolean isValid(String txHash, ConstraintValidatorContext ctx) {

        if(txHash == null || txHash.isBlank()) {
            return validationMessage(ctx, "Transaction hash should not be empty");
        }

        String cleanHash = Numeric.cleanHexPrefix(txHash);

        if(cleanHash.length() != 64) {
            return validationMessage(ctx, "Transaction hash length must be 64");
        }

        if(!cleanHash.matches("^[a-fA-F0-9]{64}$")) {
            return validationMessage(ctx, "Invalid Transaction hash");
        }

        return true;
    }
}
