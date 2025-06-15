FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :service-media:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/service-media/build/libs/service-media-*-all.jar /app/app.jar
EXPOSE 8084
CMD ["java", "-jar", "/app/app.jar"]
