package com.web3.learning.service;

import com.web3.learning.config.CredentialsConfig;
import com.web3.learning.domain.*;
import com.web3.learning.domain.api.ApiResponse;
import com.web3.learning.exception.Web3XplorerException;
import com.web3.learning.utils.Web3CallerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

    private static final String TEST_PRIVATE_KEY = "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";
    private static final Credentials TEST_CREDENTIALS = Credentials.create(TEST_PRIVATE_KEY);
    private static final Long CHAIN_ID = 1L;
    private static final String VALID_ADDRESS = "0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae";
    private static final String VALID_TX_HASH = "0x" + "a".repeat(64);

    @Mock
    private CredentialsConfig credentialsConfig;

    @InjectMocks
    private SigningService signingService;

    // ─── sign() ──────────────────────────────────────────────────────────────

    @Test
    void sign_happyPath_returnsTxSigningResponse() {
        when(credentialsConfig.getWalletAddress()).thenReturn(TEST_CREDENTIALS.getAddress());
        when(credentialsConfig.getCredentials()).thenReturn(TEST_CREDENTIALS);

        TxSigningRequest request = new TxSigningRequest(CHAIN_ID, VALID_ADDRESS, 0.001);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getNonce(eq(CHAIN_ID), eq(TEST_CREDENTIALS.getAddress())))
                 .thenReturn(BigInteger.ONE);
            utils.when(() -> Web3CallerUtils.getMaxPriorityFeePerGas(CHAIN_ID))
                 .thenReturn(BigInteger.valueOf(1_000_000_000L));
            utils.when(() -> Web3CallerUtils.getBaseFee(CHAIN_ID))
                 .thenReturn(BigInteger.valueOf(10_000_000_000L));

            ApiResponse<TxSigningResponse> result = signingService.sign(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("Transaction Signed Successfully");
            assertThat(result.getData().getSignedTxHex()).startsWith("0x");
            assertThat(result.getData().getTxHash()).startsWith("0x");
        }
    }

    @Test
    void sign_web3XplorerExceptionFromUtils_rethrowsPreservingCode() {
        when(credentialsConfig.getWalletAddress()).thenReturn(TEST_CREDENTIALS.getAddress());

        TxSigningRequest request = new TxSigningRequest(CHAIN_ID, VALID_ADDRESS, 0.001);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getNonce(anyLong(), anyString()))
                 .thenThrow(new Web3XplorerException("RPC_ERR_-32000", "insufficient funds"));

            assertThatThrownBy(() -> signingService.sign(request))
                    .isInstanceOf(Web3XplorerException.class)
                    .hasMessage("insufficient funds")
                    .satisfies(e -> assertThat(((Web3XplorerException) e).getCode()).isEqualTo("RPC_ERR_-32000"));
        }
    }

    @Test
    void sign_genericException_wrapsAsSigningError() {
        when(credentialsConfig.getWalletAddress()).thenReturn(TEST_CREDENTIALS.getAddress());
        when(credentialsConfig.getCredentials()).thenReturn(TEST_CREDENTIALS);

        TxSigningRequest request = new TxSigningRequest(CHAIN_ID, VALID_ADDRESS, 0.001);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getNonce(anyLong(), anyString())).thenReturn(BigInteger.ONE);
            utils.when(() -> Web3CallerUtils.getMaxPriorityFeePerGas(anyLong())).thenReturn(BigInteger.valueOf(1_000_000_000L));
            // null baseFee → NPE when service does BigInteger.TWO.multiply(null)
            utils.when(() -> Web3CallerUtils.getBaseFee(anyLong())).thenReturn(null);

            assertThatThrownBy(() -> signingService.sign(request))
                    .isInstanceOf(Web3XplorerException.class)
                    .satisfies(e -> assertThat(((Web3XplorerException) e).getCode()).isEqualTo("SIGNING_ERROR"));
        }
    }

    // ─── broadcast() ─────────────────────────────────────────────────────────

    @Test
    void broadcast_happyPath_returnsPublishedResponse() {
        String signedTxHex = "0xsignedtxhex";
        BroadcastRequest request = new BroadcastRequest(CHAIN_ID, signedTxHex);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.broadcastTransaction(CHAIN_ID, signedTxHex))
                 .thenReturn(VALID_TX_HASH);

            ApiResponse<BroadCastResponse> result = signingService.broadcast(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("Transaction broadcasted Successfully");
            assertThat(result.getData().getTxHash()).isEqualTo(VALID_TX_HASH);
            assertThat(result.getData().getStatus()).isEqualTo("PUBLISHED");
        }
    }

    @Test
    void broadcast_web3XplorerExceptionFromUtils_rethrowsPreservingCode() {
        BroadcastRequest request = new BroadcastRequest(CHAIN_ID, "0xsignedtxhex");

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.broadcastTransaction(anyLong(), anyString()))
                 .thenThrow(new Web3XplorerException("RPC_ERR_-32010", "nonce too low"));

            assertThatThrownBy(() -> signingService.broadcast(request))
                    .isInstanceOf(Web3XplorerException.class)
                    .hasMessage("nonce too low")
                    .satisfies(e -> assertThat(((Web3XplorerException) e).getCode()).isEqualTo("RPC_ERR_-32010"));
        }
    }

    @Test
    void broadcast_genericException_wrapsAsBroadcastFailed() {
        BroadcastRequest request = new BroadcastRequest(CHAIN_ID, "0xsignedtxhex");

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.broadcastTransaction(anyLong(), anyString()))
                 .thenThrow(new RuntimeException("unexpected io error"));

            assertThatThrownBy(() -> signingService.broadcast(request))
                    .isInstanceOf(Web3XplorerException.class)
                    .hasMessage("unexpected io error")
                    .satisfies(e -> assertThat(((Web3XplorerException) e).getCode()).isEqualTo("BROADCAST_FAILED"));
        }
    }

    // ─── transactionEnquiry() ────────────────────────────────────────────────

    @Test
    void transactionEnquiry_txNotFound_returnsUnknown() {
        TxEnquiryRequest request = new TxEnquiryRequest(CHAIN_ID, VALID_TX_HASH);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getTransaction(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.empty());

            ApiResponse<TxEnquiryResponse> result = signingService.transactionEnquiry(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getData().getStatus()).isEqualTo("UNKNOWN");
        }
    }

    @Test
    void transactionEnquiry_txFoundNoReceipt_returnsPending() {
        TxEnquiryRequest request = new TxEnquiryRequest(CHAIN_ID, VALID_TX_HASH);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getTransaction(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.of(mock(Transaction.class)));
            utils.when(() -> Web3CallerUtils.getTransactionReceipt(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.empty());

            ApiResponse<TxEnquiryResponse> result = signingService.transactionEnquiry(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getData().getStatus()).isEqualTo("PENDING");
        }
    }

    @Test
    void transactionEnquiry_txMinedWithStatus0x1_returnsSuccessWithConfirmations() {
        TxEnquiryRequest request = new TxEnquiryRequest(CHAIN_ID, VALID_TX_HASH);

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.getBlockNumber()).thenReturn(BigInteger.valueOf(100L));
        when(receipt.getBlockHash()).thenReturn("0xblockhash");
        when(receipt.getGasUsed()).thenReturn(BigInteger.valueOf(21_000L));
        when(receipt.getCumulativeGasUsed()).thenReturn(BigInteger.valueOf(21_000L));
        when(receipt.getTransactionIndex()).thenReturn(BigInteger.ZERO);
        when(receipt.getStatus()).thenReturn("0x1");

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getTransaction(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.of(mock(Transaction.class)));
            utils.when(() -> Web3CallerUtils.getTransactionReceipt(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.of(receipt));
            utils.when(() -> Web3CallerUtils.getLatestBlock(CHAIN_ID))
                 .thenReturn(BigInteger.valueOf(105L));

            ApiResponse<TxEnquiryResponse> result = signingService.transactionEnquiry(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getData().getStatus()).isEqualTo("SUCCESS");
            assertThat(result.getData().getBlockNumber()).isEqualTo(BigInteger.valueOf(100L));
            // confirmations = latestBlock(105) - blockNumber(100) + 1 = 6
            assertThat(result.getData().getConfirmations()).isEqualTo(BigInteger.valueOf(6L));
        }
    }

    @Test
    void transactionEnquiry_txMinedWithStatus0x0_returnsFailed() {
        TxEnquiryRequest request = new TxEnquiryRequest(CHAIN_ID, VALID_TX_HASH);

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.getBlockNumber()).thenReturn(BigInteger.valueOf(200L));
        when(receipt.getBlockHash()).thenReturn("0xblockhash");
        when(receipt.getGasUsed()).thenReturn(BigInteger.valueOf(21_000L));
        when(receipt.getCumulativeGasUsed()).thenReturn(BigInteger.valueOf(21_000L));
        when(receipt.getTransactionIndex()).thenReturn(BigInteger.ONE);
        when(receipt.getStatus()).thenReturn("0x0");

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getTransaction(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.of(mock(Transaction.class)));
            utils.when(() -> Web3CallerUtils.getTransactionReceipt(CHAIN_ID, VALID_TX_HASH))
                 .thenReturn(Optional.of(receipt));
            utils.when(() -> Web3CallerUtils.getLatestBlock(CHAIN_ID))
                 .thenReturn(BigInteger.valueOf(210L));

            ApiResponse<TxEnquiryResponse> result = signingService.transactionEnquiry(request);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getData().getStatus()).isEqualTo("FAILED");
        }
    }

    @Test
    void transactionEnquiry_web3XplorerExceptionFromUtils_rethrowsPreservingCode() {
        TxEnquiryRequest request = new TxEnquiryRequest(CHAIN_ID, VALID_TX_HASH);

        try (MockedStatic<Web3CallerUtils> utils = Mockito.mockStatic(Web3CallerUtils.class)) {
            utils.when(() -> Web3CallerUtils.getTransaction(anyLong(), anyString()))
                 .thenThrow(new Web3XplorerException("WEB3_CONN", "All connections failed for chainId: 1"));

            assertThatThrownBy(() -> signingService.transactionEnquiry(request))
                    .isInstanceOf(Web3XplorerException.class)
                    .hasMessage("All connections failed for chainId: 1")
                    .satisfies(e -> assertThat(((Web3XplorerException) e).getCode()).isEqualTo("WEB3_CONN"));
        }
    }
}
