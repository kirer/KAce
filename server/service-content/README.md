# KAce 内容服务 (Content Service)

## 概述

内容服务负责KAce平台的所有内容管理功能，包括内容创建、编辑、版本控制、分类、标签管理和内容搜索等。作为平台的核心功能模块之一，内容服务提供灵活的内容类型定义和丰富的内容关联能力，支持各类内容的结构化存储和检索。

## 核心功能

### 1. 内容管理
- 多类型内容创建和编辑
- 富文本、Markdown等多格式支持
- 内容状态管理(草稿、发布、归档等)
- 内容元数据维护

### 2. 版本控制
- 内容修改历史记录
- 版本比较和差异展示
- 版本回滚和恢复功能
- 草稿和发布版本管理

### 3. 内容分类与标签
- 灵活的分类体系定义
- 多层级分类结构支持
- 标签创建和管理
- 基于分类和标签的内容关联

### 4. 内容关系管理
- 内容之间的相互引用
- 父子内容关系维护
- 内容依赖关系管理
- 内容组和集合管理

### 5. 内容权限控制
- 基于角色的访问控制
- 细粒度权限设置
- 内容可见性控制
- 协作编辑权限管理

### 6. 内容评论与反馈
- 评论功能支持
- 内容点赞和评分
- 用户反馈收集
- 内容质量评估

### 7. 内容搜索
- 全文搜索功能
- 基于元数据的高级搜索
- 相关内容推荐
- 搜索结果个性化排序

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **数据持久化**: PostgreSQL + JPA/Hibernate
- **搜索引擎**: Elasticsearch
- **缓存**: Redis
- **API风格**: RESTful API
- **依赖注入**: Koin

## API端点

### 内容类型管理
- `GET /api/v1/content-types`: 获取所有内容类型
- `GET /api/v1/content-types/{id}`: 获取特定内容类型
- `POST /api/v1/content-types`: 创建内容类型
- `PUT /api/v1/content-types/{id}`: 更新内容类型
- `DELETE /api/v1/content-types/{id}`: 删除内容类型

### 内容管理
- `GET /api/v1/contents`: 获取内容列表
- `GET /api/v1/contents/{id}`: 获取特定内容
- `POST /api/v1/contents`: 创建新内容
- `PUT /api/v1/contents/{id}`: 更新内容
- `DELETE /api/v1/contents/{id}`: 删除内容
- `POST /api/v1/contents/{id}/publish`: 发布内容
- `POST /api/v1/contents/{id}/archive`: 归档内容

### 内容版本
- `GET /api/v1/contents/{id}/versions`: 获取内容版本历史
- `GET /api/v1/contents/{id}/versions/{versionId}`: 获取特定版本
- `POST /api/v1/contents/{id}/versions/{versionId}/restore`: 恢复到特定版本

### 分类和标签
- `GET /api/v1/categories`: 获取所有分类
- `POST /api/v1/categories`: 创建分类
- `GET /api/v1/tags`: 获取所有标签
- `POST /api/v1/tags`: 创建标签

### 内容搜索
- `GET /api/v1/search`: 全文搜索接口
- `GET /api/v1/search/advanced`: 高级搜索接口

## 数据模型

主要领域模型包括：
- Content: 内容基本信息
- ContentType: 内容类型定义
- ContentVersion: 内容版本
- Category: 分类
- Tag: 标签
- ContentRelation: 内容关联
- ContentPermission: 内容权限
- ContentComment: 内容评论
- ContentFeedback: 内容反馈

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 数据库连接参数
- Elasticsearch连接配置
- Redis缓存配置
- 内容存储路径配置
- 搜索引擎配置

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置PostgreSQL数据库
3. 安装并配置Elasticsearch
4. 安装并配置Redis
5. 修改`application.conf`中的相关配置参数
6. 运行`./gradlew build`编译项目
7. 通过`java -jar build/libs/service-content.jar`启动服务

## 集成测试

运行集成测试：
```bash
./gradlew test
```

## 依赖服务

- 用户服务：用于权限验证和用户信息获取
- 媒体服务：用于内容中的媒体资源管理
- 通知服务：用于内容变更通知
- 系统服务：系统配置和日志记录 