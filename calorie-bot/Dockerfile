# Simple runtime Dockerfile for calorie-bot
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Copy built jar if present; if building via compose, ensure mvn package is done or add a build stage separately
ARG JAR_FILE=target/calorie-bot-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
