# Construcción
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/tallerexpress-api-1.0.0.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Xmx400m"
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
