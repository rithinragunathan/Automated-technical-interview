package com.mockinterview.common.response;

public record ApiResponse<T>(boolean success, String message, T data, String timestamp) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, java.time.Instant.now().toString());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, java.time.Instant.now().toString());
    }
}
