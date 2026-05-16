package com.web3.learning.controller;


import com.web3.learning.domain.*;
import com.web3.learning.domain.api.ApiResponse;
import com.web3.learning.domain.validation.BroadCastResponse;
import com.web3.learning.domain.validation.annotations.ValidTxHash;
import com.web3.learning.service.SigningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final SigningService signingService;

    @PostMapping("/sign")
    public ResponseEntity<ApiResponse<TxSigningResponse>> signTransaction(@Valid @RequestBody TxSigningRequest txSigningRequest) throws IOException {
        return ResponseEntity.ok(signingService.sign(txSigningRequest));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<ApiResponse<BroadCastResponse>> broadcastTransaction(@Valid @RequestBody BroadcastRequest broadcastRequest) throws IOException {
        return ResponseEntity.ok(signingService.broadcast(broadcastRequest));
    }

    @PostMapping("/{txHash}")
    public ResponseEntity<ApiResponse<TxEnquiryResponse>> transactionStatus(@Valid @RequestBody TxEnquiryRequest txEnquiryRequest) throws IOException {
        return ResponseEntity.ok(signingService.transactionEnquiry(txEnquiryRequest));
    }

}
