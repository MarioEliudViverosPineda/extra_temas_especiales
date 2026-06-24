# Imagen base con JDK 17 (puedes ajustar según tu versión de Java)
FROM eclipse-temurin:21
WORKDIR /app
COPY target/untitled-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]