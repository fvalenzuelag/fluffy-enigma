# Actualizar paquetes
`sudo apt update`

# Instalar dependencias
`sudo apt install -y ca-certificates curl gnupg`

# Agregar repositorio oficial de Docker
```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
```

```bash
echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

# Instalar Docker Engine
```bash
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin
```

# Verificar
`sudo docker run hello-world`

# Habilitar el servicio de Docker
`sudo systemctl enable docker`

# Iniciar el servicio de Docker
`sudo systemctl start docker`

# Asignar permisos de administrador sobre Docker a usuario 'Ubuntu'
```bash
sudo usermod -aG docker ubuntu
newgrp docker
``` 
(solo se corre la primera vez)

# Adicional (opcional):
Cambiar el hostname/nombre de maquina: 
`sudo hostnamectl set-hostname nuevo-nombre`
Actualizar consola parar ver el cambio: 
`exec bash`