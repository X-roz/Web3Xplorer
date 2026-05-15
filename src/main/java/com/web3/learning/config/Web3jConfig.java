package com.web3.learning.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    @Value("${ethereum.rpc-url}")
    private String rpcUrl;

    @Value("${privateKey}")
    private String privateKey;

    @Getter
    private Credentials credentials;
    @Getter
    private String walletAddress;

    @PostConstruct
    private void init() {
        credentials = setCredentials();
        walletAddress = credentials.getAddress();
    }

    @Bean
    @Primary
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    private Credentials setCredentials() {
        return Credentials.create(privateKey);
    }
}
