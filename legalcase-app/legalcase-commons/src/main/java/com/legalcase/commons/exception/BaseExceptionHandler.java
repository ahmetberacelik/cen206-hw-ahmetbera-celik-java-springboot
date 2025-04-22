package com.legalcase.commons.exception;

import com.legalcase.commons.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Base exception handler that can be extended by services to provide service-specific error handling
 * while still leveraging common exception handling functionality
 */
@Slf4j
public abstract class BaseExceptionHandler {

    /**
     * Create a error response for a specific exception
     */
    protected <T> ResponseEntity<ApiResponse<T>> createErrorResponse(
            Exception ex, HttpStatus status, String message, WebRequest request) {
        
        String path = getPath(request);
        log.error("Error occurred at {}: {}", path, message, ex);
        
        ApiResponse<T> response = ApiResponse.error(message != null ? message : ex.getMessage());
        return new ResponseEntity<>(response, status);
    }
    
    /**
     * Create a detailed error response
     */
    protected ResponseEntity<ErrorResponse> createDetailedErrorResponse(
            Exception ex, HttpStatus status, String message, WebRequest request) {
        
        String path = getPath(request);
        log.error("Error occurred at {}: {}", path, message, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message != null ? message : ex.getMessage())
                .path(path)
                .build();
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    /**
     * Get the request path
     */
    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getNativeRequest(HttpServletRequest.class);
            if (httpRequest != null) {
                return httpRequest.getRequestURI();
            }
        }
        return "unknown";
    }
} 