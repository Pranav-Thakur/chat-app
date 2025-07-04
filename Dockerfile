# Use Java 11 base image as Railway uses Java 11 in Singapore region
FROM eclipse-temurin:11-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/app.jar"]