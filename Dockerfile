# Construccion de Multi-Stage de Docker
# ETAPA 1: BUILD
# Usamos una imagen que ya tiene MAVEN instalado para compilar
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Creamos la carpeta de trabajo
WORKDIR /app

# Copiamos primero el pom.xml y descargamos dependencias.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos y generamos el JAR.
RUN mvn clean package -DskipTests

# ETAPA 2: RUN (Ejecución)
# Usamos una imagen "vacía" solo con Java (sin Maven) para que pese poco.
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

#Copiamos el JAR generado en la etapa anterior (build)
COPY --from=build /app/target/booking-api-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Arrancamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]