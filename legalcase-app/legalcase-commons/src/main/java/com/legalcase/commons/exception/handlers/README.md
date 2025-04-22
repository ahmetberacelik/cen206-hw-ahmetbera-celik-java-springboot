# Ortak İstisna İşleme Mekanizması

Bu modül, LegalCase mikroservis mimarisinde tutarlı hata işleme için ortak bileşenleri içerir. Tüm mikroservislerin aynı hata işleme yaklaşımını ve yanıt formatını kullanmasını sağlar.

## İstisna Sınıfları

Sistem genelinde kullanılan ortak istisna sınıfları:

- `BusinessException`: İş mantığı hatalarının temel sınıfı
- `ResourceNotFoundException`: Kaynak bulunamadığında fırlatılır
- `ValidationException`: Doğrulama hatalarında fırlatılır
- `ServiceUnavailableException`: Servis erişilemez olduğunda fırlatılır
- `AuthorizationException`: Yetkilendirme hatalarında fırlatılır

## Kullanım

### 1. Özel İstisna İşleme

Tüm yeni mikroservislerde, GlobalExceptionHandler'ı kullanan bir RestControllerAdvice'a sahip olun. Özel istisna işleme gerekiyorsa, BaseExceptionHandler'ı genişletin.

```java
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends BaseExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(
            CustomException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
}
```

### 2. İstisna Fırlatma

İş mantığınızda ortak istisna sınıflarını kullanın:

```java
// Kaynak bulunamadığında
throw new ResourceNotFoundException(Entity.class, id);

// Validasyon hatası
throw new ValidationException("field", "validation message");

// Servis erişilemez olduğunda
throw new ServiceUnavailableException("Service Name", e);
```

### 3. Hata Yanıtları

Tüm API'ler için tutarlı hata yanıtı formatı:

- Basit hatalar için `ApiResponse<T>`:
  ```json
  {
    "success": false,
    "message": "Validation failed for field 'email': Invalid email format",
    "data": null,
    "timestamp": "2023-06-15T14:30:45.123Z"
  }
  ```

- Detaylı doğrulama hataları için `ErrorResponse`:
  ```json
  {
    "timestamp": "2023-06-15T14:30:45.123Z",
    "path": "/api/v1/users",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "validationErrors": [
      { "field": "email", "message": "Invalid email format" },
      { "field": "password", "message": "Password must be at least 8 characters" }
    ]
  }
  ```

## Fayda ve Avantajlar

- Tüm servislerde tutarlı hata yanıtları
- Kod tekrarının azaltılması
- Merkezi hata yönetimi ve loglama
- Servisler arası iletişimde daha iyi hata yönetimi 