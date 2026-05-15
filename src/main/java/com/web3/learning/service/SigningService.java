package com.web3.learning.service;

import com.web3.learning.config.Web3jConfig;
import com.web3.learning.domain.TxSigningRequest;
import com.web3.learning.domain.TxSigningResponse;
import com.web3.learning.domain.api.ApiResponse;
import com.web3.learning.exception.Web3XplorerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.web3.learning.constants.Constants.ETH_MULTIPLIER;
import static com.web3.learning.utils.KeccakHashUtils.bytesToHex;
import static com.web3.learning.utils.KeccakHashUtils.keccak256;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigningService {

    private final Web3j web3j;
    private final Web3jConfig web3jConfig;

    public ApiResponse<TxSigningResponse> sign(TxSigningRequest request) throws IOException {

        try {
            BigDecimal requestAmount = new BigDecimal(String.valueOf(request.getAmount()));
            BigInteger value = requestAmount.multiply(ETH_MULTIPLIER).toBigInteger();

            BigInteger nonce = web3j.ethGetTransactionCount(web3jConfig.getWalletAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
            BigInteger nativeEthGasLimit = BigInteger.valueOf(21000);
            BigInteger maxPriorityFeePerGas = web3j.ethMaxPriorityFeePerGas().send().getMaxPriorityFeePerGas(); // Node-level suggestion based on mempool tip history

            BigInteger baseFee = web3j.ethBaseFee().send().getBaseFee(); // Network's Mandatory Fee

            // 2x Multiplier: because it gives 6 blocks of runway certainty for our transaction
            BigInteger maxFeePerGas = (BigInteger.TWO.multiply(baseFee)).add(maxPriorityFeePerGas);

            RawTransaction rawTransaction = RawTransaction.createTransaction(request.getChainId(), nonce, nativeEthGasLimit,
                    request.getDestinationAddress(), value, "", maxPriorityFeePerGas, maxFeePerGas);

            byte[] rawEncodeBytes = TransactionEncoder.encode(rawTransaction);
            Sign.SignatureData signatureData = Sign.signMessage(rawEncodeBytes, web3jConfig.getCredentials().getEcKeyPair());
            byte[] signedTxBytes = TransactionEncoder.encode(rawTransaction, signatureData);

            TxSigningResponse txSigningResponse = new TxSigningResponse();
            txSigningResponse.setSignedTxHex(Numeric.toHexString(signedTxBytes));
            txSigningResponse.setTxHash(keccak256(signedTxBytes));

            return ApiResponse.success(txSigningResponse, "Transaction Signed Successfully");
        } catch (Exception e) {
            throw new Web3XplorerException("Signing Error", e.getMessage());
        }
    }

}

