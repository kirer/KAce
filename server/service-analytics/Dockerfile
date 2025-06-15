FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :service-analytics:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/service-analytics/build/libs/service-analytics-*-all.jar /app/app.jar
EXPOSE 8086
CMD ["java", "-jar", "/app/app.jar"]
