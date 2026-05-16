package com.web3.learning.utils;

import com.web3.learning.config.RpcProvidersConfig;
import com.web3.learning.config.Web3Connectors;
import com.web3.learning.exception.Web3XplorerException;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Web3CallerUtils {

    public static BigInteger getNonce(Long chainId, String address) {
        return caller(chainId, "getNonce", wc -> {
            var response = wc.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getTransactionCount();
        });
    }

    public static BigInteger getMaxPriorityFeePerGas(Long chainId) {
        return caller(chainId, "getMaxPriorityFeePerGas", wc -> {
            var response = wc.ethMaxPriorityFeePerGas().send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getMaxPriorityFeePerGas();
        });
    }

    public static BigInteger getBaseFee(Long chainId) {
        return caller(chainId, "getBaseFee", wc -> {
            var response = wc.ethBaseFee().send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getBaseFee();
        });
    }

    public static String broadcastTransaction(Long chainId, String signedTxHash) {
        return caller(chainId, "broadcastTransaction", wc -> {
            var response = wc.ethSendRawTransaction(signedTxHash).send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getTransactionHash();
        });
    }

    public static Optional<Transaction> getTransaction(Long chainId, String txHash) {
        return caller(chainId, "getTransaction", wc -> {
            var response = wc.ethGetTransactionByHash(txHash).send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getTransaction();
        });
    }

    public static Optional<TransactionReceipt> getTransactionReceipt(Long chainId, String txHash) {
        return caller(chainId, "getTransactionReceipt", wc -> {
            var response = wc.ethGetTransactionReceipt(txHash).send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getTransactionReceipt();
        });
    }

    public static BigInteger getLatestBlock(Long chainId) {
        return caller(chainId, "getLatestBlock", wc -> {
            var response = wc.ethBlockNumber().send();
            if (response.hasError()) throw new RpcErrorException(response.getError().getCode(), response.getError().getMessage());
            return response.getBlockNumber();
        });
    }

    private static <T> T caller(Long chainId, String methodName, Web3JAction<T> action) {
        List<Web3Connectors> connections = RpcProvidersConfig.getChainIdToConnectors().get(chainId);

        if (connections == null || connections.isEmpty()) {
            log.error("service = caller, method: {}, no connections configured for chainId: {}", methodName, chainId);
            throw new Web3XplorerException("WEB3_NO_CONN", "No RPC connections configured for chainId: " + chainId);
        }
        for (Web3Connectors wc : connections) {
            try {
                return action.execute(wc.connection());
            } catch (RpcErrorException e) {
                log.error("service = caller, method: {}, RPC error from {} for chainId {} [code={}]: {}", methodName, wc.url(), chainId, e.getRpcCode(), e.getMessage());
                throw new Web3XplorerException("RPC_ERR_" + e.getRpcCode(), e.getMessage());
            } catch (Exception e) {
                log.error("service = caller, method: {}, connection failed url: {} for chainId {}. Trying next fallback. Error: ", methodName, wc.url(), chainId, e);
            }
        }
        log.error("service = caller, method: {}, all connections failed for chainId: {}", methodName, chainId);
        throw new Web3XplorerException("WEB3_CONN", "All connections failed for chainId: " + chainId);
    }

    private static class RpcErrorException extends RuntimeException {
        private final int rpcCode;

        RpcErrorException(int rpcCode, String message) {
            super(message);
            this.rpcCode = rpcCode;
        }

        int getRpcCode() {
            return rpcCode;
        }
    }

}
