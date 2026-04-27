"""
Inventario con Flask + psycopg2 contra PostgreSQL.
Credenciales y host leídos de variables de entorno (ver docker-compose.yml).
"""
from __future__ import annotations

import os
from flask import Flask, jsonify, request
from flask_cors import CORS
import psycopg2
from psycopg2.extras import RealDictCursor

app = Flask(__name__)
CORS(app)


def get_conn():
    return psycopg2.connect(
        host=os.environ["DB_HOST"],
        port=os.environ.get("DB_PORT", "5432"),
        dbname=os.environ["DB_NAME"],
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
    )


@app.get("/products")
def list_products():
    with get_conn() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute("SELECT id, name, stock FROM products ORDER BY id")
            rows = cur.fetchall()
    return jsonify([dict(r) for r in rows])


@app.post("/products")
def create_product():
    data = request.get_json(silent=True) or {}
    name = (data.get("name") or "").strip()
    stock = data.get("stock", 0)
    if not name:
        return jsonify({"error": "name es obligatorio"}), 400
    try:
        stock = int(stock)
    except (TypeError, ValueError):
        return jsonify({"error": "stock debe ser entero"}), 400
    with get_conn() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(
                "INSERT INTO products (name, stock) VALUES (%s, %s) RETURNING id, name, stock",
                (name, stock),
            )
            row = cur.fetchone()
        conn.commit()
    return jsonify(dict(row)), 201


@app.patch("/products/<int:product_id>/stock")
def patch_stock(product_id: int):
    data = request.get_json(silent=True) or {}
    if "stock" not in data:
        return jsonify({"error": "campo stock obligatorio"}), 400
    try:
        stock = int(data["stock"])
    except (TypeError, ValueError):
        return jsonify({"error": "stock debe ser entero"}), 400
    with get_conn() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(
                "UPDATE products SET stock = %s WHERE id = %s RETURNING id, name, stock",
                (stock, product_id),
            )
            row = cur.fetchone()
        conn.commit()
    if row is None:
        return jsonify({"error": "producto no encontrado"}), 404
    return jsonify(dict(row))


if __name__ == "__main__":
    port = int(os.environ.get("PORT", "5000"))
    app.run(host="0.0.0.0", port=port)
