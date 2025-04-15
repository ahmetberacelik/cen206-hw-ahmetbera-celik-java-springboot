package com.legalcase.caseservice.api.advice;

import com.legalcase.caseservice.application.exception.CaseNotFoundException;
import com.legalcase.caseservice.application.exception.CaseValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the API
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handler for CaseNotFoundException
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCaseNotFoundException(CaseNotFoundException ex) {
        log.error("Case not found exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handler for CaseValidationException
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(CaseValidationException.class)
    public ResponseEntity<ErrorResponse> handleCaseValidationException(CaseValidationException ex) {
        log.error("Case validation exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handler for validation exceptions
     *
     * @param ex the exception
     * @return validation error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handler for IllegalArgumentException
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Generic exception handler
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Basic error response class
     */
    public static class ErrorResponse {
        private final int status;
        private final String message;
        private final LocalDateTime timestamp;
        
        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public int getStatus() {
            return status;
        }
        
        public String getMessage() {
            return message;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * Validation error response with field errors
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> errors;
        
        public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
            super(status, message, timestamp);
            this.errors = errors;
        }
        
        public Map<String, String> getErrors() {
            return errors;
        }
    }
} 