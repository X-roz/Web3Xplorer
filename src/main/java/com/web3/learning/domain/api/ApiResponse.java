package com.web3.learning.domain.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;

    private String code;

    private String message;

    private T data;

    private T errors;

    public static <T>ApiResponse<T> success(T data, String message) {

        return ApiResponse.<T>builder()
                .success(true)
                .code("")
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseEntity<ApiResponse<Object>> error(String code, String message) {

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .errors(null)
                .code(code)
                .message(message)
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    public static ResponseEntity<ApiResponse<Object>> error(String code, String message, Map<String, String> errors) {

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .errors(errors)
                .code(code)
                .message(message)
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

}
