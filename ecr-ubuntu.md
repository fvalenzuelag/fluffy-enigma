# Docker Hub
```bash
# 1. Construir las imágenes (desde la carpeta de cada servicio)
docker build -t tienda-backend .
docker build -t tienda-frontend .
docker build -t tienda-db .

# 2. Iniciar sesión en Docker Hub
docker login
# → ingresa usuario y contraseña de Docker Hub

# 3. Etiquetar las imágenes con tu usuario y tag de versión
docker tag tienda-backend TU_USUARIO/tienda-backend:v1
docker tag tienda-frontend TU_USUARIO/tienda-frontend:v1
docker tag tienda-db TU_USUARIO/tienda-db:v1

# 4. Publicar (push) al registry
docker push TU_USUARIO/tienda-backend:v1
docker push TU_USUARIO/tienda-frontend:v1
docker push TU_USUARIO/tienda-db:v1
```

# Prerequisitos en Ubuntu para ECR
```bash
# 1. Configurar credenciales AWS (obtenerlas desde AWS Academy → Details → AWS CLI)
aws configure
# → pega: aws_access_key_id, aws_secret_access_key, region=us-east-1
aws configure set aws_session_token "TU_SESSION_TOKEN"

# Verificar que las credenciales funcionan
aws sts get-caller-identity

# 2. Crear repositorios en ECR (también se puede hacer desde la consola web)
aws ecr create-repository --repository-name tienda-backend --region us-east-1
aws ecr create-repository --repository-name tienda-frontend --region us-east-1
aws ecr create-repository --repository-name tienda-db --region us-east-1

# 3. Autenticar Docker hacia ECR (reemplaza TU_ACCOUNT_ID)
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# 4. Etiquetar las imágenes con la URL del repositorio ECR
docker tag tienda-backend TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-backend:v1
docker tag tienda-frontend TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-frontend:v1
docker tag tienda-db TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-db:v1

# 5. Push a ECR
docker push TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-backend:v1
docker push TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-frontend:v1
docker push TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/tienda-db:v1
```