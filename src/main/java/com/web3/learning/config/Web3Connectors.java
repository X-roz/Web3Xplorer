package com.web3.learning.config;

import org.web3j.protocol.Web3j;

public record Web3Connectors(String url, Web3j connection) {}
