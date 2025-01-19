FROM openjdk:17-jdk-alpine
LABEL authors="patriclee"

WORKDIR /app

COPY /bbs-core/target/bbs-core-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar","app.jar"]


