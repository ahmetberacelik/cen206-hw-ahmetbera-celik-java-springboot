package com.ahmet.hasan.yakup.esra.legalcase.utils;

import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private List<String> errorMessages;
    private int errorCode;

    // Başarılı yanıt için factory metodu
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    // Hata yanıtı için factory metodu
    public static <T> ApiResponse<T> error(List<String> errorMessages, int errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errorMessages = errorMessages;
        response.errorCode = errorCode;
        return response;
    }

    // Tek hata mesajı için kolaylık metodu
    public static <T> ApiResponse<T> error(String errorMessage, int errorCode) {
        return error(Collections.singletonList(errorMessage), errorCode);
    }

    // Getter ve setter'lar
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