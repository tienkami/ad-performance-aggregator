# ========================================================
# Stage 1: Build Environment Setup
# ========================================================
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app

# Cache dependency configuration footprints to speed up subsequent layer compilations
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source matrices and compile isolated artifact targets
COPY src ./src
RUN mvn clean package -DskipTests

# ========================================================
# Stage 2: Production Image Footing
# ========================================================
FROM openjdk:8-jre-slim
WORKDIR /app

# Import compiled executable assembly distribution binary
COPY --from=builder /app/target/ad-performance-aggregator-1.0-SNAPSHOT.jar app.jar

# Define default execution wrapper constraints mapping input hooks
ENTRYPOINT ["java", "-jar", "app.jar"]