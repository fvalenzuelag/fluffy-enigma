# Imagen oficial Node 20 sobre Alpine (imagen pequeña, adecuada para EC2).
FROM node:20-alpine

WORKDIR /app

# Copiamos primero solo el manifiesto de dependencias: si no cambia package.json,
# Docker reutiliza la capa "npm install" y acelera builds sucesivos (cache de capas).
COPY package.json ./

RUN npm install --omit=dev

# El código fuente va después: cambia más a menudo que las dependencias.
COPY src/ ./src/

EXPOSE 3000

# Ejecutamos el servidor directamente con node (sin PM2 en este laboratorio).
CMD ["node", "src/index.js"]
