"""
API mínima en Flask para el taller de Docker.
Escucha en 0.0.0.0 para que el contenedor acepte conexiones desde la red de Docker.
"""

from flask import Flask, jsonify

app = Flask(__name__)


@app.route("/api/hello")
def hello():
    return jsonify(service="python", message="Hola desde el backend Python")


@app.route("/health")
def health():
    return jsonify(status="ok")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
