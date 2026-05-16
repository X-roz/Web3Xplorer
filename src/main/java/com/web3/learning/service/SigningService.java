package com.web3.learning.service;

import com.web3.learning.config.CredentialsConfig;
import com.web3.learning.domain.*;
import com.web3.learning.domain.api.ApiResponse;
import com.web3.learning.domain.validation.BroadCastResponse;
import com.web3.learning.exception.Web3XplorerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static com.web3.learning.constants.Constants.ETH_MULTIPLIER;
import static com.web3.learning.utils.KeccakHashUtils.keccak256;
import static com.web3.learning.utils.Web3CallerUtils.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigningService {

    private final CredentialsConfig credentialsConfig;

    public ApiResponse<TxSigningResponse> sign(TxSigningRequest request) {
        log.info("service = sign, status = started processing Signing Request :: {}", request);
        try {
            BigDecimal requestAmount = new BigDecimal(String.valueOf(request.getAmount()));
            BigInteger value = requestAmount.multiply(ETH_MULTIPLIER).toBigInteger();
            Long chainId = request.getChainId();

            BigInteger nonce = getNonce(chainId, credentialsConfig.getWalletAddress());
            BigInteger nativeEthGasLimit = BigInteger.valueOf(21000);
            BigInteger maxPriorityFeePerGas = getMaxPriorityFeePerGas(chainId); // Node-level suggestion based on mempool tip history

            BigInteger baseFee = getBaseFee(chainId); // Network's Mandatory Fee

            // 2x Multiplier: because it gives 6 blocks of runway certainty for our transaction
            BigInteger maxFeePerGas = (BigInteger.TWO.multiply(baseFee)).add(maxPriorityFeePerGas);

            RawTransaction rawTransaction = RawTransaction.createTransaction(request.getChainId(), nonce, nativeEthGasLimit,
                    request.getDestinationAddress(), value, "", maxPriorityFeePerGas, maxFeePerGas);

            byte[] rawEncodeBytes = TransactionEncoder.encode(rawTransaction);
            Sign.SignatureData signatureData = Sign.signMessage(rawEncodeBytes, credentialsConfig.getCredentials().getEcKeyPair());
            byte[] signedTxBytes = TransactionEncoder.encode(rawTransaction, signatureData);

            TxSigningResponse txSigningResponse = new TxSigningResponse();
            txSigningResponse.setSignedTxHex(Numeric.toHexString(signedTxBytes));
            txSigningResponse.setTxHash(keccak256(signedTxBytes));

            log.info("service = sign, status = Signing completed Successfully - {}", request);
            return ApiResponse.success(txSigningResponse, "Transaction Signed Successfully");
        } catch (Exception e) {
            throw new Web3XplorerException("Signing Error", e.getMessage());
        }
    }

    public ApiResponse<BroadCastResponse> broadcast(BroadcastRequest broadcastRequest) {
        log.info("service = broadcast, broadcasting start :: {}", broadcastRequest);
        try{
            String txHash = broadcastTransaction(broadcastRequest.getChainId(), broadcastRequest.getSignedTxHex());
            BroadCastResponse broadCastResponse = new BroadCastResponse();
            broadCastResponse.setTxHash(txHash);
            broadCastResponse.setStatus("PUBLISHED");
            log.info("service = broadcast, Transaction published :: {}", broadCastResponse);
            return ApiResponse.success(broadCastResponse, "Transaction broadcasted Successfully");
        } catch (Exception e) {
            throw new Web3XplorerException("BROADCAST_FAILED",e.getMessage());
        }
    }

    public ApiResponse<TxEnquiryResponse> transactionEnquiry(TxEnquiryRequest txEnquiryRequest) {
        log.info("service = transactionEnquiry, started processing :: {}", txEnquiryRequest);
        try {
            TxEnquiryResponse txEnquiryResponse = new TxEnquiryResponse();
            Optional<Transaction> transaction = getTransaction(txEnquiryRequest.getChainId(), txEnquiryRequest.getTxHash());
            if (transaction.isEmpty()){
                txEnquiryResponse.setStatus("UNKNOWN");
                return ApiResponse.success(txEnquiryResponse, "Transaction Hash is not yet Published");
            }

            Optional<TransactionReceipt> transactionReceipt = getTransactionReceipt(txEnquiryRequest.getChainId(), txEnquiryRequest.getTxHash());
            if (transactionReceipt.isEmpty()) {
                txEnquiryResponse.setStatus("PENDING");
                return ApiResponse.success(txEnquiryResponse, "Transaction Hash waiting in Mempool to get Mined");
            }
            // Transaction Receipt Present : Tx Mined
            TransactionReceipt minedTx = transactionReceipt.get();
            txEnquiryResponse.setBlockNumber(minedTx.getBlockNumber());
            txEnquiryResponse.setBlockHash(minedTx.getBlockHash());
            txEnquiryResponse.setGasUsed(minedTx.getGasUsed());
            txEnquiryResponse.setCumulativeGasUsed(minedTx.getCumulativeGasUsed());
            txEnquiryResponse.setTransactionIndex(minedTx.getTransactionIndex());

            // Check Receipt status:  (0x0 - failed, 0x1-sucess)
            txEnquiryResponse.setStatus(minedTx.getStatus() != null ? ("0x1".equals(minedTx.getStatus()) ? "SUCCESS" : "FAILED") : "MINED");

            // Conformations
            txEnquiryResponse.setConfirmations(BigInteger.ZERO);
            if(txEnquiryResponse.getBlockNumber() != null) {
                BigInteger confirmations = (getLatestBlock(txEnquiryRequest.getChainId()).subtract(txEnquiryResponse.getBlockNumber())).add(BigInteger.ONE);
                txEnquiryResponse.setConfirmations(confirmations);
            }
            log.info("service = transactionEnquiry,  Transaction Enquired :: {}", txEnquiryResponse);
            return ApiResponse.success(txEnquiryResponse, "TX Enquiry Successful");
        } catch (Exception e) {
            throw new Web3XplorerException("TX_ENQUIRY_FAILED", e.getMessage());
        }

    }

}

