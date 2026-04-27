# Prácticas Docker — 2.º año Informática

Cinco proyectos listos para **Ubuntu EC2** (o cualquier host con Docker / Docker Compose). Cada carpeta es autónoma y trae **README** con comandos de build, ejecución y `curl`.

| Carpeta | Stack | Base de datos |
|---------|--------|----------------|
| [ejercicio-01-node-peliculas](ejercicio-01-node-peliculas) | Node.js 20, Express | No |
| [ejercicio-02-python-tareas](ejercicio-02-python-tareas) | Python 3.12, Flask | No |
| [ejercicio-03-java-productos](ejercicio-03-java-productos) | Java 21, Spring Boot 3.2 | No |
| [ejercicio-04-python-inventario-postgres](ejercicio-04-python-inventario-postgres) | Python 3.12, Flask | PostgreSQL 16 (Compose) |
| [ejercicio-05-java-empleados-mysql](ejercicio-05-java-empleados-mysql) | Java 21, Spring Boot 3.2, JPA | MySQL 8.3 (Compose) |

## Requisitos

- Docker Engine y plugin **Compose** (en Ubuntu: paquetes oficiales de Docker).
- Para los ejercicios Java sin Docker: **JDK 21** y **Maven 3.9+** (opcional; el `Dockerfile` compila con Maven en contenedor).

## Material adicional en el repo

- [aws-ubuntu-vm.md](aws-ubuntu-vm.md) — instalar Docker en Ubuntu.
- [ecr-ubuntu.md](ecr-ubuntu.md) — referencia ECR (opcional para despliegues).
