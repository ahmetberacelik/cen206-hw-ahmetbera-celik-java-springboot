FROM maven:3.8.6-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application and skip tests
RUN mvn package -DskipTests

# Create runtime image
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Install MySQL client (for troubleshooting if needed)
RUN apt-get update && apt-get install -y default-mysql-client && apt-get clean

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Environment variables that can be overridden when running the container
ENV DB_URL=jdbc:mysql://mysql:3306/legalcasedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC \
    DB_USER=legalcaseuser \
    DB_PASSWORD=legalcasepass

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]