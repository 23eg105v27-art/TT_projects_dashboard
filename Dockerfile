FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy only build descriptor first for better dependency layer caching
COPY pom.xml ./

# Preload Maven dependencies to speed up rebuilds
RUN mvn -B -q -DskipTests dependency:go-offline

# Copy source and build application jar
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Run as non-root for production safety
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/target/TTdashboard-0.0.1-SNAPSHOT.jar /app/app.jar

ENV PORT=8080
ENV JAVA_OPTS=""

EXPOSE 8080

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]