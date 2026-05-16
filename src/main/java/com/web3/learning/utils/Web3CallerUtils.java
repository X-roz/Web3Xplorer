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
        return caller(chainId, "getNonce",
                wc -> wc.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount());
    }

    public static BigInteger getMaxPriorityFeePerGas(Long chainId) {
        return caller(chainId, "getMaxPriorityFeePerGas",
                wc -> wc.ethMaxPriorityFeePerGas().send().getMaxPriorityFeePerGas());
    }

    public static BigInteger getBaseFee(Long chainId) {
        return caller(chainId, "getBaseFee", wc -> wc.ethBaseFee().send().getBaseFee());
    }

    public static String broadcastTransaction(Long chainId, String signedTxHash) {
        return caller(chainId, "broadcastTransaction",
                wc -> wc.ethSendRawTransaction(signedTxHash).send().getTransactionHash());
    }

    public static Optional<Transaction> getTransaction(Long chainId, String txHash) {
        return caller(chainId, "getTransaction",
                wc -> wc.ethGetTransactionByHash(txHash).send().getTransaction());
    }

    public static Optional<TransactionReceipt> getTransactionReceipt(Long chainId, String txHash) {
        return caller(chainId, "getTransactionReceipt",
                wc -> wc.ethGetTransactionReceipt(txHash).send().getTransactionReceipt());
    }

    public static BigInteger getLatestBlock(Long chainId) {
        return caller(chainId, "getLatestBlock", wc -> wc.ethBlockNumber().send().getBlockNumber());
    }

    private static  <T> T caller(Long chainId, String methodName, Web3JAction<T> action) {
        List<Web3Connectors> connections = RpcProvidersConfig.getChainIdToConnectors().get(chainId);

        if(connections == null || connections.isEmpty()){
            log.error("service = caller, method: {}, no connections to execute the action", methodName);
            return null;
        }
        for (Web3Connectors wc : connections) {
            try {
                return action.execute(wc.connection());
            } catch (Exception e) {
                log.error("service = caller, method: {}, Connection failed url: {} for chainId {}. Trying next fallback. Error: ", methodName, wc.url(), chainId, e);
            }
        }
        log.error("service = caller, method: {}, All connections failed for chainId : {}", methodName, chainId);
        throw new Web3XplorerException("WEB3_CONN", "All connections failed for chainId :" + chainId);
    }

}
