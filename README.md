# Taller Docker para estudiantes

Repositorio de ejemplo para aprender **Docker** y **Docker Compose** con tres servicios pequeños (Java, Python y Node) que se comunican entre sí.

## Qué es Docker (idea general)

**Docker** empaqueta una aplicación con todo lo que necesita para ejecutarse (bibliotecas, herramientas, configuración) en una unidad llamada **imagen**. Al arrancar una imagen obtienes un **contenedor**: un proceso aislado que comparte el kernel del sistema operativo del host pero tiene su propio sistema de archivos y red de forma controlada.

Ventajas habituales en clase y en la industria:

- **“En mi máquina funcionaba”**: todos usan el mismo entorno definido en el Dockerfile.
- **Despliegue**: la misma imagen puede ejecutarse en portátiles, servidores o la nube.
- **Aislamiento**: cada servicio en su contenedor, con dependencias separadas.

Conceptos que conviene tener claros desde el principio:

| Concepto | Qué es |
|----------|--------|
| **Imagen** | Plantilla de solo lectura con capas (SO base + tu app + dependencias). |
| **Contenedor** | Instancia en ejecución de una imagen. |
| **Dockerfile** | Archivo de instrucciones para **construir** una imagen (`FROM`, `COPY`, `RUN`, `CMD`, etc.). |
| **Docker Compose** | Archivo (normalmente `docker-compose.yml`) que describe **varios** servicios: imágenes, puertos, variables y cómo se enlazan. |

## Requisitos

- [Docker Desktop](https://docs.docker.com/get-docker/) (o Docker Engine + plugin Compose) instalado y en ejecución.
- Terminal básica y, para seguir el código, nociones mínimas de Java, Python o Node según el servicio que quieras tocar.

## Inicio rápido

En la raíz del repositorio:

```bash
docker compose up --build
```

Luego abre el **frontend** en el navegador: [http://localhost:3000](http://localhost:3000)

Para detener: `Ctrl+C` o, en otra terminal, `docker compose down`.

Los backends también exponen puertos en el host (consulta `docker-compose.yml`): por ejemplo Java en el **8080** y Python en el **5001** (mapeado al 5000 del contenedor).

## Qué incluye este repo

- Tres carpetas de aplicación: `java-backend/`, `python-backend/`, `node-frontend/`.
- Tres Dockerfiles en la raíz: `Dockerfile.java`, `Dockerfile`, `Dockerfile.node`.
- Un `docker-compose.yml` que orquesta los tres servicios y la red entre ellos.

Para una explicación paso a paso (tabla de archivos, proxy del frontend, `build.context`, comandos de práctica y siguientes pasos en un curso), lee la **[Guía para estudiantes](GUIA-ESTUDIANTES.md)**.

## Comandos útiles

```bash
# Estado de los servicios
docker compose ps

# Logs en vivo del servicio frontend
docker compose logs -f frontend

# Construir solo la imagen Java (ejemplo), desde la raíz
docker build -t demo-java -f Dockerfile.java .
```

## Más documentación oficial

- [Documentación de Docker](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
