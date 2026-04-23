# Ejercicio: `docker-compose` con MySQL y la app Spring Boot

Objetivo: que practiques **orquestación con Docker Compose**: un segundo contenedor (**MySQL 8.4**) ya está definido en `docker-compose.yml`; tu trabajo es **añadir el servicio de la aplicación Java**, enlazarlo a la base de datos con **variables de entorno** y comprobar que la API responde usando MySQL.

## Qué tienes ya en el proyecto

- **`Dockerfile`**: imagen de referencia para construir el JAR de Spring Boot.
- **`docker-compose.yml`**: servicio **`db`** (MySQL) con variables tomadas de un archivo **`.env`** (no versiones `.env` en git: copia desde **`.env.example`**).
- **API de comprobación**: [http://localhost:8080/api/db-ping](http://localhost:8080/api/db-ping) devuelve metadatos de la base de datos cuando la conexión funciona (con H2 en local o con MySQL en Docker).

## Parte 0 — Preparar variables de entorno

1. En la carpeta `java/`, copia el ejemplo:  
   `cp .env.example .env` (en Windows PowerShell: `Copy-Item .env.example .env`).
2. Edita **`.env`** y pon contraseñas distintas de ejemplo si quieres (en clase basta con valores sencillos, nunca reutilices esas claves fuera del laboratorio).

Levanta solo la base de datos para validar:

```bash
docker compose up db
```

En otra terminal, opcionalmente prueba un cliente MySQL apuntando a `localhost:3306` con el usuario y la base definidos en `.env`.

## Parte 1 — Variables del contenedor MySQL (oficial)

El servicio `db` usa la imagen **`mysql:8.4`**. Las variables más habituales que expone la imagen oficial (resumen) son:

| Variable | Rol |
|----------|-----|
| `MYSQL_ROOT_PASSWORD` | Contraseña del usuario `root` dentro del servidor MySQL. |
| `MYSQL_DATABASE` | Nombre de la base de datos que se crea al arrancar. |
| `MYSQL_USER` / `MYSQL_PASSWORD` | Usuario adicional con permisos sobre `MYSQL_DATABASE`. |

En `docker-compose.yml` ya están enlazadas a valores de **`.env`** (sustitución de Compose). Revisa el bloque `environment:` del servicio `db` y entiende qué valor llega al contenedor en cada caso.

## Parte 2 — Añadir el servicio `app` (lo que debes escribir tú)

Debes **editar `docker-compose.yml`** y declarar un segundo servicio, por ejemplo llamado **`app`**, que:

1. **Construya la imagen** a partir del `Dockerfile` de esta misma carpeta (`build` en el contexto correcto).
2. **Publique el puerto 8080** del contenedor al host (por ejemplo `8080:8080`).
3. **Dependa de MySQL** de forma razonable (al menos `depends_on: db`; idealmente esperar a que el servicio esté sano usando el `healthcheck` ya definido en `db`).
4. Defina las variables de entorno que Spring Boot usa para el **DataSource** (nombres estándar):

| Variable de entorno | Ejemplo de valor (ajusta nombre de host y credenciales) |
|---------------------|----------------------------------------------------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://db:3306/dockerlab` — el host **`db`** es el nombre del servicio en Compose (DNS interno). El nombre de la base debe coincidir con `MYSQL_DATABASE` en `.env`. |
| `SPRING_DATASOURCE_USERNAME` | Mismo usuario que `MYSQL_USER` en `.env`. |
| `SPRING_DATASOURCE_PASSWORD` | Misma contraseña que `MYSQL_PASSWORD` en `.env`. |

Puedes repetir los literales en `environment` o reutilizar sustitución desde `.env` (por ejemplo `${MYSQL_USER}`) para no duplicar secretos a mano.

5. (Opcional) Añade `restart: unless-stopped` o un nombre de contenedor para facilitar el taller.

**Pista de red:** los contenedores del mismo `docker-compose` comparten red por defecto; el nombre DNS del servicio MySQL es el nombre bajo `services:` (aquí **`db`**), no `localhost` desde dentro del contenedor `app`.

## Parte 3 — Probar

Desde `java/`:

```bash
docker compose up --build
```

Luego:

- [http://localhost:8080/api/hello](http://localhost:8080/api/hello)
- [http://localhost:8080/api/db-ping](http://localhost:8080/api/db-ping) — en MySQL deberías ver algo como `databaseProductName` = `MySQL`.

Para apagar y borrar volúmenes (datos de MySQL):

```bash
docker compose down -v
```

## Criterios de aceptación (checklist)

- [ ] Existen **dos servicios** en `docker-compose.yml`: `db` y `app`.
- [ ] MySQL recibe credenciales vía **`environment`** (y/o interpolación desde **`.env`**).
- [ ] La app recibe **`SPRING_DATASOURCE_*`** coherentes con el usuario y la base creados por MySQL.
- [ ] `docker compose up --build` levanta ambos contenedores sin error y `/api/db-ping` responde con MySQL.

## Solución de referencia (solo después de intentarlo)

Hay un archivo **`docker-compose.SOLUCION.yml`** con un `app` de ejemplo. Para comparar sin mezclar con tu trabajo:

```bash
docker compose -f docker-compose.SOLUCION.yml config
```

No copies la solución literal en exámenes; úsala para contrastar nombres de variables y estructura.
