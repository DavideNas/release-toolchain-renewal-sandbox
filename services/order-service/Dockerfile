# --- Stage 1: Build dell'applicazione ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia i file pom.xml e scarica le dipendenze (ottimizzazione della cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia i sorgenti e compila escludendo i test per velocizzare la sandbox
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Immagine di Runtime finale ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Best practice di sicurezza: non girare come utente root nel container
RUN addgroup -S devops && adduser -S devopsuser -G devops
USER devopsuser

# Copia l'artefatto compilato dallo stage precedente
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]