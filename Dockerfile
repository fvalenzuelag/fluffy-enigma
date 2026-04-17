# Imagen oficial de Python (más pequeña y mantenida que instalar Python sobre Ubuntu).
FROM python:3.12-slim

WORKDIR /app

# Copiamos primero solo dependencias para aprovechar la caché de capas de Docker.
COPY python-backend/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Código de la aplicación Python.
COPY python-backend/ .

EXPOSE 5000

CMD ["python", "app.py"]
