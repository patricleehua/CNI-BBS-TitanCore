#FROM openjdk:17-jdk-alpine
FROM openjdk:17-jdk-slim
LABEL authors="patriclee"

WORKDIR /app

COPY /bbs-core/target/bbs-core-1.0.0-SNAPSHOT.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar","app.jar"]


