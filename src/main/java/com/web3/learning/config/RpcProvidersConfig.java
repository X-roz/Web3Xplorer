package com.web3.learning.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.util.*;

@Log4j2
@Component
@ConfigurationProperties("rpc.providers")
@Data
public class RpcProvidersConfig {

    private Map<Long, List<String>> chainIdToRpcUrls;

    @Getter
    private final List<Long> supportedChainIds = new ArrayList<>();

    @Getter
    private final static Map<Long, List<Web3Connectors>> chainIdToConnectors = new HashMap<>() {};

    @PostConstruct
    public void init() {
        try {
            chainIdToRpcUrls.forEach((chainId, rpcList) -> {
                supportedChainIds.add(chainId);
                List<Web3Connectors> connections = new ArrayList<>();
                rpcList.forEach(r -> {
                    Web3j con = rpcConnection(r);
                    connections.add(new Web3Connectors(r, con));
                });
                chainIdToConnectors.put(chainId, connections);
            });
        } catch (Exception e) {
            log.error("service = ChainConfig, error = {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Web3j rpcConnection(String url) {
        return Web3j.build(new HttpService(url));
    }

}
