FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
ARG SERVICE_NAME
ARG 8087=8080
COPY --from=build /app/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-*-all.jar /app/app.jar
EXPOSE ${8087}
CMD ["java", "-jar", "/app/app.jar"] 