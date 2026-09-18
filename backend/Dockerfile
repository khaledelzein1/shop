# --- Build ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Couche dépendances séparée du code source : ne se re-télécharge que si pom.xml change.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/shop-backend-*.jar app.jar
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
