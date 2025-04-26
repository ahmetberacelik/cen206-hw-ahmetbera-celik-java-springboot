# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Önce parent POM'u kopyala ve yükle
COPY pom.xml ./
RUN mvn -B install -N

# Commons modülünü kopyala ve derle
COPY legalcase-commons/pom.xml ./legalcase-commons/
COPY legalcase-commons/src ./legalcase-commons/src/
RUN mvn -B clean install -f legalcase-commons/pom.xml -DskipTests

# Client service POM'unu kopyala
COPY legalcase-client-service/pom.xml ./legalcase-client-service/

# Client service kaynak kodlarını kopyala
COPY legalcase-client-service/src ./legalcase-client-service/src/

# Client servis modülünü derle
WORKDIR /build/legalcase-client-service
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Sadece derlenmiş JAR dosyasını kopyala
COPY --from=build /build/legalcase-client-service/target/*.jar app.jar

# Log ve veri dizinleri için volume tanımla
VOLUME /tmp /logs

# Güvenlik için root olmayan kullanıcı oluştur
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Sağlık kontrolü için health check ekle
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget -q --spider http://localhost:8083/api/actuator/health || exit 1

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"] 