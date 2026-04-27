# Ejercicio 1 — Node.js 20: API de películas (sin base de datos)

API REST con **Express**. Datos en memoria. Imagen base **node:20-alpine**.

---

## Archivo: `package.json`

Define el módulo ES (`"type": "module"`) y la dependencia `express`.

### Build (imagen Docker)

```bash
cd ejercicio-01-node-peliculas
docker build -t peliculas-api:1.0 .
```

### Run (contenedor)

```bash
docker run --rm -p 3000:3000 peliculas-api:1.0
```

### Verificación con `curl`

```bash
curl -s http://localhost:3000/movies
curl -s http://localhost:3000/movies/1
curl -s http://localhost:3000/movies/99
```

---

## Archivo: `src/index.js`

Servidor Express en el puerto **3000**, escucha en **0.0.0.0** para que el puerto publicado desde Docker funcione.

### Build / run / curl

(iguales que arriba; el código se incluye en la imagen al hacer `docker build`.)

---

## Archivo: `Dockerfile`

- **WORKDIR /app**
- Copia **primero** `package.json` y ejecuta `npm install` (mejor uso de caché de capas).
- Copia **`src/`** después.
- **EXPOSE 3000**
- **CMD** con `node src/index.js`

### Build

```bash
cd ejercicio-01-node-peliculas
docker build -t peliculas-api:1.0 .
```

### Run

```bash
docker run --rm -p 3000:3000 peliculas-api:1.0
```

### Verificación

```bash
curl -s http://localhost:3000/movies | jq .
curl -s http://localhost:3000/movies/2
```

---

## Reflexión técnica

La decisión más importante es **separar la copia de `package.json` de la copia del código** (`COPY` en dos fases). Así Docker puede reutilizar la capa donde se ejecutó `npm install` mientras no cambien las dependencias, lo que reduce mucho el tiempo de build en iteraciones de desarrollo y en CI — un patrón estándar en imágenes Node y en exámenes de contenedores.
