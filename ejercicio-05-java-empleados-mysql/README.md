# Ejercicio 5 — Java 21 + Spring Boot + MySQL (Docker Compose)

**Spring Data JPA** + **MySQL 8.3**. Entidad **Employee** (`id`, `name`, `department`, `salary`). `JpaRepository` con **`findByDepartment`**.

Endpoints:

- `GET /employees`
- `GET /employees/{id}`
- `GET /employees/by-department?department=IT`
- `POST /employees`
- `DELETE /employees/{id}`

**application.properties**: URL y credenciales con **valores por defecto** y sobreescritura por variables de entorno. **`spring.jpa.hibernate.ddl-auto=update`**.

El **constructor** del controlador inserta **3 empleados** si la tabla está vacía (vía **`TransactionTemplate`** para ejecutar en transacción).

---

## Archivo: `pom.xml`

### Build (Maven, tests con H2 en memoria)

```bash
cd ejercicio-05-java-empleados-mysql
mvn -B test
```

### Build / run con Compose

```bash
cd ejercicio-05-java-empleados-mysql
docker compose up --build
```

### Verificación

```bash
curl -s http://localhost:8080/employees
curl -s "http://localhost:8080/employees/by-department?department=IT"
curl -s http://localhost:8080/employees/1
curl -s -X POST http://localhost:8080/employees -H "Content-Type: application/json" -d "{\"name\":\"Pedro\",\"department\":\"Finanzas\",\"salary\":61000}"
curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/employees/4
```

### Bajar

```bash
docker compose down
```

### Bajar y borrar datos MySQL

```bash
docker compose down -v
```

---

## Archivo: `src/main/resources/application.properties`

Variables: **DB_HOST**, **DB_PORT**, **DB_NAME**, **DB_USER**, **DB_PASSWORD** (con defaults para el `docker-compose.yml`).

### Build / run / curl

(mismos comandos Compose de arriba.)

---

## Archivo: `EmployeeController.java` + `EmployeeRepository.java`

### Build / run / curl

```bash
docker compose up --build
curl -s http://localhost:8080/employees
```

---

## Archivo: `Dockerfile`

Multietapa Maven 21 → JRE 21 Alpine.

### Build (solo imagen)

```bash
cd ejercicio-05-java-empleados-mysql
docker build -t empleados-api:1.0 .
```

### Run con Compose (recomendado)

```bash
docker compose up --build
```

### Verificación

```bash
curl -s http://localhost:8080/employees | head -c 500
```

---

## Archivo: `docker-compose.yml`

**mysql:8.3**, healthcheck **`mysqladmin ping`**, volumen **mysqldata**, API con **`depends_on: condition: service_healthy`**.

### Build / run / curl

```bash
cd ejercicio-05-java-empleados-mysql
docker compose up --build
curl -s "http://localhost:8080/employees/by-department?department=RRHH"
```

---

## Reflexión técnica

Lo más relevante es **no sembrar datos JPA directamente en el cuerpo del constructor sin transacción**: Spring no aplica `@Transactional` en llamadas desde el constructor, así que usar **`TransactionTemplate.executeWithoutResult`** en el constructor garantiza que **`saveAll`** corre dentro de una transacción y evita estados inconsistentes o fallos silenciosos según el dialecto JDBC. En paralelo, **`ddl-auto=update`** en entorno de laboratorio acelera el esquema al evitar migraciones manuales, con la salvedad de que en producción suele preferirse Flyway/Liquibase.
