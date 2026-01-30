# Usamos Java 17.
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Renombramos a "app.jar".
COPY target/booking-api-0.0.1-SNAPSHOT.jar app.jar

# La app usa el puerto 8080.
EXPOSE 8080

# Iniciamos la app.
ENTRYPOINT ["java", "-jar", "app.jar"]