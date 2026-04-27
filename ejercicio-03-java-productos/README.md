# Ejercicio 3 — Java 21 + Spring Boot 3.2: API de productos (sin base de datos)

**Spring Web**. Endpoints: `GET /products`, `GET /products/:id`. Lista en memoria (`List.of`).

**Dockerfile** multietapa: **maven:3.9-eclipse-temurin-21** → **eclipse-temurin:21-jre-alpine** + **`-XX:+UseContainerSupport`**.

---

## Archivo: `pom.xml`

Spring Boot **3.2.x**, Java **21**, artefacto **`products`** → JAR `products-0.0.1-SNAPSHOT.jar`.

### Build (solo Maven, sin Docker)

```bash
cd ejercicio-03-java-productos
mvn -B package -DskipTests
```

### Build (imagen Docker)

```bash
cd ejercicio-03-java-productos
docker build -t productos-api:1.0 .
```

### Run

```bash
docker run --rm -p 8080:8080 productos-api:1.0
```

### Verificación

```bash
curl -s http://localhost:8080/products
curl -s http://localhost:8080/products/1
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/products/99
```

---

## Archivo: `src/main/java/.../ProductController.java`

Lista inmutable de productos y búsqueda por `id`.

### Build / run / curl

(mismos que arriba.)

---

## Archivo: `Dockerfile`

Multietapa + `ENTRYPOINT` con **`-XX:+UseContainerSupport`**.

### Build

```bash
cd ejercicio-03-java-productos
docker build -t productos-api:1.0 .
```

### Run

```bash
docker run --rm -p 8080:8080 productos-api:1.0
```

### Verificación

```bash
curl -s http://localhost:8080/products | head -c 200
```

---

## Reflexión técnica

La decisión clave es el **Dockerfile multietapa**: la imagen final solo contiene el **JRE** y el **JAR**, no Maven ni el código fuente. Eso reduce tamaño y superficie de ataque y acerca el contenedor a lo que se despliega en producción. Añadir **`-XX:+UseContainerSupport`** alinea el comportamiento del heap y de los hilos con los **límites de cgroup** que Docker/Kubernetes aplican al contenedor, evitando que la JVM “vea” toda la RAM del host y se reserve más memoria de la permitida.
