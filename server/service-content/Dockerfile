FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :service-content:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/service-content/build/libs/service-content-*-all.jar /app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/app.jar"]
