package com.web3.learning.exception;

import lombok.Getter;

@Getter
public class Web3XplorerException extends  RuntimeException {

    private final String code;

    public Web3XplorerException(String code, String message) {
        super(message);
        this.code = code;
    }


}
