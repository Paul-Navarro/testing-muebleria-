# syntax=docker/dockerfile:1

########### Etapa de build (Maven sin wrapper) ###########
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1) Cachea dependencias
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# 2) Copia el código y empaqueta
COPY src ./src
RUN mvn -B -DskipTests package

########### Etapa de runtime (JRE liviano) ###########
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# (opcional pero recomendado) ejecutar como no-root
RUN addgroup -S app && adduser -S app -G app
USER app

# Copia el jar construido
COPY --from=build /app/target/*jar /app/app.jar

EXPOSE 8080
# Permite pasar flags por JAVA_OPTS 
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
