#!/bin/bash

# 为每个服务生成Dockerfile
generate_dockerfile() {
  service=$1
  port=$2
  
  echo "为 $service 生成 Dockerfile，端口：$port"
  
  # 创建Dockerfile
  cat > "$service/Dockerfile" << EOF
FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :$service:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/$service/build/libs/$service-*-all.jar /app/app.jar
EXPOSE $port
CMD ["java", "-jar", "/app/app.jar"]
EOF
  
  echo "已生成 $service/Dockerfile"
}

# 生成各服务的Dockerfile
generate_dockerfile "service-gateway" "8080"
generate_dockerfile "service-auth" "8081"
generate_dockerfile "service-content" "8082"
generate_dockerfile "service-user" "8083"
generate_dockerfile "service-media" "8084"
generate_dockerfile "service-notification" "8085"
generate_dockerfile "service-analytics" "8086"

echo "所有Dockerfile生成完成！" 