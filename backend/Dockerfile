# Agendaty Backend - Dockerfile multietapa
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
