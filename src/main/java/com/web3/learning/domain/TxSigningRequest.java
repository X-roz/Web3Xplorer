package com.web3.learning.domain;

import com.web3.learning.domain.validation.annotations.ValidChainId;
import com.web3.learning.domain.validation.annotations.ValidEthAddress;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;


@Data
public class TxSigningRequest {

    @NonNull
    @ValidChainId
    private Long chainId;

    @NonNull
    @ValidEthAddress
    private String destinationAddress;

    @NonNull
    private Double amount;
}
