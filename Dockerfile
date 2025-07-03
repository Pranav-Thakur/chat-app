# Use Java 11 base image as Render uses Java 11 in Singapore region
# ---------- Stage 1: Build ----------
FROM maven:3.8.5-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy all project files
COPY . .

# Build the Spring Boot jar
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:11-jdk-alpine

WORKDIR /app

# Copy built jar from stage 1
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
RUN echo "Active profile: $SPRING_PROFILES_ACTIVE"
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
