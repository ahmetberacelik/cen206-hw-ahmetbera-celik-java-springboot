package com.legalcase.caseservice.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for creating new cases
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaseRequest {
    
    @NotBlank(message = "Dava başlığı boş olamaz")
    @Size(min = 3, max = 100, message = "Dava başlığı 3-100 karakter arasında olmalıdır")
    private String title;
    
    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;
    
    @NotNull(message = "Müşteri ID boş olamaz")
    private Long clientId;
    
    private Long assignedUserId;
} 