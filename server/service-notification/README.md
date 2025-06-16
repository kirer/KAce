# KAce 通知服务 (Notification Service)

## 概述

通知服务是KAce平台的关键系统组件，负责管理和分发各类通知和消息。该服务通过多种通道（包括邮件、短信、应用内通知、WebSocket实时推送等）向用户传递重要信息，确保用户能够及时获取系统状态更新、应用事件、协作活动以及其他重要通知。

## 核心功能

### 1. 通知管理
- 通知创建和调度
- 通知状态管理和追踪
- 批量通知处理
- 通知优先级和过期策略

### 2. 多通道分发
- 电子邮件通知
- 短信和移动推送通知
- 应用内通知
- WebSocket实时通知
- 第三方消息平台集成(如企业微信、钉钉等)

### 3. 通知模板
- 多语言模板支持
- 模板变量和个性化
- 富文本和HTML邮件模板
- 模板版本控制

### 4. 通知偏好
- 用户订阅和退订管理
- 基于角色的通知过滤
- 通知频率控制
- 免打扰时段设置

### 5. 通知统计和分析
- 通知发送统计
- 通知阅读率分析
- 通知效果评估
- 系统负载监控

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **数据持久化**: PostgreSQL + JPA/Hibernate
- **消息队列**: RabbitMQ / Kafka
- **邮件服务**: SMTP / SendGrid / AWS SES
- **缓存**: Redis
- **WebSocket**: Ktor WebSocket
- **API风格**: RESTful API
- **依赖注入**: Koin

## API端点

### 通知管理
- `GET /api/v1/notifications`: 获取通知列表
- `GET /api/v1/notifications/{id}`: 获取特定通知
- `POST /api/v1/notifications`: 创建新通知
- `DELETE /api/v1/notifications/{id}`: 删除通知
- `POST /api/v1/notifications/{id}/mark-read`: 标记通知为已读
- `POST /api/v1/notifications/mark-all-read`: 标记所有通知为已读

### 通知模板
- `GET /api/v1/notification-templates`: 获取模板列表
- `GET /api/v1/notification-templates/{id}`: 获取特定模板
- `POST /api/v1/notification-templates`: 创建新模板
- `PUT /api/v1/notification-templates/{id}`: 更新模板
- `DELETE /api/v1/notification-templates/{id}`: 删除模板
- `POST /api/v1/notification-templates/{id}/test`: 测试发送模板

### 通知偏好
- `GET /api/v1/notification-preferences`: 获取通知偏好
- `PUT /api/v1/notification-preferences`: 更新通知偏好
- `GET /api/v1/notification-preferences/channels`: 获取可用通知渠道
- `PUT /api/v1/notification-preferences/do-not-disturb`: 设置免打扰时段

### WebSocket API
- `WSS /api/v1/ws/notifications`: 实时通知WebSocket端点

## 数据模型

主要领域模型包括：
- Notification: 通知基本信息
- NotificationTemplate: 通知模板
- NotificationChannel: 通知渠道
- NotificationPreference: 用户通知偏好
- NotificationStatus: 通知状态跟踪
- NotificationBatch: 批量通知
- NotificationStatistic: 通知统计数据

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 数据库连接参数
- 消息队列连接配置
- 邮件服务器配置
- 短信服务配置
- 推送通知配置
- WebSocket配置
- 通知默认设置
- 调度和批处理配置

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置PostgreSQL数据库
3. 安装RabbitMQ或Kafka
4. 配置Redis缓存
5. 配置邮件服务器或第三方邮件服务
6. 修改`application.conf`中的相关配置参数
7. 运行`./gradlew build`编译项目
8. 通过`java -jar build/libs/service-notification.jar`启动服务

## 集成测试

运行集成测试：
```bash
./gradlew test
```

## 依赖服务

- 用户服务：用于获取用户联系信息和偏好
- 系统服务：用于获取系统级配置和日志记录
- 内容服务：用于填充通知内容 