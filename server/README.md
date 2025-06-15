# KAce 服务端

这是KAce项目的服务端部分，采用微服务架构设计，使用Kotlin和Ktor框架开发。

## 项目结构

KAce服务端由以下微服务组成：

- `service-common`: 共享库，包含通用工具、模型和扩展
- `service-gateway`: API网关，处理路由和请求转发
- `service-auth`: 认证服务，处理用户认证和授权
- `service-user`: 用户服务，管理用户、角色和权限
- `service-content`: 内容服务，管理各种类型的内容
- `service-media`: 媒体服务，处理图片、视频等媒体文件
- `service-notification`: 通知服务，处理系统通知和消息
- `service-analytics`: 分析服务，提供数据分析功能
- `service-plugin`: 插件服务，支持系统功能扩展

## 技术栈

- **语言**: Kotlin
- **框架**: Ktor
- **数据库**: PostgreSQL
- **ORM**: Exposed
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **搜索引擎**: Elasticsearch
- **对象存储**: MinIO
- **依赖注入**: Koin
- **构建工具**: Gradle (Kotlin DSL)

## 依赖管理

项目使用Gradle版本目录(libs.versions.toml)进行依赖管理，所有依赖和版本都集中在`gradle/libs.versions.toml`文件中定义。

## Docker支持

项目提供完整的Docker支持，包括：

- 每个服务的Dockerfile
- docker-compose.yml文件，用于本地开发和测试
- 支持PostgreSQL、Redis、Elasticsearch、MinIO和RabbitMQ等基础设施服务

### 本地开发

要在本地启动所有服务，请运行：

```bash
docker-compose up
```

要只启动基础设施服务（数据库等），请运行：

```bash
docker-compose up postgres redis elasticsearch minio rabbitmq
```

## 端口分配

- Gateway: 8080
- Auth: 8081
- Content: 8082
- User: 8083
- Media: 8084
- Notification: 8085
- Analytics: 8086
- Plugin: 8087

## 开发指南

请参考`CODE_STRUCTURE.md`文件了解代码结构和开发规范。

## 项目架构

KAce服务端采用微服务架构，由以下服务组成：

- **auth-service**: 认证授权服务，负责用户认证和令牌管理
- **user-service**: 用户服务，负责用户、角色、权限和组织管理
- **content-service**: 内容服务，负责内容、分类和标签管理
- **media-service**: 媒体服务，负责媒体文件和处理任务管理
- **analytics-service**: 分析服务，负责事件跟踪和指标收集
- **notification-service**: 通知服务，负责通知和消息发送
- **gateway**: API网关，负责请求路由和统一入口
- **plugins**: 插件系统，支持动态扩展平台功能

## 开发进度

所有计划的服务和功能已全部完成实现，包括：

- 认证服务：JWT认证、令牌管理、Redis令牌存储
- 用户服务：用户管理、角色权限管理、组织管理
- 内容服务：内容管理、分类管理、标签管理、内容版本控制
- 媒体服务：媒体文件管理、媒体文件夹管理、媒体处理任务
- 分析服务：事件跟踪、指标收集、报表生成
- 通知服务：通知管理、通知模板、通知偏好、多渠道通知
- API网关：请求路由、过滤器、错误处理、服务客户端
- 插件系统：插件API、插件管理器、插件加载机制、示例插件

## 部署

系统支持Docker容器化部署，详细的部署步骤请参考 [部署文档](DEPLOYMENT.md)。

## 开发环境设置

### 前提条件

- JDK 17+
- Kotlin 1.9.0+
- Docker & Docker Compose (用于本地开发)
- IDE: IntelliJ IDEA (推荐)

### 本地开发

1. 克隆仓库：

```bash
git clone https://github.com/yourusername/kace.git
cd kace/server
```

2. 启动本地开发环境：

```bash
docker-compose -f docker-compose.dev.yml up -d
```

3. 构建项目：

```bash
./gradlew build
```

4. 运行特定服务（例如，认证服务）：

```bash
./gradlew :auth-service:run
```

## 项目结构

项目采用领域驱动设计(DDD)和清晰架构原则，每个微服务的结构如下：

```
service-name/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── kace/
│       │           └── servicename/
│       │               ├── api/
│       │               │   ├── controller/
│       │               │   ├── dto/
│       │               │   └── route/
│       │               ├── domain/
│       │               │   ├── model/
│       │               │   ├── repository/
│       │               │   └── service/
│       │               └── infrastructure/
│       │                   ├── config/
│       │                   ├── persistence/
│       │                   │   ├── entity/
│       │                   │   └── repository/
│       │                   └── service/
│       └── resources/
│           ├── application.conf
│           └── db/
│               └── migration/
└── build.gradle.kts
```

## 插件系统

KAce支持通过插件系统扩展平台功能。插件API提供了标准接口，允许开发者创建自定义插件。目前已实现的示例插件包括：

- **文章插件**: 提供文章管理功能
- **产品插件**: 提供产品管理功能

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 许可证

[MIT License](LICENSE) 