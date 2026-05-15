package com.web3.learning.exception;

import com.web3.learning.domain.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Web3XplorerException.class)
    public ResponseEntity<ApiResponse<Object>> handleWeb3Exception(Web3XplorerException web3XplorerException) {
        return ApiResponse.error(web3XplorerException.getCode(),web3XplorerException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ApiResponse.error("ERR_0001", "Validation Failed", errors);
    }

}
