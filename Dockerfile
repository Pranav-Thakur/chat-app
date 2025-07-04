# Use Java 11 base image as Railway uses Java 11 in Singapore region
# Dockerfile (Spring Boot 2.7 + Java 11 + Maven)
FROM maven:3.8.5-openjdk-11 AS builder

WORKDIR /app

COPY . .

# Build your app
RUN mvn clean package -DskipTests

# Use a slim JRE for running the app
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the jar
COPY --from=builder /app/target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
