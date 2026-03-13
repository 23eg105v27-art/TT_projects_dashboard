# Place this Dockerfile in the project root (same level as pom.xml)

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only files needed for dependency resolution and build (better layer caching)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Pre-fetch dependencies for faster incremental builds
RUN if [ -f ./mvnw ]; then chmod +x ./mvnw; ./mvnw -B -q -DskipTests dependency:go-offline; else mvn -B -q -DskipTests dependency:go-offline; fi

# Copy source only after dependencies are cached
COPY src ./src

# Build jar without tests
RUN if [ -f ./mvnw ]; then ./mvnw -B -DskipTests clean package; else mvn -B -DskipTests clean package; fi

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

ENV PORT=8080

# Copy only the built artifact into the lightweight runtime image
COPY --from=build /app/target/TTdashboard-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

# Render sets PORT dynamically; this keeps compatibility with local runs too
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
