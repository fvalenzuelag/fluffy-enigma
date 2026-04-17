# JDK para compilar y ejecutar una sola clase (sin Maven).
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY java-backend/ .

RUN javac App.java

EXPOSE 8080

CMD ["java", "App"]
