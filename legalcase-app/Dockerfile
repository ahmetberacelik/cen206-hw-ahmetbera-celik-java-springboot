# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
# Bağımlılıkları ayrı bir katmanda önbellekte tutarak hızlı build sağlama
RUN mvn dependency:go-offline
# Uygulamayı derleme
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Sadece derlenmiş JAR dosyasını kopyala
COPY --from=build /build/target/*.jar app.jar

# Güvenlik için root olmayan kullanıcı oluştur
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Health check tanımla
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Uygulamayı çalıştır
ENTRYPOINT ["java", "-jar", "app.jar"]