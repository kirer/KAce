# KAce 认证服务 (Authentication Service)

## 概述

认证服务是KAce平台的核心安全组件，负责处理所有与身份验证和安全令牌管理相关的功能。该服务为平台中的所有其他微服务提供统一的身份验证机制，支持多种认证方式，并通过令牌管理确保系统的安全访问控制。

## 核心功能

### 1. 用户认证
- 用户名密码认证
- 第三方OAuth认证（如Google、GitHub、微信等）
- 双因素认证（2FA）支持
- 单点登录（SSO）整合

### 2. 令牌管理
- JWT令牌生成与验证
- 令牌刷新机制
- 令牌撤销与黑名单管理
- 会话管理与控制

### 3. 安全功能
- 密码策略管理
- 防暴力破解机制
- 登录尝试限制与锁定
- 安全事件记录与审计

### 4. 客户端集成
- API密钥管理
- 客户端应用注册
- OAuth客户端凭证管理
- 多平台SDK支持

### 5. 服务级别身份验证
- 服务间认证
- 微服务安全通信
- API网关集成
- 内部服务权限控制

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **数据持久化**: PostgreSQL + JPA/Hibernate
- **令牌存储**: Redis
- **加密技术**: PBKDF2, BCrypt
- **API风格**: RESTful API
- **协议支持**: OAuth 2.0, OpenID Connect
- **依赖注入**: Koin

## API端点

### 用户认证
- `POST /api/v1/auth/login`: 用户登录获取令牌
- `POST /api/v1/auth/logout`: 用户登出
- `POST /api/v1/auth/refresh`: 刷新访问令牌
- `POST /api/v1/auth/password/reset/request`: 请求密码重置
- `POST /api/v1/auth/password/reset/confirm`: 确认密码重置
- `POST /api/v1/auth/2fa/enable`: 启用双因素认证
- `POST /api/v1/auth/2fa/verify`: 验证双因素认证码

### OAuth认证
- `GET /api/v1/auth/oauth/{provider}`: 初始化OAuth认证流程
- `GET /api/v1/auth/oauth/{provider}/callback`: OAuth认证回调
- `GET /api/v1/auth/oauth/providers`: 获取可用OAuth提供者列表

### 令牌管理
- `GET /api/v1/auth/token/validate`: 验证令牌有效性
- `POST /api/v1/auth/token/revoke`: 撤销特定令牌
- `GET /api/v1/auth/sessions`: 获取当前活动会话
- `DELETE /api/v1/auth/sessions/{id}`: 终止特定会话

### 客户端管理
- `GET /api/v1/auth/clients`: 获取注册客户端列表
- `POST /api/v1/auth/clients`: 注册新客户端
- `GET /api/v1/auth/clients/{id}`: 获取特定客户端信息
- `PUT /api/v1/auth/clients/{id}`: 更新客户端信息
- `DELETE /api/v1/auth/clients/{id}`: 删除客户端

## 数据模型

主要领域模型包括：
- AuthToken: 认证令牌信息
- OAuth2Client: OAuth客户端应用
- UserCredential: 用户凭证
- AuthSession: 认证会话
- TwoFactorAuth: 双因素认证配置
- LoginAttempt: 登录尝试记录
- TokenBlacklist: 令牌黑名单

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 数据库连接参数
- Redis连接配置
- JWT签名密钥和算法
- 令牌有效期设置
- 密码策略配置
- OAuth提供者配置
- 安全限制与锁定策略

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置PostgreSQL数据库
3. 安装Redis服务
4. 生成JWT签名密钥
5. 修改`application.conf`中的相关配置参数
6. 运行`./gradlew build`编译项目
7. 通过`java -jar build/libs/service-auth.jar`启动服务

## 集成测试

运行集成测试：
```bash
./gradlew test
```

## 依赖服务

- 用户服务：用于获取用户信息和验证用户存在性
- 系统服务：用于系统配置和日志记录
- 通知服务：用于发送安全通知和警报 