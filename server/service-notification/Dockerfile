FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :service-notification:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/service-notification/build/libs/service-notification-*-all.jar /app/app.jar
EXPOSE 8085
CMD ["java", "-jar", "/app/app.jar"]
