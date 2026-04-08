# ====== Builder stage ======
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -Dmaven.test.skip=true clean package

# ====== Runtime stage (slim) ======
FROM eclipse-temurin:21-jre-alpine AS runtime
ENV TZ=UTC \
    JAVA_OPTS=""
WORKDIR /app
COPY --from=builder /app/target/autism-tracker-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

