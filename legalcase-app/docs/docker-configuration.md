# Docker Yapılandırması

## Çok Aşamalı (Multistage) Yapı

Bu projede tüm servisler için çok aşamalı (multistage) Docker yapılandırması kullanılmaktadır. Bu yaklaşımın avantajları:

1. **Daha küçük imaj boyutu**: Sadece çalışma zamanı için gereken dosyalar son imaja dahil edilir.
2. **Gelişmiş güvenlik**: Derleme araçları ve bağımlılıkları son imajda bulunmaz, bu da güvenlik yüzeyini azaltır.
3. **Daha hızlı dağıtım**: Küçük imajlar daha hızlı indirilir ve dağıtılır.
4. **Daha temiz yapı**: Derleme ve çalışma zamanı kaygıları birbirinden ayrılmıştır.

## Dockerfile Yapısı

Her servis için Dockerfile iki aşamalıdır:

### 1. Build Aşaması

```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# Bağımlılıkları önbellekleme
RUN mvn dependency:go-offline
# Kaynak kodları kopyalama ve derleme
COPY src ./src
RUN mvn clean package -DskipTests
```

### 2. Runtime Aşaması

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Sadece JAR dosyasını kopyalama
COPY --from=build /build/target/*.jar app.jar

# Güvenlik ve performans iyileştirmeleri
VOLUME /tmp /logs
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s CMD wget -q --spider http://localhost:PORT/actuator/health

EXPOSE PORT
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Güvenlik İyileştirmeleri

1. **Root olmayan kullanıcı**: Tüm servisler, sınırlı yetkilere sahip özel bir kullanıcı tarafından çalıştırılır.
2. **Minimal temel imaj**: Alpine tabanlı JRE imajı, tam JDK yerine kullanılarak güvenlik yüzeyi azaltılır.
3. **Gereksiz araçların olmaması**: Derleme araçları ve bağımlılıkları nihai imajda yer almaz.

## Performans İyileştirmeleri

1. **Bağımlılık önbellekleme**: Maven bağımlılıkları, Docker katmanları arasında önbelleğe alınır, bu da tekrarlanan yapılandırmalarda build süresini azaltır.
2. **Volume kullanımı**: Geçici ve log dosyaları için volume kullanımı, container performansını artırır.
3. **Health check**: Servis sağlığını izlemek için entegre health check mekanizması.

## İmajları Oluşturma

Her servis için Docker imajını oluşturmak için:

```bash
cd legalcase-app/[service-name]
docker build -t legalcase-[service-name]:latest .
```

## Docker Compose ile Çalıştırma

Tüm servisleri Docker Compose ile başlatmak için:

```bash
cd legalcase-app
docker-compose up -d
``` 