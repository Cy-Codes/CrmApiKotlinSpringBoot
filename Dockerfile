# ============================================================================
# CrmApiKotlinSpringBoot - Cloud-Agnostic Container Image
# The same image runs unchanged on local Docker, AWS ECS (Fargate) and
# Azure Container Apps. All environment-specific settings are injected at
# runtime through environment variables (see .env.example).
# ============================================================================
ARG JAVA_VERSION=25

# Stage 1: Build the Spring Boot fat JAR with the Gradle wrapper
FROM eclipse-temurin:${JAVA_VERSION}-jdk AS build
WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/
RUN ./gradlew --no-daemon --quiet dependencies > /dev/null

COPY src/ src/
RUN ./gradlew --no-daemon bootJar \
    && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \;

# Stage 2: Minimal JRE runtime running as a non-root user
FROM eclipse-temurin:${JAVA_VERSION}-jre AS runtime
WORKDIR /app

ARG APP_USER=crmapi
ARG APP_UID=10001
RUN useradd --system --no-create-home --uid "${APP_UID}" "${APP_USER}"

ENV SERVER_PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080
USER ${APP_USER}

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
