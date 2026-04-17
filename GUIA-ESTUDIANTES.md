# Guía para estudiantes: Docker, contenedores y Docker Compose

Este repositorio es un ejemplo **muy pequeño** pensado para quien empieza con Docker. Hay **tres aplicaciones** (tres “proyectos” en carpetas) y **tres Dockerfiles** en la raíz, más un **`docker-compose.yml`** que levanta todo junto.

## Ideas clave (en pocas palabras)

- **Imagen**: una “plantilla” de sistema de archivos + configuración (por ejemplo: tiene Python instalado y tu código copiado dentro).
- **Contenedor**: una **instancia en ejecución** creada a partir de una imagen. Es como ejecutar un proceso aislado con su propio sistema de archivos y red.
- **Dockerfile**: receta paso a paso para **construir** una imagen (`FROM`, `COPY`, `RUN`, `CMD`, etc.).
- **Docker Compose**: un archivo YAML que describe **varios servicios** (varios contenedores), cómo se construyen, qué puertos publican y cómo se conectan entre sí.

## Qué hay en este repo

| Carpeta / archivo        | Rol |
|--------------------------|-----|
| `java-backend/`          | Backend Java: un solo archivo `App.java` con un servidor HTTP mínimo (solo JDK). |
| `python-backend/`        | Backend Python: `app.py` con Flask y un par de rutas JSON. |
| `node-frontend/`         | Frontend: Express sirve HTML estático y **reenvía** peticiones a los dos backends (proxy). |
| `Dockerfile.java`        | Construye la imagen del backend Java (compila `App.java` y ejecuta `java App`). |
| `Dockerfile`             | Construye la imagen del backend Python (instala dependencias y ejecuta `app.py`). |
| `Dockerfile.node`        | Construye la imagen del frontend Node (`npm install` y `npm start`). |
| `docker-compose.yml`     | Orquesta los tres servicios y define variables de entorno para las URLs internas. |

### Nombre del proyecto (prefijo `taller-docker-*` en contenedores)

En el `docker-compose.yml`, la clave de nivel superior `name: taller-docker` define el **nombre del proyecto** de Compose: si lo cambias (por ejemplo `name: curso-maria-2026`), los contenedores pasarán a llamarse `curso-maria-2026-frontend-1`, etc. Alternativas sin editar el archivo: variable de entorno `COMPOSE_PROJECT_NAME=mi-nombre` o `docker compose -p mi-nombre up`.

### Por qué el frontend hace de “puente”

Dentro de Docker Compose, los contenedores se ven por **nombre de servicio** (por ejemplo `http://java-backend:8080`). Eso funciona **entre contenedores**, pero el **navegador en tu ordenador** no conoce esos nombres. Por eso la página HTML se sirve desde **Node** y las llamadas a los APIs van a rutas del mismo sitio (`/api/java/...`, `/api/python/...`); Node las **proxifica** hacia los otros contenedores. Así el alumno ve claramente: red interna de Compose + un punto de entrada público (puerto 3000).

## Cómo ejecutarlo

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Luego abre en el navegador:

- **Interfaz**: [http://localhost:3000](http://localhost:3000)

Opcionalmente puedes probar los backends directamente (puertos publicados en el `docker-compose.yml`):

- Java: [http://localhost:8080/api/hello](http://localhost:8080/api/hello)
- Python (mapeado al host como 5001): [http://localhost:5001/api/hello](http://localhost:5001/api/hello)

Para parar: `Ctrl+C` o, en otra terminal, `docker compose down`.

## Cómo se relacionan Dockerfile y contexto (`build.context`)

En `docker-compose.yml`, cada servicio tiene:

```yaml
build:
  context: .
  dockerfile: Dockerfile.java   # (o Dockerfile, Dockerfile.node)
```

- **`context: .`**: el directorio que Docker usa como origen de los archivos que se pueden copiar con `COPY` (aquí, la raíz del repo).
- **`dockerfile: ...`**: qué receta usar para **esa** imagen.

Los tres Dockerfiles hacen `COPY java-backend/ .`, `COPY python-backend/ .` o `COPY node-frontend/ .` para meter **solo** el código de cada app en su imagen (buena práctica pedagógica: una imagen, una responsabilidad).

## Comandos útiles para practicar

Construir solo una imagen (ejemplo Java), desde la raíz:

```bash
docker build -t demo-java -f Dockerfile.java .
```

Ver contenedores en marcha:

```bash
docker compose ps
```

Ver logs de un servicio:

```bash
docker compose logs -f frontend
```

## Resumen

Has visto **tres imágenes** construidas con **tres Dockerfiles**, **tres contenedores** definidos como servicios en **Compose**, y cómo se **conectan por red** usando nombres de servicio. El siguiente paso natural en un curso sería añadir volúmenes para desarrollo, variables de entorno por entorno (dev/prod) y healthchecks; aquí se ha preferido mantener el ejemplo mínimo para no saturar al principiante.
