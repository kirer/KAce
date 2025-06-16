# KAce 媒体服务 (Media Service)

## 概述

媒体服务是KAce平台的专用媒体资源管理模块，负责处理所有类型的媒体文件，包括图片、音频、视频和文档等。该服务提供从上传、处理、存储到分发的完整媒体生命周期管理，并支持多种存储策略和CDN集成，确保媒体资源的高效交付。

## 核心功能

### 1. 媒体库管理
- 多类型媒体文件上传和管理
- 媒体文件元数据维护
- 媒体分类和标签管理
- 媒体集合和专辑功能

### 2. 媒体处理服务
- 图像处理（裁剪、缩放、水印等）
- 视频处理（转码、压缩、缩略图提取）
- 音频处理（格式转换、音质优化）
- 文档预览生成

### 3. CDN集成
- 云存储桶集成（S3、OSS等）
- CDN分发策略管理
- 文件URL签名和加密
- 内容分发网络优化

### 4. 媒体权限控制
- 基于角色的访问控制
- 媒体可见性设置
- 媒体使用权限管理
- 敏感内容保护

### 5. 高级功能
- 智能内容分析（图像识别、语音转文本）
- 媒体内容检索（颜色、形状、音频特征）
- 视频流处理和直播支持
- 数字水印和版权保护

## 技术实现

- **语言与框架**: Kotlin + Ktor
- **数据持久化**: PostgreSQL + JPA/Hibernate
- **对象存储**: MinIO / AWS S3 兼容存储
- **媒体处理**: FFmpeg, ImageMagick
- **CDN集成**: CloudFront / Akamai / 自定义CDN
- **API风格**: RESTful API
- **依赖注入**: Koin

## API端点

### 媒体上传
- `POST /api/v1/media/upload`: 上传媒体文件
- `POST /api/v1/media/upload/chunked`: 分块上传大文件
- `POST /api/v1/media/upload/url`: 通过URL导入媒体

### 媒体管理
- `GET /api/v1/media`: 获取媒体列表
- `GET /api/v1/media/{id}`: 获取特定媒体信息
- `PUT /api/v1/media/{id}`: 更新媒体信息
- `DELETE /api/v1/media/{id}`: 删除媒体

### 媒体处理
- `POST /api/v1/media/{id}/process/resize`: 调整图像大小
- `POST /api/v1/media/{id}/process/crop`: 裁剪图像
- `POST /api/v1/media/{id}/process/transcode`: 转码视频
- `POST /api/v1/media/{id}/process/thumbnail`: 生成缩略图

### 媒体获取
- `GET /api/v1/media/{id}/content`: 获取媒体内容
- `GET /api/v1/media/{id}/download`: 下载媒体
- `GET /api/v1/media/{id}/share`: 生成分享链接

### 媒体库管理
- `GET /api/v1/media/library`: 获取媒体库信息
- `POST /api/v1/media/library/organize`: 整理媒体库
- `GET /api/v1/media/library/stats`: 获取媒体库统计信息

## 数据模型

主要领域模型包括：
- Media: 媒体文件基本信息
- MediaMetadata: 媒体文件元数据
- MediaProcessingTask: 媒体处理任务
- MediaLibrary: 媒体库
- MediaCollection: 媒体集合
- MediaPermission: 媒体访问权限
- MediaDistribution: 媒体分发配置

## 配置选项

服务配置通过`application.conf`文件提供，主要包括：
- 服务器端口设置
- 数据库连接参数
- 存储策略配置（本地/S3/OSS等）
- 媒体处理工具路径配置
- CDN配置参数
- 上传限制设置（文件大小、类型等）
- 缓存策略配置

## 安装与部署

1. 确保已安装JDK 11或更高版本
2. 配置PostgreSQL数据库
3. 安装FFmpeg和ImageMagick等媒体处理工具
4. 配置对象存储服务（MinIO/S3等）
5. 修改`application.conf`中的相关配置参数
6. 运行`./gradlew build`编译项目
7. 通过`java -jar build/libs/service-media.jar`启动服务

## 集成测试

运行集成测试：
```bash
./gradlew test
```

## 依赖服务

- 用户服务：用于权限验证和用户信息获取
- 系统服务：系统配置和日志记录
- 通知服务：用于媒体处理完成通知 