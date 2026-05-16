package com.web3.learning.domain;

import com.web3.learning.domain.validation.annotations.ValidChainId;
import com.web3.learning.domain.validation.annotations.ValidTxHash;
import lombok.Data;
import lombok.NonNull;

@Data
public class TxEnquiryRequest {

    @NonNull
    @ValidChainId
    private Long chainId;

    @NonNull
    @ValidTxHash
    private String txHash;
}
