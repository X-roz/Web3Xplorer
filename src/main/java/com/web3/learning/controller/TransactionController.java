package com.web3.learning.controller;


import com.web3.learning.domain.TxSigningRequest;
import com.web3.learning.domain.TxSigningResponse;
import com.web3.learning.service.SigningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    public TxSigningResponse signTransaction(@RequestBody TxSigningRequest txSigningRequest) throws IOException {
        log.info("Transaction Signing Request :: {}", txSigningRequest);
        return signingService.sign(txSigningRequest);
    }
}
