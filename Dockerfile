FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY backend/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]