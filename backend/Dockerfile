FROM maven:3.9.6-eclipse-temurin-21-alpine as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app/backend
COPY --from=builder /app/target/simple-monero-donation-service-0.5.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/backend/app.jar"]
