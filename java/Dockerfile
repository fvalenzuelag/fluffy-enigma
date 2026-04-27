# Imagen final: solo JRE. MySQL va en otro contenedor (ver docker-compose.yml).
# Build: docker build -t cartas-app:1.0 .
# Local con DB: docker compose up --build

FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /src
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/target/cartas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
