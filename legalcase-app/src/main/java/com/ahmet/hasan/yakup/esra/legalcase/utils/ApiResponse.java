package com.ahmet.hasan.yakup.esra.legalcase.utils;

import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private List<String> errorMessages;
    private int errorCode;

    // Factory method for successful response
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    // Factory method for error response
    public static <T> ApiResponse<T> error(List<String> errorMessages, int errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errorMessages = errorMessages;
        response.errorCode = errorCode;
        return response;
    }

    // Convenience method for single error message
    public static <T> ApiResponse<T> error(String errorMessage, int errorCode) {
        return error(Collections.singletonList(errorMessage), errorCode);
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public int getErrorCode() {
        return errorCode;
    }
}