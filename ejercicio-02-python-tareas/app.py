"""
API de tareas (TODO) en memoria — Flask + flask-cors.
IMPORTANTE: host 0.0.0.0 para aceptar conexiones desde fuera del contenedor.
"""
from __future__ import annotations

import os
from flask import Flask, jsonify, request
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

_tasks: list[dict] = [
    {"id": 1, "title": "Leer guía Docker", "done": False},
    {"id": 2, "title": "Probar curl en EC2", "done": True},
]
_next_id = 3


@app.get("/tasks")
def list_tasks():
    return jsonify(_tasks)


@app.post("/tasks")
def create_task():
    global _next_id
    data = request.get_json(silent=True) or {}
    title = (data.get("title") or "").strip()
    if not title:
        return jsonify({"error": "title es obligatorio"}), 400
    task = {"id": _next_id, "title": title, "done": False}
    _next_id += 1
    _tasks.append(task)
    return jsonify(task), 201


@app.patch("/tasks/<int:task_id>")
def toggle_task(task_id: int):
    for t in _tasks:
        if t["id"] == task_id:
            t["done"] = not t["done"]
            return jsonify(t)
    return jsonify({"error": "tarea no encontrada"}), 404


if __name__ == "__main__":
    port = int(os.environ.get("PORT", "5000"))
    app.run(host="0.0.0.0", port=port)
