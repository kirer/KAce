# KAce 网关服务 (Gateway Service)

## 概述

网关服务是KAce平台的统一入口点，负责管理和路由所有外部请求到对应的微服务。作为系统的边界守护者，网关提供了请求路由、负载均衡、认证授权、限流限速、监控和日志记录等关键功能，同时简化了客户端与后端微服务之间的交互。

## 核心功能

### 1. 请求路由
- 动态路由配置
- 路径重写和URL重映射
- 服务发现集成
- 请求转发和负载均衡

### 2. 安全控制
- 令牌验证与授权
- 请求签名验证
- CORS跨域支持
- SSL终止和HTTPS强制

### 3. 流量控制
- 请求限流与限速
- 熔断机制
- 超时控制
- 重试策略

### 4. 请求处理
- 请求头修改与标准化
- 响应转换和格式化
- 请求体和响应体转换
- 错误统一处理

### 5. 监控与分析
- 请求日志记录
- 性能指标收集
- 服务健康检查
- 异常和告警通知

### 6. API文档
- Swagger/OpenAPI集成
- API版本管理
- 文档自动化生成
- API测试控制台

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **路由机制**: Ktor Routing
- **认证集成**: JWT Auth
- **服务发现**: 自定义服务注册
- **缓存**: Redis
- **监控**: Prometheus + Grafana
- **日志**: Logback + ELK Stack
- **依赖注入**: Koin

## API端点

### 网关管理
- `GET /api/admin/gateway/routes`: 获取当前路由配置
- `POST /api/admin/gateway/routes`: 添加路由配置
- `PUT /api/admin/gateway/routes/{id}`: 更新路由配置
- `DELETE /api/admin/gateway/routes/{id}`: 删除路由配置
- `GET /api/admin/gateway/stats`: 获取网关统计数据

### 健康检查
- `GET /health`: 网关健康状态检查
- `GET /health/services`: 所有微服务健康状态

### API文档
- `GET /api-docs`: API文档入口
- `GET /api-docs/{service}`: 特定服务API文档

## 路由配置

网关通过配置文件定义路由规则，主要包括：
- 路径模式和目标服务
- 请求方法限制
- 鉴权要求
- 限流规则
- 路径重写规则

路由配置示例:
```hocon
routes = [
  {
    path = "/api/v1/auth/**"
    targetService = "service-auth"
    stripPrefix = true
    requireAuth = false
    rateLimit {
      requests = 60
      perSeconds = 60
    }
  },
  {
    path = "/api/v1/users/**"
    targetService = "service-user"
    stripPrefix = false
    requireAuth = true
    methods = ["GET", "POST", "PUT", "DELETE"]
  }
]
```

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 路由定义和映射
- 认证服务配置
- 限流和熔断参数
- CORS配置
- 日志级别和格式
- 监控集成配置

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置Redis服务(可选，用于缓存和限流)
3. 修改`application.conf`中的相关配置参数
4. 运行`./gradlew build`编译项目
5. 通过`java -jar build/libs/service-gateway.jar`启动服务

## 运维考量

### 高可用部署
- 多实例部署支持
- 无状态设计，便于水平扩展
- 健康检查和自动恢复机制
- 负载均衡器前置配置

### 性能优化
- 连接池管理
- 请求缓存策略
- 异步请求处理
- 资源限制与隔离

## 依赖服务

- 认证服务：用于令牌验证
- 用户服务：用于权限检查
- 系统服务：用于配置管理
- 所有业务微服务：作为路由目标 