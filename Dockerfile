# -------------------------------------------------------------
# Étape 1 : Build de l'application (Conteneur de compilation)
# -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copie du fichier de configuration Maven pour mettre en cache les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie du code source et compilation du fichier JAR
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Étape 2 : Image d'exécution de Production (Runtime léger)
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Sécurité Production : Création d'un utilisateur non-root pour éviter les privilèges root
RUN addgroup -S soasygroup && adduser -S soasyuser -G soasygroup
USER soasyuser

# Copie du fichier JAR compilé depuis l'étape 1
COPY --from=build /app/target/*.jar app.jar

# Port exposé par Spring Boot
EXPOSE 8080

# Optimisation JVM pour la mémoire en environnement conteneurisé
ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xmx512m", "-jar", "app.jar"]
