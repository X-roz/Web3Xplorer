package com.web3.learning.domain;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TxEnquiryResponse {

    private String status;
    private BigInteger confirmations;
    private BigInteger transactionIndex;
    private String blockHash;
    private BigInteger blockNumber;
    private BigInteger cumulativeGasUsed;
    private BigInteger gasUsed;
}
