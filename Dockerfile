FROM gradle:8.8-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle ./
COPY gradle ./gradle
COPY order-platform-msa-mcp-server ./order-platform-msa-mcp-server
COPY order-platform-msa-mcp-server/build.cloud.gradle ./order-platform-msa-mcp-server/build.gradle

RUN ./gradlew :order-platform-msa-mcp-server:bootJar -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/order-platform-msa-mcp-server/build/libs/*.jar /app/application.jar

EXPOSE 8099
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/application.jar"]
