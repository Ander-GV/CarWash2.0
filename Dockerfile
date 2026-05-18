FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
# Descargar las dependencias (optimiza el caché de Docker)
RUN mvn dependency:go-offline -B
COPY src ./src
# Compilar el proyecto saltando los tests para mayor rapidez
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiar el archivo .jar generado en la fase anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
