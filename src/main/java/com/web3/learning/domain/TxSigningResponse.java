package com.web3.learning.domain;

import lombok.Data;

@Data
public class TxSigningResponse {

    private String signedTxHex;

    private String txHash;
}
