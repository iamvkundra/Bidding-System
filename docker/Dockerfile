# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew ./

# Copy source code
COPY src src

# Build the application
RUN gradle build --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built artifact from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set logging pattern
ENV LOGGING_PATTERN_CONSOLE="%d{yyyy-MM-dd HH:mm:ss} - %msg%n" \
    LOGGING_PATTERN_FILE="%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

# Command to run the application
ENTRYPOINT ["java", \
            "-jar", \
            "-Dspring.profiles.active=prod", \
            "-Dserver.port=8080", \
            "-Dmanagement.endpoints.web.exposure.include=health,prometheus,metrics", \
            "app.jar"]