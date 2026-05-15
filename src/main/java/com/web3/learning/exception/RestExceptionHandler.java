package com.web3.learning.exception;

import com.web3.learning.domain.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Web3XplorerException.class)
    public ResponseEntity<ApiResponse<Object>> handleWeb3Exception(Web3XplorerException web3XplorerException) {

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .data(null)
                .message(web3XplorerException.getMessage())
                .code(web3XplorerException.getCode())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
