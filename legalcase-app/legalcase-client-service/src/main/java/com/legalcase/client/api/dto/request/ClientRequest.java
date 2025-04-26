package com.legalcase.client.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    
    @NotBlank(message = "İsim alanı boş olamaz")
    @Size(min = 2, max = 50, message = "İsim 2-50 karakter arasında olmalıdır")
    private String name;
    
    @NotBlank(message = "Soyisim alanı boş olamaz")
    @Size(min = 2, max = 50, message = "Soyisim 2-50 karakter arasında olmalıdır")
    private String surname;
    
    @Email(message = "Geçerli bir e-posta adresi girilmelidir")
    private String email;
    
    private String phoneNumber;
    
    @Size(max = 500, message = "Adres en fazla 500 karakter olabilir")
    private String address;
    
    private String taxId;
    
    private String identityNumber;
    
    @Size(max = 1000, message = "Notlar en fazla 1000 karakter olabilir")
    private String notes;
} 