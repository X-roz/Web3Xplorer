package com.web3.learning.domain.validation;

import lombok.Data;

@Data
public class BroadCastResponse {
    private String txHash;
    private String status;
}
