FROM maven:3.8.8-eclipse-temurin-17-alpine as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app/backend
# COPY ./target/simple-monero-donation-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY --from=builder /app/target/simple-monero-donation-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/backend/app.jar"]