FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :service-auth:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/service-auth/build/libs/service-auth-*-all.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]
