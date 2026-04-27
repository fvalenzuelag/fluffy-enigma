# Imagen slim de Python 3.12: equilibrio entre tamaño y herramientas estándar.
FROM python:3.12-slim

WORKDIR /app

# Instalamos dependencias antes de copiar el resto (mejor caché si solo cambia el código).
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

ENV PYTHONUNBUFFERED=1

EXPOSE 5000

# flask run también sirve; aquí usamos app.run desde __main__ con host 0.0.0.0.
CMD ["python", "-u", "app.py"]
