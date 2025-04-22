# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# Önce pom.xml'i kopyalayıp bağımlılıkları önbelleğe alarak build süresini kısaltma
RUN mvn dependency:go-offline
# Kaynak kodları kopyala ve derle
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Sadece derlenmiş JAR dosyasını kopyala
COPY --from=build /build/target/*.jar app.jar

# Log ve veri dizinleri için volume tanımla
VOLUME /tmp /logs

# Güvenlik için root olmayan kullanıcı oluştur
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Sağlık kontrolü için health check ekle
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget -q --spider http://localhost:8082/api/actuator/health || exit 1

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"] 