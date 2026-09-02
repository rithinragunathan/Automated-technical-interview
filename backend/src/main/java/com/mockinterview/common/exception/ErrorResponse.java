package com.mockinterview.common.exception;

public record ErrorResponse(String timestamp, int status, String message, String path) {
}
