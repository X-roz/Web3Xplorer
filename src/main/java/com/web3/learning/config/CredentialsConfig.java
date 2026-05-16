package com.web3.learning.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;

@Component
public class CredentialsConfig {

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

    private Credentials setCredentials() {
        return Credentials.create(privateKey);
    }
}
