package com.web3.learning.domain.validation;

import com.web3.learning.domain.validation.annotations.ValidEthAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.web3.learning.utils.KeccakHashUtils.keccak256;

public class EthereumAddressValidator implements ConstraintValidator<ValidEthAddress, String> {

    @Override
    public boolean isValid(String address, ConstraintValidatorContext context) {
        if (address == null){
            return false;
        }
        // Regex Validation
        return address.matches("^0x[a-fA-F0-9]{40}$") && additionalValidation(address);
    }

    private boolean additionalValidation(String address) {
        String cleanAddress = address.substring(2);

        // all Uppercase or all lowercase address are valid
        if(cleanAddress.equals(cleanAddress.toLowerCase()) || cleanAddress.equals(cleanAddress.toUpperCase())){
            return true;
        }

        // CHECK SUM VALIDATION:
        // Step 1: Lower Case Conversion
        String lower = cleanAddress.toLowerCase();

        // Step 2: Keccak 256 Hash generation on lowercase address
        String hash = keccak256(lower);

        for (int i=0; i<cleanAddress.length();i++) {

            char addrChar = cleanAddress.charAt(i);

            // digits -> continue
            if(Character.isDigit(addrChar)) {
                continue;
            }

            // Alphabets -> do validation
            // from the hash - take the i Character and convert to int
            int hashValue = Character.digit(hash.charAt(i), 16);

            // check that Hash value is greater than 8 and the addrChar is uppercase continue; otherwise fail it
            if(hashValue >= 8) {
                if(!Character.isUpperCase(addrChar)) {
                    return false;
                }
            } else { // check that Hash value is less than 8 and the addrChar is lowercase continue; otherwise fail it
                if(!Character.isLowerCase(addrChar)) {
                    return false;
                }
            }
        }

        return true;
    }

}
