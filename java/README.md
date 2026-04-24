# Laboratorio: Docker y Docker Compose con Spring Boot + MySQL (catálogo de cartas)

Este proyecto sigue la guía **«Dockerfile y Docker Compose con Spring Boot + MySQL»** (tema: catálogo de cartas): empaquetar la aplicación en una imagen Docker, ejecutarla en un contenedor y levantar **aplicación + MySQL** con un solo comando usando Docker Compose.

## Contexto y objetivos

- **Contexto:** una aplicación Spring Boot expone un API REST; los datos de las cartas se guardan en **MySQL**. Docker empaqueta la app (Java incluido); Docker Compose orquesta la app y la base en la misma red.
- **Objetivo general:** construir una imagen a partir de un JAR, ejecutar contenedores y usar Compose para levantar el entorno completo.
- **Objetivos específicos:** entender Dockerfile vs imagen vs contenedor; conectar Spring Boot a MySQL usando el **nombre del servicio** (`db`) como host en la URL JDBC; practicar `docker compose up` / `down`.

## Requisitos previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) o Docker Engine con **Docker Compose** habilitado.
- **Java 17** y **Maven** instalados para compilar el proyecto (`mvn clean package`).

## Estructura del proyecto

Tras compilar, la carpeta debería ser similar a esta:

```text
java/
├── Dockerfile                      # imagen que copia el JAR ya compilado
├── docker-compose.yml              # MySQL + app (laboratorio guiado)
├── docker-compose.SOLUCION.yml     # misma idea, referencia docente
├── .dockerignore
├── pom.xml
├── README.md
├── EXERCISE-docker-compose.md      # material complementario (si aplica)
├── .env.example                    # opcional; el Compose del PDF usa variables en el YAML
└── src/
    ├── main/
    │   ├── java/com/example/cartas/
    │   │   ├── CartasApplication.java
    │   │   ├── model/Carta.java
    │   │   ├── repo/CartaRepository.java
    │   │   └── web/CartaController.java
    │   └── resources/application.properties
    └── test/
        ├── java/.../CartasApplicationTests.java
        └── resources/application.properties   # H2 para tests
```

## Caso de uso y API REST

Cada carta tiene campos como **id**, **nombre**, **tipo**, **rareza** y **ataque**, persistidos en MySQL (JPA crea/actualiza tablas según `spring.jpa.hibernate.ddl-auto`).

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/cartas` | Lista todas las cartas |
| `GET` | `/cartas/{id}` | Obtiene una carta por id |
| `POST` | `/cartas` | Crea una carta (cuerpo JSON) |

Ejemplo de creación:

```bash
curl -X POST http://localhost:8080/cartas ^
  -H "Content-Type: application/json" ^
  -d "{\"nombre\":\"Bola de Fuego\",\"tipo\":\"Instantáneo\",\"rareza\":\"Común\",\"ataque\":3}"
```

En PowerShell puedes usar `Invoke-RestMethod` o comillas simples en el JSON según tu entorno.

---

## Parte 1: Conexión a MySQL (`application.properties`)

La aplicación usa la URL JDBC contra el host **`db`** (nombre del servicio MySQL en Compose), **no** `localhost`, y credenciales por variables de entorno:

- `spring.datasource.url=jdbc:mysql://db:3306/cartasdb?...`
- `spring.datasource.username=${DB_USERNAME}`
- `spring.datasource.password=${DB_PASSWORD}`

Los valores concretos de usuario y contraseña se definen en **Docker Compose** (`environment` del servicio `app`).

---

## Parte 2: Dockerfile

El `Dockerfile` de este repo copia el JAR generado por Maven:

- Nombre del artefacto: **`cartas-0.0.1-SNAPSHOT.jar`** (coincide con `artifactId` y `version` en `pom.xml`).
- Imagen base: **Eclipse Temurin 17 JRE (Alpine)** (equivalente moderno a la imagen `openjdk:17-jdk-alpine` que suele citarse en guías; ajusta la línea `FROM` si tu curso exige otra imagen).

Si cambias `artifactId` o `version` en `pom.xml`, debes actualizar la ruta del `COPY` en el `Dockerfile`.

---

## Parte 3: Compilar la aplicación

Desde la carpeta `java/`:

```bash
mvn clean package
```

Comprueba que exista el JAR, por ejemplo:

```text
target/cartas-0.0.1-SNAPSHOT.jar
```

Si el archivo no existe, `docker build` fallará en el paso `COPY`.

---

## Parte 4: Construir la imagen Docker

```bash
docker build -t cartas-app:1.0 .
```

Listar imágenes:

```bash
docker images
```

Deberías ver `cartas-app` con tag `1.0`.

---

## Parte 5: Probar solo el contenedor de la app

La guía propone ejecutar la imagen así:

```bash
docker run --name cartas-demo -p 8080:8080 cartas-app:1.0
```

