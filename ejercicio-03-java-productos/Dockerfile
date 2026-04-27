# Etapa 1: compilar con Maven + Temurin 21 (JDK completo).
# Así la imagen final no incluye compilador ni dependencias de build.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

# Etapa 2: solo JRE 21 Alpine + JAR (superficie de ataque y tamaño reducidos).
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /src/target/products-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# -XX:+UseContainerSupport: el JVM respeta límites CPU/memoria del cgroup (contenedor).
# En versiones recientes puede venir activado por defecto; se deja explícito para el laboratorio.
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
