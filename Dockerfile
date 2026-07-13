# =========================
# Etapa de compilación
# =========================
FROM gradle:8.14.2-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon

# =========================
# Etapa de ejecución
# =========================
FROM eclipse-temurin:21-jre

# Instalar Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
