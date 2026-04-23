# Laboratorio Java (Spring Boot)

Proyecto **Maven** mínimo con **Spring Boot 3** y Java **21**. Sirve como base para que los estudiantes escriban su propio **Dockerfile**; en este repo hay además una **solución de referencia** en `Dockerfile` (y `.dockerignore`) para el docente o para corregir después.

**Ejercicio Docker Compose + MySQL:** sigue las instrucciones en [`EXERCISE-docker-compose.md`](EXERCISE-docker-compose.md). El fichero `docker-compose.yml` incluye ya el servicio **MySQL**; el alumno debe **añadir el servicio de la aplicación** y enlazar variables de entorno. La solución de referencia está en `docker-compose.SOLUCION.yml`.

## Estructura del proyecto

```text
java/
├── Dockerfile                      # solución de referencia (multietapa)
├── docker-compose.yml              # MySQL + volumen; el alumno añade el servicio app
├── docker-compose.SOLUCION.yml     # referencia con app + db (docente / autocorrección)
├── EXERCISE-docker-compose.md      # enunciado del ejercicio Compose + MySQL
├── .env.example                    # plantilla de variables para Compose
├── .dockerignore
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/dockerlab/
    │   │   ├── DockerLabApplication.java    # punto de entrada
    │   │   └── web/HelloController.java     # REST de ejemplo
    │   └── resources/application.properties
    └── test/java/.../DockerLabApplicationTests.java
```

## Cómo ejecutarlo en local (sin Docker)

Desde la carpeta `java/`:

```bash
mvn spring-boot:run
```

Comprueba en el navegador o con `curl`:

- [http://localhost:8080/api/hello](http://localhost:8080/api/hello) — JSON de saludo
- [http://localhost:8080/api/db-ping](http://localhost:8080/api/db-ping) — comprueba la conexión JDBC (H2 en local por defecto; MySQL con Compose)
- [http://localhost:8080/health](http://localhost:8080/health) — comprobación simple

Para generar el JAR ejecutable:

```bash
mvn -q -DskipTests package
java -jar target/docker-lab-java-0.0.1-SNAPSHOT.jar
```

## Imágenes Docker habituales para Spring Boot

En un **Dockerfile** multietapa suele haber una imagen con **JDK** (compilar con Maven o Gradle) y otra más ligera con **JRE** solo para ejecutar el JAR. Todas las opciones siguientes son imágenes oficiales o muy usadas en la práctica; elige una familia y mantén **la misma distribución de Java** (versión 21, mismo vendor) entre etapas para evitar sorpresas.

### Eclipse Temurin (Adoptium) — muy recomendada en cursos

Imágenes mantenidas por la comunidad Eclipse; buen equilibrio entre documentación, tamaño y compatibilidad.

| Uso típico | Ejemplo de tag | Notas |
|------------|----------------|-------|
| Compilar y empaquetar | `eclipse-temurin:21-jdk-jammy` | Ubuntu 22.04 (Jammy), JDK 21 |
| Compilar (Alpine, imagen más pequeña) | `eclipse-temurin:21-jdk-alpine` | Útil en etapa de build; revisa compatibilidad de `glibc` vs `musl` con herramientas nativas |
| Solo ejecutar JAR | `eclipse-temurin:21-jre-jammy` | JRE 21, sin compilador |
| Solo ejecutar (Alpine) | `eclipse-temurin:21-jre-alpine` | Contenedor final más pequeño |

Documentación: [Eclipse Temurin en Docker Hub](https://hub.docker.com/_/eclipse-temurin).

### Amazon Corretto

Distribución de OpenJDK de AWS; útil si alineas despliegue con AWS o quieres otra variante probada en producción.

| Uso típico | Ejemplo de tag |
|------------|-----------------|
| JDK | `amazoncorretto:21-al2023` |
| JDK Alpine | `amazoncorretto:21-alpine` |

Documentación: [Amazon Corretto en Docker Hub](https://hub.docker.com/_/amazoncorretto).

### Microsoft Build of OpenJDK

Opción habitual en entornos Microsoft / Azure.

| Uso típico | Ejemplo de tag |
|------------|-----------------|
| JDK | `mcr.microsoft.com/openjdk/jdk:21-ubuntu` |
| JRE / runtime | `mcr.microsoft.com/openjdk/jdk:21-mariner` (distro específica de Microsoft) |

Catálogo: [Microsoft Container Registry – OpenJDK](https://mcr.microsoft.com/en-us/catalog?search=openjdk).

### Oracle GraalVM (imagen oficial Oracle)

Interesa si usas **GraalVM Native Image** (binario nativo en lugar de JAR en JVM). No es necesario para este laboratorio, que es un JAR Spring Boot clásico.

| Uso típico | Ejemplo de tag |
|------------|-----------------|
| JDK + Native Image | `container-registry.oracle.com/graalvm/native-image:21` (ejemplo orientativo; revisa tags actuales en el registro) |

### Imagen “solo runtime” muy pequeña: Distroless (Google)

No incluye shell ni muchas utilidades: excelente para seguridad y tamaño, pero más incómoda para depurar en clase. Suele combinarse con una etapa de build en Temurin/Corretto y copiar el JAR a una imagen `gcr.io/distroless/java21-debian12` (o la variante que corresponda a tu versión de Java).

Documentación: [Google Distroless](https://github.com/GoogleContainerTools/distroless).

### Imágenes de Spring (opcional)

El proyecto Spring publica imágenes base orientadas a **Cloud Native Buildpacks** y casos concretos; para un taller donde el alumno escribe el Dockerfile a mano, **Temurin + JAR** suele ser la combinación más clara.

---

## Construir y ejecutar con la solución de referencia

Desde la carpeta `java/` (contexto = este proyecto):

```bash
docker build -t docker-lab-java .
docker run --rm -p 8080:8080 docker-lab-java
```

Desde la **raíz** del repositorio:

```bash
docker build -t docker-lab-java -f java/Dockerfile java
docker run --rm -p 8080:8080 docker-lab-java
```

La imagen final usa **Eclipse Temurin 21 JRE**; la etapa de compilación usa la imagen oficial **Maven** con el mismo JDK.

---

## Pistas si escribes el Dockerfile tú mismo

1. **Contexto de build**: el `COPY` debe alcanzar `pom.xml` y `src/` (o copiar el JAR ya construido si compilas fuera de Docker).
2. **Puerto**: la app escucha en **8080** (`EXPOSE 8080` y variable o comando coherente).
3. **Multietapa**: una etapa con `mvn package` (o Gradle) y otra que solo tenga el JAR y un JRE reduce mucho el tamaño de la imagen final.

Cuando tengas tu imagen construida, prueba algo como:

```bash
docker run --rm -p 8080:8080 nombre-de-tu-imagen
```
