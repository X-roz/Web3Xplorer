package com.web3.learning.domain.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;

    private String code;

    private String message;

    private T data;

    public static <T>ApiResponse<T> success(T data, String message) {

        return ApiResponse.<T>builder()
                .success(true)
                .code("")
                .message(message)
                .data(data)
                .build();
    }

    public static <T>ApiResponse<T> error(String code,  String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

}
