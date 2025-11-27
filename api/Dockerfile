# 1. Build stage
FROM eclipse-temurin:21 AS builder

WORKDIR /snippetvault
COPY . .

RUN ./mvnw -DskipTests -pl api -am clean package

# 2. Run stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /snippetvault/api/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
