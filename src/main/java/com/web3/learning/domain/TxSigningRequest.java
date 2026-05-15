package com.web3.learning.domain;

import lombok.Data;
import lombok.NonNull;


@Data
public class TxSigningRequest {

    @NonNull
    private Long chainId;

    @NonNull
    private String destinationAddress;

    @NonNull
    private Double amount;
}
