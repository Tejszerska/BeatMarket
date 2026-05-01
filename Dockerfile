# 1/ Builder
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests # skipping tests for build speed

# 2/ Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# copying built .jar from 1st stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]