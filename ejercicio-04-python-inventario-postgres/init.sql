-- Se ejecuta al crear el volumen de datos la primera vez (entrypoint de postgres:16-alpine).
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

INSERT INTO products (name, stock) VALUES
    ('Cuaderno A4', 120),
    ('Bolígrafo azul', 300),
    ('Goma de borrar', 45);
