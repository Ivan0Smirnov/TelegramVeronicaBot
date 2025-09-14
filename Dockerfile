FROM maven:3.9.2-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/target/helix-0.0.1-SNAPSHOT.jar app.jar
ENV BOT_TOKEN=${BOT_TOKEN}
CMD ["java", "-jar", "app.jar"]
