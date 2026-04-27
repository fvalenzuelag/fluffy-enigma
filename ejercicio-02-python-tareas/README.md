# Ejercicio 2 — Python 3.12: API de tareas (sin base de datos)

**Flask** + **flask-cors**. Endpoints: `GET /tasks`, `POST /tasks`, `PATCH /tasks/:id` (alterna `done`). Datos en memoria.

---

## Archivo: `requirements.txt`

### Build

```bash
cd ejercicio-02-python-tareas
docker build -t tareas-api:1.0 .
```

### Run

```bash
docker run --rm -p 5000:5000 tareas-api:1.0
```

### Verificación

```bash
curl -s http://localhost:5000/tasks
curl -s -X POST http://localhost:5000/tasks -H "Content-Type: application/json" -d "{\"title\":\"Nueva tarea\"}"
curl -s -X PATCH http://localhost:5000/tasks/1
```

---

## Archivo: `app.py`

`app.run(host="0.0.0.0", ...)` es **obligatorio** dentro de Docker: si usaras `localhost` o `127.0.0.1` solo, el proceso no escucharía en la interfaz del contenedor y el mapeo de puertos no vería el servicio.

### Build / run / curl

(mismos comandos que la sección anterior.)

---

## Archivo: `Dockerfile`

Base **python:3.12-slim**, **EXPOSE 5000**.

### Build

```bash
cd ejercicio-02-python-tareas
docker build -t tareas-api:1.0 .
```

### Run

```bash
docker run --rm -p 5000:5000 tareas-api:1.0
```

### Verificación

```bash
curl -s http://localhost:5000/tasks
```

---

## Reflexión técnica

El punto crítico es **`host='0.0.0.0'`** en `app.run`. Por defecto Flask en desarrollo puede enlazar solo a `127.0.0.1`, lo que dentro de un contenedor impide que el tráfico entrante por el puerto mapeado (`-p 5000:5000`) llegue al proceso. Enlazar a `0.0.0.0` significa “escuchar en todas las interfaces de red del contenedor”, que es lo que espera Docker al publicar puertos.
