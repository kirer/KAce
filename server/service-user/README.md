# KAce 用户服务 (User Service)

## 概述

用户服务是KAce平台的核心服务之一，负责用户账户管理、认证授权、用户资料管理以及用户偏好设置等功能。该服务为平台其他模块提供统一的用户身份验证和授权机制，确保系统安全性和用户数据隔离。

## 核心功能

### 1. 用户管理
- 用户注册、登录和注销
- 用户账户的CRUD操作
- 用户状态管理(激活、停用、删除等)
- 用户个人资料管理

### 2. 认证与授权
- 基于JWT的用户认证
- 密码重置和账户恢复
- 令牌刷新和会话管理
- 权限验证和资源访问控制

### 3. 角色和权限管理
- 基于RBAC的角色权限模型
- 角色分配和权限分配
- 动态权限验证
- 资源访问控制

### 4. 组织管理
- 组织结构定义和管理
- 用户与组织关联
- 基于组织的访问控制
- 组织层级关系维护

### 5. 用户偏好设置
- 用户个性化配置存储
- 偏好设置的分类管理
- 偏好设置的导入导出
- 用户界面和行为配置

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **数据持久化**: PostgreSQL + JPA/Hibernate
- **认证机制**: JWT (JSON Web Tokens)
- **API风格**: RESTful API
- **依赖注入**: Koin

## API端点

### 用户认证相关
- `POST /api/v1/auth/login`: 用户登录
- `POST /api/v1/auth/logout`: 用户登出
- `POST /api/v1/auth/refresh`: 刷新认证令牌
- `GET /api/v1/auth/me`: 获取当前用户信息
- `POST /api/v1/auth/password/change`: 修改密码
- `POST /api/v1/auth/password/reset/request`: 请求重置密码
- `POST /api/v1/auth/password/reset/complete`: 完成密码重置

### 用户管理相关
- `GET /api/v1/users`: 获取用户列表
- `GET /api/v1/users/{id}`: 获取特定用户信息
- `POST /api/v1/users`: 创建新用户
- `PUT /api/v1/users/{id}`: 更新用户信息
- `DELETE /api/v1/users/{id}`: 删除用户

### 用户偏好设置相关
- `GET /api/v1/user/preferences`: 获取所有偏好设置
- `GET /api/v1/user/preferences/{key}`: 获取特定偏好设置
- `POST /api/v1/user/preferences`: 设置偏好
- `DELETE /api/v1/user/preferences/{key}`: 删除偏好
- `GET /api/v1/user/preferences/export`: 导出偏好设置
- `POST /api/v1/user/preferences/import`: 导入偏好设置

## 数据模型

主要领域模型包括：
- User: 用户基本信息
- UserProfile: 用户详细资料
- Role: 角色定义
- Permission: 权限定义
- Organization: 组织结构
- UserPreference: 用户偏好设置

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 数据库连接参数
- JWT认证相关配置
- 密码安全策略配置

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置PostgreSQL数据库
3. 修改`application.conf`中的数据库连接参数
4. 运行`./gradlew build`编译项目
5. 通过`java -jar build/libs/service-user.jar`启动服务

## 集成测试

运行集成测试：
```bash
./gradlew test
```

## 依赖服务

- 通知服务：用于发送账户相关通知
- 系统服务：系统配置和日志记录 