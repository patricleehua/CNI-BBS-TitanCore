FROM eclipse-temurin:17-jdk-jammy
LABEL authors="patriclee"

WORKDIR /app

COPY /bbs-core/target/bbs-core-2.0.0.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar","app.jar"]


