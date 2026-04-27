# Ejercicio 4 — Python 3.12 + PostgreSQL (Docker Compose)

**Flask** + **psycopg2**. Endpoints: `GET /products`, `POST /products`, `PATCH /products/:id/stock`.

**Compose**: servicio **db** (`postgres:16-alpine` + **healthcheck** `pg_isready`), servicio **api** (build local), volumen **pgdata**, **`depends_on` con `condition: service_healthy`**.

**init.sql**: crea la tabla e inserta **3** productos (solo en la primera inicialización del volumen).

---

## Archivo: `init.sql`

### Build / run (Compose levanta db + api)

```bash
cd ejercicio-04-python-inventario-postgres
docker compose up --build
```

### Verificación (en otra terminal, con los contenedores arriba)

```bash
curl -s http://localhost:5000/products
curl -s -X POST http://localhost:5000/products -H "Content-Type: application/json" -d "{\"name\":\"Marcador\",\"stock\":80}"
curl -s -X PATCH http://localhost:5000/products/1/stock -H "Content-Type: application/json" -d "{\"stock\":99}"
```

### Bajar (conservar datos en volumen)

```bash
docker compose down
```

### Bajar y borrar volumen PostgreSQL

```bash
docker compose down -v
```

---

## Archivo: `app.py`

Lee **DB_HOST**, **DB_PORT**, **DB_NAME**, **DB_USER**, **DB_PASSWORD** del entorno.

### Build / run / curl

(iguales que la sección anterior.)

---

## Archivo: `Dockerfile`

Solo la API; la base de datos no se incluye en esta imagen.

### Build (solo imagen API, sin compose)

```bash
cd ejercicio-04-python-inventario-postgres
docker build -t inventario-api:1.0 .
```

Sin `docker compose`, la API fallará al conectar si no pasas un Postgres accesible con esas variables.

### Run + curl con Compose

```bash
docker compose up --build
curl -s http://localhost:5000/products
```

---

## Archivo: `docker-compose.yml`

Define red implícita, **healthcheck** en `db` y **`depends_on`** con **`service_healthy`** en `api`.

### Build / run / curl

```bash
cd ejercicio-04-python-inventario-postgres
docker compose up --build
curl -s http://localhost:5000/products | head -c 400
```

---

## Archivo: `requirements.txt`

### Build / run / curl

(misma secuencia con `docker compose up --build`.)

---

## Reflexión técnica

La pieza más importante es **`depends_on` con `condition: service_healthy`** junto al **healthcheck de Postgres**. Sin eso, la API puede arrancar antes de que `init.sql` haya terminado o antes de que el servidor acepte conexiones, produciendo errores intermitentes al arrancar Compose. El healthcheck convierte el “¿está listo el servicio?” en un **criterio explícito** que Docker Compose respeta antes de iniciar contenedores dependientes.