**Nota:** esta aplicación arranca **con JPA y MySQL**; el `application.properties` de producción apunta al host **`db`**. Un `docker run` aislado **no** resuelve ese nombre salvo que unas el contenedor a una red donde exista un servicio MySQL con alias o nombre `db`, y definas `DB_USERNAME` / `DB_PASSWORD`. Para el laboratorio, el flujo principal de verificación es **Docker Compose** (siguiente sección).

Para detener y eliminar el contenedor de prueba:

```bash
docker stop cartas-demo
docker rm cartas-demo
```

---

## Parte 6 y 7: `docker-compose.yml`

El archivo define:

- **Servicio `db`:** imagen `mysql:8.0`, base `cartasdb`, usuario y contraseña de aplicación, puerto **3306** publicado, volumen **`cartas-data`** para persistir datos.
- **Servicio `app`:** construye la imagen con el `Dockerfile` del proyecto, publica **8080**, inyecta `DB_USERNAME` y `DB_PASSWORD`, y declara dependencia del servicio `db`.

En este repositorio se añadió un **healthcheck** en MySQL y `depends_on` con `condition: service_healthy` para reducir fallos por arranque en orden (la guía en papel suele usar solo `depends_on: - db`).

---

## Parte 8: Levantar todo el entorno

Desde `java/`:

```bash
docker compose up --build
```

- `--build` fuerza reconstruir la imagen de la app si cambiaste código o Dockerfile.

---

## Parte 9: Verificar

En otra terminal:

```bash
docker compose ps
```

Deberían figurar los contenedores (por ejemplo `cartas-mysql` y `cartas-app`).

Probar el API:

```bash
curl http://localhost:8080/cartas
```

Tras insertar cartas con `POST`, los datos quedan en el volumen `cartas-data` mientras no borres el volumen.

---

## Parte 10: Detener el entorno

```bash
docker compose down
```

Para detener y **eliminar también el volumen** de MySQL (se pierden los datos):

```bash
docker compose down -v
```

---

## Errores comunes (resumen)

| Síntoma | Causa probable | Qué hacer |
|--------|----------------|-----------|
| `COPY failed` en `docker build` | No existe el JAR o el nombre en el `Dockerfile` no coincide con `target/` | Ejecutar `mvn clean package` y revisar el nombre en `target/` |
| La app no conecta a MySQL | La URL usa `localhost` en lugar del servicio Compose | En Docker Compose el host debe ser **`db`** (como en `application.properties`) |
| Cambios en código sin efecto | Compose no reconstruyó la imagen | `docker compose up --build` |
| Puerto 8080 ocupado | Otra aplicación usa el puerto | Cambiar el mapeo, por ejemplo `8081:8080` en `docker-compose.yml` |
| Datos desaparecieron | Se usó `docker compose down -v` | Evitar `-v` si quieres conservar datos |

---

## Hoja rápida de comandos

| Acción | Comando |
|--------|---------|
| Compilar | `mvn clean package` |
| Construir imagen | `docker build -t cartas-app:1.0 .` |
| App sola (aviso: requiere MySQL alcanzable como `db`) | `docker run --name cartas-demo -p 8080:8080 cartas-app:1.0` |
| Ver imágenes | `docker images` |
| Levantar todo | `docker compose up --build` |
| Estado de servicios | `docker compose ps` |
| Bajar entorno | `docker compose down` |
| Bajar y borrar volumen | `docker compose down -v` |

---

## Tests y ejecución local sin Docker

Los tests usan **H2** en memoria (`src/test/resources/application.properties`):

```bash
mvn test
```

Ejecutar la app en el IDE con el `application.properties` principal requiere MySQL accesible en **`db:3306`** (poco habitual fuera de Compose) o adaptar temporalmente la URL a `localhost` y exportar `DB_USERNAME` / `DB_PASSWORD`; lo habitual en clase es desarrollar contra **Compose**.

---

## Diagrama mental (Compose)

```text
+----------------------+       JDBC a db:3306        +------------------------+
| Contenedor app       |  ------------------------>  | Contenedor MySQL       |
| Spring Boot :8080    |                             | Base cartasdb :3306    |
+----------------------+                             +------------------------+
```

La aplicación **no** debe usar `localhost` para MySQL dentro de Compose: debe usar el nombre del servicio (**`db`**), porque ambos contenedores comparten la red interna que crea Docker Compose.

---

## Anexo: elección de imagen base Java

Este laboratorio usa **Java 17** y una imagen **Eclipse Temurin JRE Alpine** en el `Dockerfile`. Otras familias habituales (Corretto, Microsoft Build of OpenJDK, imágenes multietapa con Maven) son válidas en otros proyectos; para este taller basta con **JAR + JRE** y un `COPY` del artefacto compilado localmente.

Documentación: [Eclipse Temurin en Docker Hub](https://hub.docker.com/_/eclipse-temurin).
