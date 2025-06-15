FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app

# 复制Gradle配置文件
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc

# 下载依赖项
RUN gradle dependencies --no-daemon

# 复制源代码
COPY . .

# 构建项目
ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:build --no-daemon

# 运行阶段
FROM openjdk:17-slim

WORKDIR /app

# 复制构建产物
ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar /app/app.jar

# 设置环境变量
ENV JAVA_OPTS=""

# 暴露端口（根据服务类型设置）
ARG PORT
EXPOSE ${PORT}

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"] 