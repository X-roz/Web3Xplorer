package com.web3.learning.domain;

import com.web3.learning.domain.validation.annotations.ValidChainId;
import com.web3.learning.domain.validation.annotations.ValidSignedTxHex;
import lombok.Data;
import lombok.NonNull;

@Data
public class BroadcastRequest {

    @NonNull
    @ValidChainId
    private Long chainId;

    @NonNull
    @ValidSignedTxHex
    private String signedTxHex;

}
