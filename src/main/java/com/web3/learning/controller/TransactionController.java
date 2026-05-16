package com.web3.learning.controller;


import com.web3.learning.domain.BroadcastRequest;
import com.web3.learning.domain.TxSigningRequest;
import com.web3.learning.domain.TxSigningResponse;
import com.web3.learning.domain.api.ApiResponse;
import com.web3.learning.domain.validation.BroadCastResponse;
import com.web3.learning.service.SigningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
