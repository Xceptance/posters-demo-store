# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for faster caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Ensure required directories exist for mounting
RUN mkdir -p /app/db /app/log

# Copy the built JAR file from the build stage
COPY --from=build /app/target/posters-demo-store-*.jar app.jar

# Expose the internal port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
