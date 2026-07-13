# 🖥️ ISAALAB Dashboard

> Panel de monitoreo ligero para servidores Linux con Docker

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green)

## 🚀 Características

- 📊 **Monitoreo del servidor** — CPU, RAM, disco y uptime en tiempo real
- 🐳 **Gestión de contenedores Docker** — Listar, iniciar, detener, reiniciar
- 📋 **Logs en vivo** — Visualizar logs con carga incremental
- 🔍 **Inspección de contenedores** — `docker inspect` desde el panel
- 📈 **Gráficas en tiempo real** — Historial de CPU y RAM con Chart.js
- 🔎 **Filtro de contenedores** — Búsqueda en tiempo real
- 🌙 **Tema claro/oscuro** — Toggle de tema
- 🌐 **Internacionalización** — Español / English
- 🔐 **Autenticación básica** — Acceso seguro a la API

## 🛠️ Tecnologías

- **Java 21** + **Spring Boot 3.4.4**
- **Gradle** — Build tool
- **Docker** — Contenerización
- **Chart.js** — Gráficas en tiempo real
- **HTML + CSS + JS vanilla** — Frontend

## 🚀 Inicio rápido

```bash
# Clonar
git clone https://github.com/millanda29/isaalab-dashboard.git
cd isaalab-dashboard

# Construir y ejecutar con Docker
docker compose up -d --build

# Abrir
http://localhost:8080
```

## 🔐 Credenciales

| Usuario | Contraseña |
|---------|------------|
| `admin` | `admin` |

> ⚠️ Cambia las credenciales en `SecurityConfig.java` antes de producción.

## 📡 API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/system` | CPU, RAM, disco, uptime |
| GET | `/api/docker/containers` | Listar contenedores |
| GET | `/api/docker/stats` | Stats por contenedor |
| GET | `/api/docker/logs/{id}` | Logs de contenedor |
| GET | `/api/docker/inspect/{id}` | Inspeccionar contenedor |
| POST | `/api/docker/start/{id}` | Iniciar contenedor |
| POST | `/api/docker/stop/{id}` | Detener contenedor |
| POST | `/api/docker/restart/{id}` | Reiniciar contenedor |

## 📁 Estructura

```
src/main/java/com/isaalab/dashboard/
├── config/SecurityConfig.java
├── docker/
│   ├── ContainerInfo.java
│   ├── DockerController.java
│   ├── DockerLogsService.java
│   ├── DockerService.java
│   └── DockerStatsService.java
├── system/
│   ├── SystemController.java
│   ├── SystemInfo.java
│   └── SystemService.java
└── dashboard/DashboardController.java
```

## 📄 Licencia

MIT
