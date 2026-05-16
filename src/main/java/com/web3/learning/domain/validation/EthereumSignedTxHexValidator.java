package com.web3.learning.domain.validation;

import com.web3.learning.domain.validation.annotations.ValidSignedTxHex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.utils.Numeric;

public class EthereumSignedTxHexValidator implements ConstraintValidator<ValidSignedTxHex, String> {

    @Override
    public boolean isValid(String signedTxHex, ConstraintValidatorContext ctx) {

        if (signedTxHex == null || signedTxHex.isBlank()) {
           return setCustomMessage(ctx, "Transaction hex cannot be null or empty");
        }

        String cleanHex = Numeric.cleanHexPrefix(signedTxHex);

        if (cleanHex.length() % 2 != 0 ||  !cleanHex.matches("^[a-fA-F0-9]+$")) {
            return setCustomMessage(ctx, "Transaction hex contains invalid non-hexadecimal characters or length must be even number");
        }

        try {
            byte[] txBytes = Numeric.hexStringToByteArray(cleanHex);

            SignedRawTransaction signedRawTransaction = (SignedRawTransaction) TransactionDecoder.decode(cleanHex);

            if(signedRawTransaction.getSignatureData() == null) {
                return setCustomMessage(ctx, "Missing Signature Payload in the provided hex");
            }

            byte[] r = signedRawTransaction.getSignatureData().getR();
            byte[] s = signedRawTransaction.getSignatureData().getS();
            if(r==null || r.length == 0 || s==null || s.length == 0) {
                return setCustomMessage(ctx, "Cryptographic validation failed: R or S data is not found");
            }

            String signerAddress = signedRawTransaction.getFrom();
            if(signerAddress == null) {
                return setCustomMessage(ctx, "Signer address could not be recovered");
            }

        } catch (ClassCastException classCastException) {
            return setCustomMessage(ctx, "Transaction structure mismatch: provided hex belongs to unsigned transaction");
        }
        catch (Exception e) {
            return setCustomMessage(ctx, "Invalid transaction hex");
        }

        return true;
    }

    private boolean setCustomMessage(ConstraintValidatorContext ctx,String message) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }

}
