FROM gradle:8.3-jdk17 AS build
WORKDIR /app
COPY . .
ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 