package com.web3.learning.utils;

import org.web3j.protocol.Web3j;

@FunctionalInterface
public interface Web3JAction<T> {

    T execute(Web3j wc) throws Exception;
}
