# KAce 服务端设计文档

## 目录

1. [概述](#概述)
2. [架构设计](#架构设计)
3. [技术选型](#技术选型)
4. [模块划分](#模块划分)
5. [数据库设计](#数据库设计)
6. [API设计](#api设计)
7. [安全设计](#安全设计)
8. [性能优化](#性能优化)
9. [部署方案](#部署方案)
10. [开发规范](#开发规范)
11. [配置管理设计](#配置管理设计)

## 概述

KAce服务端是一个基于Kotlin开发的单体架构应用，提供内容管理、用户权限管理、媒体资源处理等核心功能，支持通过插件扩展更多功能。服务端设计遵循模块化、可配置化原则，支持多种数据库和存储方式，便于部署和扩展。

## 架构设计

### 整体架构

KAce采用简化的分层架构，主要分为以下几层：

1. **控制器层**：处理HTTP请求和响应
2. **服务层**：实现业务逻辑
3. **数据访问层**：处理数据持久化

### 架构模式

KAce采用简化的领域驱动设计，适度借鉴DDD的概念：

1. **业务领域模型**：将业务概念映射为代码模型
2. **事件驱动**：通过事件实现模块间通信

## 技术选型

### 核心技术

- **开发语言**：Kotlin 1.9.x
- **Web框架**：Ktor 2.x
- **依赖注入**：Koin
- **数据库访问**：Exposed + 多种数据库驱动
- **认证授权**：JWT + OAuth 2.0
- **API文档**：OpenAPI/Swagger

### 数据存储

- **关系型数据库**：
  - MySQL 8.0+
  - PostgreSQL 14.0+
  - H2 (开发环境)
- **NoSQL**：
  - MongoDB 5.0+
- **缓存**：
  - Redis 6.x+
  - 内存缓存

### 文件存储

- **本地文件系统**：适合单机部署
- **MinIO**：自托管对象存储，兼容S3 API
- **云存储**：
  - 腾讯云COS
  - 阿里云OSS

### 搜索引擎

- **内置搜索**：基于数据库的简单搜索功能
- **Elasticsearch**：可选集成，提供高级搜索能力

### 消息队列

- **RabbitMQ**：用于异步任务处理、模块间通信等

## 模块划分

### 核心模块

按照系统启动和依赖关系的优先级，核心模块排序如下：

#### 1. 基础服务（优先级：最高）
- **配置管理**
  - 支持多种配置源（文件、环境变量、数据库）
  - 系统级配置管理（插件级配置由各插件自行管理）
  - 动态配置刷新
  - 配置监听机制
  - 敏感信息加密

- **日志服务**
  - 多级日志（DEBUG, INFO, WARN, ERROR）
  - 结构化日志
  - 日志分类（系统日志、业务日志、安全日志）
  - 日志存储策略（文件、数据库）
  - 日志轮转和清理
  - 日志查询接口

- **国际化支持**
  - 多语言资源管理
  - 动态语言切换
  - 插件语言包扩展
  - 语言回退机制
  - 日期、数字、货币格式本地化

- **异常处理**
  - 全局异常拦截
  - 异常分类（系统异常、业务异常、安全异常）
  - 异常日志记录
  - 友好错误响应
  - 异常追踪和关联

- **事件总线**
  - 同步/异步事件分发
  - 事件订阅机制
  - 事件过滤和拦截
  - 事件持久化选项
  - 分布式事件支持

#### 2. 插件系统核心（优先级：高）
- **插件生命周期管理**
  - 插件发现与加载
  - 初始化顺序控制
  - 启动和停止插件
  - 插件热更新
  - 插件健康检查

- **插件注册与发现**
  - 插件元数据管理
  - 插件仓库
  - 插件搜索和查询
  - 插件状态监控
  - 插件版本管理

- **插件依赖管理**
  - 依赖关系解析
  - 循环依赖检测
  - 可选依赖支持
  - 依赖冲突解决
  - 版本兼容性检查

- **扩展点机制**
  - 标准扩展点定义
  - 扩展实现注册
  - 扩展点优先级
  - 条件化扩展
  - 扩展点调用链

- **插件安全沙箱**
  - 资源隔离
  - API访问控制
  - 插件资源限制
  - 插件行为监控
  - 恶意插件检测

#### 3. API框架（优先级：高）
- **路由注册机制**
  - 声明式路由定义
  - 路由分组和嵌套
  - 前缀和中间件支持
  - 版本化路由
  - 插件路由自动注册

- **请求处理**
  - 请求验证和转换
  - 内容协商（JSON, XML, etc）
  - 文件上传处理
  - 参数绑定和转换
  - 请求限流和防抖

- **响应格式化**
  - 统一响应包装
  - 错误码标准化
  - 国际化错误消息
  - 内容类型协商
  - 分页结果格式化

- **中间件链**
  - 认证中间件
  - 权限检查中间件
  - 请求日志中间件
  - 性能监控中间件
  - 跨域处理中间件
  - 缓存中间件

- **API文档**
  - OpenAPI规范生成
  - 接口文档自动更新
  - 在线接口测试
  - 文档版本控制
  - 接口变更检测

#### 4. 用户角色权限系统（优先级：中高）
- **用户管理**
  - 用户注册和激活
  - 用户信息维护
  - 用户状态管理（启用、禁用、锁定）
  - 用户组织架构
  - 用户偏好设置
  - 用户元数据扩展

- **角色管理**
  - 角色定义和分类
  - 角色层级结构
  - 动态角色分配
  - 角色模板
  - 角色有效期
  - 角色约束条件

- **权限管理**
  - 权限定义和分类
  - 权限继承关系
  - 动态权限分配
  - 权限策略（白名单/黑名单）
  - 权限作用域
  - 临时权限授予

- **身份认证**
  - 多种认证方式（密码、OAuth、LDAP）
  - 多因素认证
  - 记住登录状态
  - 单点登录支持
  - 认证事件记录
  - 账户恢复和重置

- **会话管理**
  - 会话创建和维护
  - 会话状态跟踪
  - 会话超时控制
  - 并发会话管理
  - 强制会话终止
  - 会话劫持防护

- **安全审计**
  - 用户操作日志
  - 权限变更审计
  - 敏感操作记录
  - 登录尝试监控
  - 异常行为检测
  - 审计报告生成

#### 5. 菜单系统（优先级：中）
- **菜单结构管理**
  - 树形菜单定义
  - 菜单项排序和分组
  - 动态菜单生成
  - 菜单状态（启用/禁用）
  - 菜单缓存

- **菜单权限集成**
  - 基于角色的菜单过滤
  - 菜单项权限检查
  - 动态权限适配
  - 菜单访问控制

- **菜单扩展性**
  - 插件菜单注册
  - 菜单钩子点
  - 自定义菜单渲染
  - 外部链接支持
  - 菜单项参数化

- **菜单国际化**
  - 多语言菜单项
  - 动态语言切换
  - 本地化菜单资源
  - 语言回退规则

- **菜单UI适配**
  - 响应式菜单布局
  - 多主题支持
  - 自定义图标
  - 菜单徽章和通知
  - 折叠/展开状态保持

#### 6. 媒体资源系统（优先级：中低）
- **文件上传下载**
  - 多文件批量上传
  - 大文件分片上传
  - 断点续传支持
  - 下载速率控制
  - 文件预览生成

- **图片处理**
  - 图片压缩和优化
  - 图片格式转换
  - 图片尺寸调整
  - 图片裁剪和旋转
  - 水印添加
  - 图片元数据提取

- **媒体资源管理**
  - 文件夹层次结构
  - 标签和分类管理
  - 资源搜索和过滤
  - 资源版本控制
  - 资源关联关系
  - 资源回收站

- **存储策略管理**
  - 多存储提供商支持
  - 存储策略配置
  - 自动存储选择规则
  - 资源迁移工具
  - 存储容量监控
  - 存储成本优化

- **媒体元数据**
  - 元数据提取和索引
  - 自定义元数据字段
  - 元数据搜索
  - 元数据国际化
  - 元数据编辑历史

- **媒体处理管道**
  - 自定义处理流程
  - 处理任务队列
  - 处理进度跟踪
  - 处理结果通知
  - 批量处理优化

### 插件模块

系统通过插件实现各类业务功能，每个插件都有完整的独立性：

#### 插件特性
- **独立的数据模型**：每个插件定义和管理自己的数据结构
- **独立的API路由**：每个插件注册自己的API端点
- **独立的权限点**：每个插件定义自己的权限控制点
- **独立的菜单项**：插件可以向系统菜单注册自己的菜单项
- **独立的业务逻辑**：插件内封装完整的业务功能
- **独立的UI组件**：可选提供前端界面组件

#### 基础插件示例

1. **表单构建插件**
   - 表单设计器
   - 表单渲染
   - 数据收集
   - 数据导出

2. **数据分析插件**
   - 数据收集
   - 统计分析
   - 报表生成
   - 数据可视化

3. **API集成插件**
   - API连接管理
   - 数据转换
   - 同步配置
   - 请求日志

4. **通知系统插件**
   - 消息模板
   - 发送通道管理
   - 通知记录
   - 通知偏好设置

### 插件系统设计

#### 插件接口
系统提供标准接口供插件实现：
```kotlin
interface Plugin {
    // 插件基本信息
    val id: String
    val name: String
    val version: String
    val description: String
    
    // 依赖其他插件
    val dependencies: List<PluginDependency>
    
    // 生命周期方法
    fun initialize()
    fun onEnable()
    fun onDisable()
    
    // 注册功能
    fun registerDataModels()
    fun registerRoutes(routeRegistry: RouteRegistry)
    fun registerPermissions(permissionRegistry: PermissionRegistry)
    fun registerMenuItems(menuRegistry: MenuRegistry)
    fun registerServices(serviceRegistry: ServiceRegistry)
    
    // 获取插件提供的服务
    fun getService(serviceClass: Class<*>): Any?
}
```

#### 插件描述文件
每个插件都包含plugin.json描述文件：
```json
{
  "id": "article-plugin",
  "name": "文章管理插件",
  "version": "1.0.0",
  "description": "提供文章的创建、编辑、发布等功能",
  "main": "com.kace.plugins.article.ArticlePlugin",
  "dependencies": [],
  "permissions": [
    "article:create",
    "article:read",
    "article:update",
    "article:delete"
  ]
}
```

#### 插件实现示例
```kotlin
// 通知系统插件实现
class NotificationPlugin : Plugin {
    override val id = "notification-plugin"
    override val name = "通知系统插件"
    override val version = "1.0.0"
    override val description = "提供多渠道消息通知功能，包括模板管理、发送记录等"
    override val dependencies = listOf()
    
    override fun initialize() {
        // 初始化插件资源
    }
    
    override fun registerDataModels() {
        // 注册通知相关数据模型
        dataModelRegistry.register(NotificationTemplate::class)
        dataModelRegistry.register(NotificationChannel::class)
        dataModelRegistry.register(NotificationRecord::class)
        dataModelRegistry.register(UserNotificationPreference::class)
    }
    
    override fun registerRoutes(routeRegistry: RouteRegistry) {
        // 注册API路由
        routeRegistry.route {
            path("/api/notifications")
            get { handleGetNotifications() }
            post { handleSendNotification() }
            
            path("/api/notifications/{id}") {
                get { handleGetNotification() }
                delete { handleDeleteNotification() }
            }
            
            path("/api/notification-templates") {
                get { handleGetTemplates() }
                post { handleCreateTemplate() }
                
                path("/api/notification-templates/{id}") {
                    get { handleGetTemplate() }
                    put { handleUpdateTemplate() }
                    delete { handleDeleteTemplate() }
                }
            }
            
            path("/api/notification-channels") {
                get { handleGetChannels() }
                post { handleCreateChannel() }
                
                path("/api/notification-channels/{id}") {
                    get { handleGetChannel() }
                    put { handleUpdateChannel() }
                    delete { handleDeleteChannel() }
                }
            }
            
            path("/api/notification-preferences") {
                get { handleGetPreferences() }
                put { handleUpdatePreferences() }
            }
        }
    }
    
    override fun registerPermissions(permissionRegistry: PermissionRegistry) {
        // 注册权限点
        permissionRegistry.register("notification:send", "发送通知")
        permissionRegistry.register("notification:read", "查看通知")
        permissionRegistry.register("notification:delete", "删除通知")
        permissionRegistry.register("notification:template:manage", "管理通知模板")
        permissionRegistry.register("notification:channel:manage", "管理通知渠道")
        permissionRegistry.register("notification:preference:manage", "管理通知偏好设置")
    }
    
    override fun registerMenuItems(menuRegistry: MenuRegistry) {
        // 注册通知管理作为一级菜单
        menuRegistry.register(
            MenuItem(
                id = "notification-management",
                name = "通知管理",
                icon = "bell",
                order = 400, // 在媒体库之后
                permission = "notification:access",
                children = listOf(
                    MenuItem(
                        id = "notification-templates",
                        name = "通知模板",
                        icon = "template",
                        route = "/notifications/templates",
                        permission = "notification:template:manage"
                    ),
                    MenuItem(
                        id = "notification-channels",
                        name = "通知渠道",
                        icon = "share",
                        route = "/notifications/channels",
                        permission = "notification:channel:manage"
                    )
                )
            )
        )
        
        // 在系统设置下添加通知设置子菜单
        menuRegistry.register(
            MenuItem(
                id = "notification-settings",
                name = "通知设置",
                icon = "settings",
                route = "/settings/notifications",
                permission = "notification:settings:view",
                parentId = "system-settings" // 添加到系统设置下
            )
        )
    }
}
```

## 数据库设计

### 数据库设计原则

系统数据库设计遵循以下原则：

1. **核心表**：系统包含用户权限、媒体资源、菜单系统等核心功能的表
2. **插件表**：插件定义和管理自己的业务数据表
3. **表前缀**：插件表使用插件ID作为表名前缀，避免冲突
4. **独立管理**：插件负责自己的表结构版本管理和迁移

### 数据库配置

数据库连接配置示例：

```hocon
database {
  driver = "mysql"  # 可选: mysql, postgresql, mongodb, h2
  host = "localhost"
  port = 3306
  database = "kace"
  username = "kace_user"
  password = "password"
}
```

### 核心数据表

#### 用户与权限相关表
```
user                - 用户表
role                - 角色表
permission          - 权限表
user_role           - 用户角色关联表
role_permission     - 角色权限关联表
user_session        - 用户会话表
auth_log            - 认证日志表
```

#### 媒体资源相关表
```
media_asset         - 媒体资源表
media_folder        - 媒体文件夹表
media_metadata      - 媒体元数据表
```

#### 插件系统相关表
- **plugin**：插件基本信息
- **plugin_status**：插件状态和配置
- **plugin_dependency**：插件依赖关系

#### 菜单相关表
```
menu_item           - 菜单项表
menu_item_i18n      - 菜单项多语言表
```

### 插件数据表示例

每个插件定义和管理自己的数据表，使用插件ID作为表前缀：

#### 通知系统插件表
```
notification_template    - 通知模板表
notification_channel     - 通知渠道表
notification_record      - 通知记录表
notification_preference  - 用户通知偏好表
notification_attachment  - 通知附件表
```

## API设计

### API路由注册

系统提供API路由注册机制，每个插件注册自己的API路由：

```kotlin
routeRegistry.route {
    // 插件路由前缀
    path("/api/plugin-id") {
        // 具体API端点
        get("/resources") { ... }
        post("/resources") { ... }
        
        path("/resources/{id}") {
            get { ... }
            put { ... }
            delete { ... }
        }
    }
}
```

### 权限检查

API端点可以集成权限检查：

```kotlin
routeRegistry.route {
    path("/api/articles") {
        // 使用权限检查中间件
        get(withPermission("article:read")) { handleGetArticles() }
        post(withPermission("article:create")) { handleCreateArticle() }
    }
}
```

### API响应格式

标准响应格式：
```json
{
  "success": true,
  "data": {},
  "message": "操作成功"
}
```

错误响应格式：
```json
{
  "success": false,
  "message": "请求的资源不存在",
  "code": 404
}
```

## 安全设计

### 认证授权

- 基础认证框架由系统提供
- 具体认证和授权实现由插件提供
- 权限检查API允许插件间调用

### 插件安全机制

- 插件沙箱：限制插件访问系统资源
- 插件签名：验证插件来源
- 插件权限：控制插件能力范围

## 性能优化

- 插件延迟加载：按需加载插件
- 依赖注入优化：减少不必要的依赖
- 数据库连接池：优化数据库访问

## 部署方案

### 开发环境

- 内嵌服务器
- H2内存数据库
- 热加载插件

### 生产环境

- Docker容器部署
- Docker Compose编排
- 生产数据库
- 预加载核心插件

## 开发规范

### 编码规范
- Kotlin官方代码风格
- 简单清晰的命名

### 测试规范
- 单元测试：JUnit
- API测试：基本功能测试

### 文档规范
- 代码注释
- API文档
- 开发文档

### 版本控制

- Git版本管理
- 基本的分支策略 

## 系统初始化配置

### 基础菜单结构

系统初始化时，将自动创建以下基础菜单结构：

```
- 控制台 (仪表盘)
  - 系统概览
  - 个人工作台

- 用户管理
  - 角色管理（分配菜单，API权限）
  - 用户列表（分配角色）

- 菜单管理
  - 菜单列表

- 插件管理
  - 已安装插件列表（插件配置）
  - 插件市场

- 媒体库 (媒体资源系统)
  - 资源库
  - 资源分类
  - 存储设置

- 系统设置
  - 基本设置
  - 安全设置
  - 系统日志
```

每个菜单项都有关联的权限点和默认分配给系统管理员角色的权限。

### 基础权限点

系统初始化时，将创建以下基础权限点：

#### 系统核心权限

```
- system:dashboard:view        - 查看控制台
- system:settings:view         - 查看系统设置
- system:settings:edit         - 编辑系统设置
- system:logs:view             - 查看系统日志
- system:maintenance:perform   - 执行系统维护操作
```

#### 插件管理权限

```
- plugin:view                  - 查看插件
- plugin:install               - 安装插件
- plugin:uninstall             - 卸载插件
- plugin:enable                - 启用插件
- plugin:disable               - 禁用插件
- plugin:configure             - 配置插件
- plugin:develop               - 开发插件
```

#### 菜单管理权限

```
- menu:view                    - 查看菜单
- menu:create                  - 创建菜单
- menu:edit                    - 编辑菜单
- menu:delete                  - 删除菜单
- menu:order                   - 调整菜单顺序
- menu:permission              - 管理菜单权限
```

#### 用户管理权限

```
- user:create                  - 创建用户
- user:read                    - 查看用户
- user:update                  - 更新用户
- user:delete                  - 删除用户
- user:import                  - 导入用户
- user:export                  - 导出用户
- user:disable                 - 禁用用户
- user:reset-password          - 重置用户密码
```

#### 角色权限管理

```
- role:create                  - 创建角色
- role:read                    - 查看角色
- role:update                  - 更新角色
- role:delete                  - 删除角色
- role:assign                  - 分配角色给用户
- role:permission:assign       - 分配权限给角色
- role:permission:revoke       - 撤销角色权限
```

#### 媒体资源权限

```
- media:upload                 - 上传媒体资源
- media:download               - 下载媒体资源
- media:read                   - 查看媒体资源
- media:edit                   - 编辑媒体资源
- media:delete                 - 删除媒体资源
- media:folder:create          - 创建媒体文件夹
- media:folder:delete          - 删除媒体文件夹
- media:storage:configure      - 配置存储设置
```

### 初始角色

系统初始化时，将创建以下角色：

1. **系统管理员**
   - 拥有所有权限
   - 可管理所有系统功能
   - 不可删除

2. **媒体管理员**
   - 拥有媒体资源管理权限
   - 可管理媒体库的所有功能
   - 无系统核心管理权限

3. **普通用户**
   - 基本的查看权限
   - 个人资料管理权限
   - 上传自己的媒体资源

### 插件菜单集成

当安装并启用新插件时，插件可以向系统菜单注册自己的菜单项：

```kotlin
// 插件菜单注册示例
override fun registerMenuItems(menuRegistry: MenuRegistry) {
    // 在"系统设置"下添加子菜单
    menuRegistry.register(
        MenuItem(
            id = "email-settings",
            name = "邮件设置",
            icon = "mail",
            route = "/settings/email",
            permission = "email:settings:view",
            parentId = "system-settings" // 添加到系统设置下
        )
    )
    
    // 添加一级菜单及其子菜单
    menuRegistry.register(
        MenuItem(
            id = "my-plugin-menu",
            name = "我的插件",
            icon = "plugin-icon",
            order = 500, // 控制显示顺序
            permission = "my-plugin:access",
            children = listOf(
                MenuItem(
                    id = "my-plugin-dashboard",
                    name = "插件概览",
                    icon = "dashboard",
                    route = "/my-plugin/dashboard",
                    permission = "my-plugin:dashboard:view"
                ),
                MenuItem(
                    id = "my-plugin-settings",
                    name = "插件设置",
                    icon = "settings",
                    route = "/my-plugin/settings",
                    permission = "my-plugin:settings:view"
                )
            )
        )
    )
}
```

### 系统初始化流程

系统启动时，初始化流程如下：

1. 加载系统配置
2. 初始化数据库连接
3. 创建基础数据表（如不存在）
4. 检查并创建初始权限点
5. 检查并创建初始角色
6. 检查并创建系统菜单
7. 创建默认管理员用户（如首次启动）
8. 加载并初始化核心插件
9. 启动系统服务和API端点

此初始化流程确保系统在首次启动时具有基本的功能结构，可以立即进行管理操作。

## 菜单系统设计

### 菜单系统作为基础服务

菜单系统是KAce框架的基础服务之一，而非插件。它提供了全局的菜单注册和管理机制，允许各个插件向其中注册自己的菜单项。作为基础服务，菜单系统具有以下特点：

1. **全局可用**：所有插件都可以访问菜单服务
2. **统一管理**：菜单结构在系统级别集中管理
3. **权限集成**：与基础权限系统紧密集成
4. **持久化**：菜单配置存储在系统数据库中

### 菜单数据模型

系统提供了一个灵活的菜单数据模型，支持多级嵌套菜单：

```kotlin
data class MenuItem(
    val id: String,                 // 菜单项唯一标识
    val name: String,               // 菜单显示名称
    val icon: String? = null,       // 菜单图标
    val route: String? = null,      // 关联的路由路径
    val externalLink: String? = null, // 外部链接
    val permission: String? = null, // 访问所需权限
    val order: Int = 0,             // 排序值
    val children: List<MenuItem> = emptyList(), // 子菜单项
    val pluginId: String? = null,   // 所属插件ID
    val hidden: Boolean = false,    // 是否在菜单中隐藏
    val divider: Boolean = false,   // 是否显示分隔线
    val badge: String? = null       // 菜单徽章
)
```

### 菜单注册机制

系统提供MenuRegistry接口，允许插件注册自己的菜单项：

```kotlin
interface MenuRegistry {
    // 注册顶级菜单项
    fun register(menuItem: MenuItem)
    
    // 向现有菜单添加子菜单
    fun addChild(parentId: String, childMenuItem: MenuItem)
    
    // 更新现有菜单项
    fun update(menuItemId: String, updater: (MenuItem) -> MenuItem)
    
    // 移除菜单项
    fun remove(menuItemId: String)
}
```

### 菜单权限控制

菜单系统与权限系统集成，可以基于用户权限动态显示菜单：

1. **菜单项权限**：每个菜单项可以关联一个权限点
2. **动态过滤**：根据当前用户的权限，过滤不可访问的菜单项
3. **权限继承**：子菜单可以继承父菜单的权限要求

### 界面元素权限控制

除了控制菜单项的可见性，权限系统还可以控制界面中的具体元素：

1. **按钮权限**：界面上的操作按钮可以与特定权限关联
   ```kotlin
   // Compose UI中的按钮权限控制
   @Composable
   fun ActionButton(permissionRequired: String, onClick: () -> Unit, content: @Composable () -> Unit) {
       val permissionState = rememberPermissionState(permission = permissionRequired)
       
       if (permissionState.hasPermission) {
           Button(onClick = onClick) {
               content()
           }
       }
   }
   
   // 使用示例
   ActionButton(
       permissionRequired = "article:create",
       onClick = { navigateToCreateArticle() }
   ) {
       Text("创建文章")
   }
   ```

2. **数据权限**：控制用户可以看到的数据范围
   ```kotlin
   // 共享业务逻辑中的数据权限过滤
   class ArticleRepository(
       private val apiService: ArticleApiService,
       private val permissionChecker: PermissionChecker
   ) {
       suspend fun getArticles(): List<Article> {
           return when {
               permissionChecker.hasPermission("article:read:all") -> 
                   apiService.getAllArticles()
               permissionChecker.hasPermission("article:read:department") -> {
                   val departmentId = permissionChecker.getCurrentUserDepartmentId()
                   apiService.getArticlesByDepartment(departmentId)
               }
               permissionChecker.hasPermission("article:read:own") -> {
                   val userId = permissionChecker.getCurrentUserId()
                   apiService.getArticlesByAuthor(userId)
               }
               else -> emptyList()
           }
       }
   }
   ```

3. **字段权限**：控制用户可以看到或编辑的字段
   ```kotlin
   // 共享模型中的字段权限处理
   @Serializable
   data class ArticleResponse(
       val id: String,
       val title: String,
       val content: String,
       val author: String,
       val createdAt: Long,
       // 可选字段，根据权限显示
       val viewCount: Int? = null,
       val reviewStatus: String? = null,
       val reviewComment: String? = null
   )
   
   // 字段显示逻辑
   @Composable
   fun ArticleDetails(article: ArticleResponse) {
       val permissionState = rememberPermissionState()
       
       Column {
           Text("标题: ${article.title}")
           Text("作者: ${article.author}")
           Text("内容: ${article.content}")
           
           // 只有拥有统计查看权限的用户才能看到浏览量
           if (permissionState.hasPermission("article:stats:view") && article.viewCount != null) {
               Text("浏览量: ${article.viewCount}")
           }
           
           // 只有拥有审核查看权限的用户才能看到审核信息
           if (permissionState.hasPermission("article:review:view")) {
               article.reviewStatus?.let { 
                   Text("审核状态: $it") 
               }
               article.reviewComment?.let { 
                   Text("审核评论: $it") 
               }
           }
       }
   }
   ```

### 权限命名规范

为了更好地管理权限，系统采用以下权限命名规范：

1. **基本格式**：`资源:操作[:范围]`
   - 资源：表示被操作的对象，如article, user, media
   - 操作：表示对资源的行为，如create, read, update, delete
   - 范围(可选)：表示操作的范围限制，如all, department, own

2. **示例**：
   - `article:create` - 创建文章的权限
   - `article:read:all` - 读取所有文章的权限
   - `article:read:own` - 只读取自己创建的文章的权限
   - `article:update:department` - 更新本部门文章的权限
   - `article:publish` - 发布文章的权限
   - `article:review` - 审核文章的权限

3. **通配符权限**：
   - `article:*` - 文章的所有操作权限
   - `*:read` - 所有资源的读取权限

### 菜单与界面权限示例

下面是在菜单项和具体界面元素中应用权限的Kotlin Multiplatform示例：

```kotlin
// 菜单注册 - 在共享代码中定义
menuRegistry.register(
    MenuItem(
        id = "article-list",
        name = "文章列表",
        route = "/articles",
        permission = "article:read" // 只要有阅读权限就可以访问列表页
    )
)

// 前端界面权限控制 - 使用Compose Multiplatform
@Composable
fun ArticleListScreen(viewModel: ArticleListViewModel = getViewModel()) {
    val articles by viewModel.articles.collectAsState()
    val permissionState = rememberPermissionState()
    
    Column {
        // 顶部操作区
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // 创建按钮 - 只有有创建权限才显示
            if (permissionState.hasPermission("article:create")) {
                Button(onClick = { viewModel.navigateToCreate() }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("创建文章")
                }
            }
        }
        
        // 文章列表
        LazyColumn {
            items(articles) { article ->
                ArticleItem(
                    article = article,
                    onEdit = { 
                        // 编辑按钮 - 需要更新权限
                        if (permissionState.hasPermission("article:update")) {
                            viewModel.navigateToEdit(article.id)
                        }
                    },
                    onDelete = {
                        // 删除按钮 - 需要删除权限
                        if (permissionState.hasPermission("article:delete")) {
                            viewModel.deleteArticle(article.id)
                        }
                    },
                    onPublish = {
                        // 发布按钮 - 需要发布权限
                        if (permissionState.hasPermission("article:publish") && !article.published) {
                            viewModel.publishArticle(article.id)
                        }
                    },
                    canEdit = permissionState.hasPermission("article:update"),
                    canDelete = permissionState.hasPermission("article:delete"),
                    canPublish = permissionState.hasPermission("article:publish") && !article.published
                )
            }
        }
    }
}

// 文章项组件
@Composable
fun ArticleItem(
    article: Article,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPublish: () -> Unit,
    canEdit: Boolean,
    canDelete: Boolean,
    canPublish: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(article.title, style = MaterialTheme.typography.h6)
            Text(article.author, style = MaterialTheme.typography.body2)
            Text("发布于: ${article.formattedDate}", style = MaterialTheme.typography.caption)
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // 根据权限显示不同按钮
                if (canEdit) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
                
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
                
                if (canPublish) {
                    IconButton(onClick = onPublish) {
                        Icon(Icons.Default.Publish, contentDescription = "发布")
                    }
                }
            }
        }
    }
}

// 权限状态管理 - 共享代码
class PermissionState(private val permissionChecker: PermissionChecker) {
    fun hasPermission(permission: String): Boolean {
        return permissionChecker.hasPermission(permission)
    }
}

@Composable
fun rememberPermissionState(): PermissionState {
    val permissionChecker = LocalPermissionChecker.current
    return remember { PermissionState(permissionChecker) }
}
```

### API权限控制

API端点也需要对应的权限控制，确保前端操作有对应的后端权限检查：

```kotlin
// API端点权限控制
routeRegistry.route {
    path("/api/articles") {
        // 列表查询 - 读取权限
        get(withPermission("article:read")) { handleGetArticles() }
        
        // 创建文章 - 创建权限
        post(withPermission("article:create")) { handleCreateArticle() }
    }
    
    path("/api/articles/{id}") {
        // 查看文章详情 - 读取权限
        get(withPermission("article:read")) { handleGetArticle() }
        
        // 更新文章 - 更新权限
        put(withPermission("article:update")) { handleUpdateArticle() }
        
        // 删除文章 - 删除权限
        delete(withPermission("article:delete")) { handleDeleteArticle() }
        
        // 发布文章 - 发布权限
        post("/publish", withPermission("article:publish")) { handlePublishArticle() }
    }
}
```

这种设计允许在菜单系统保持简洁的同时，对页面内的各种操作进行细粒度的权限控制。

### 菜单数据表

菜单相关的数据表包括：

```
menu_item           - 菜单项表
menu_item_i18n      - 菜单项多语言表
```

### 菜单API

系统提供统一的菜单API：

```
GET /api/menus              - 获取当前用户可访问的所有菜单
GET /api/menus/structure    - 获取完整的菜单结构(管理用)
POST /api/menus/items       - 创建菜单项(管理用)
PUT /api/menus/items/{id}   - 更新菜单项(管理用)
DELETE /api/menus/items/{id} - 删除菜单项(管理用)
```

### 前端菜单集成

前端应用可以通过API获取当前用户的菜单结构，并动态生成导航菜单：

1. **应用启动时**：获取用户可访问的菜单结构
2. **动态渲染**：根据菜单数据渲染导航组件
3. **路由集成**：将菜单项与前端路由系统集成
4. **状态保持**：记住展开/折叠状态

### 菜单扩展示例

系统初始菜单结构将会由插件系统自动扩展，示例如下：

```
- 控制台 (仪表盘)
- 用户管理
  - 角色管理（分配菜单，API权限）
  - 用户列表（分配角色）
- 菜单管理
  - 菜单列表
- 插件管理
  - 已安装插件列表（插件配置）
  - 插件市场
- 媒体库 (媒体资源系统)
  - 资源库
  - 资源分类
- 通知管理（通知系统插件）
  - 通知模板
  - 通知渠道
- 系统设置
  - 基本设置
  - 安全设置
  - 系统日志
```

以通知系统插件为例，它会在安装并激活后向系统菜单注册自己的菜单项：

```kotlin
// 通知插件菜单注册示例
override fun registerMenuItems(menuRegistry: MenuRegistry) {
    // 注册通知管理作为一级菜单
    menuRegistry.register(
        MenuItem(
            id = "notification-management",
            name = "通知管理",
            icon = "bell",
            order = 400, // 在媒体库之后
            permission = "notification:access",
            children = listOf(
                MenuItem(
                    id = "notification-templates",
                    name = "通知模板",
                    icon = "template",
                    route = "/notifications/templates",
                    permission = "notification:template:manage"
                ),
                MenuItem(
                    id = "notification-channels",
                    name = "通知渠道",
                    icon = "share",
                    route = "/notifications/channels",
                    permission = "notification:channel:manage"
                )
            )
        )
    )
    
    // 在系统设置下添加通知设置子菜单
    menuRegistry.register(
        MenuItem(
            id = "notification-settings",
            name = "通知设置",
            icon = "settings",
            route = "/settings/notifications",
            permission = "notification:settings:view",
            parentId = "system-settings" // 添加到系统设置下
        )
    )
}
```

这种菜单注册机制使得插件可以灵活地扩展系统菜单，同时保持整体菜单结构的一致性和权限控制的完整性。

## 配置管理设计

### 配置分离原则

KAce采用明确的配置分离原则：

1. **系统级配置**：由核心框架统一管理
2. **插件级配置**：由各插件自行管理

这种分离确保了插件可以独立管理自己的配置，同时减轻了系统配置的复杂性。

### 系统级配置

系统级配置管理以下核心内容：

1. **数据库连接配置**：
   ```hocon
   database {
     driver = "mysql"  # 可选: mysql, postgresql, mongodb, h2
     host = "localhost"
     port = 3306
     database = "kace"
     username = "kace_user"
     password = "password"
   }
   ```

2. **文件存储配置**：
   ```hocon
   storage {
     type = "local"  # 可选: local, minio, cos, oss
     # 本地存储配置
     local {
       root-dir = "./uploads"
     }
     # MinIO配置（如果type=minio）
     minio {
       endpoint = "http://minio:9000"
       access-key = "minioadmin"
       secret-key = "minioadmin"
       bucket = "kace"
     }
   }
   ```

3. **服务器配置**：
   ```hocon
   server {
     host = "0.0.0.0"
     port = 8080
     cors {
       enabled = true
       allowed-origins = ["*"]
     }
   }
   ```

4. **安全配置**：
   ```hocon
   security {
     jwt {
       secret = "${JWT_SECRET}"  # 通过环境变量设置
       expiration = 86400  # 1天，单位秒
     }
     password {
       algorithm = "bcrypt"
       min-length = 8
       require-special-chars = true
     }
   }
   ```

5. **日志配置**：
   ```hocon
   logging {
     level = "INFO"  # 可选: ERROR, WARN, INFO, DEBUG, TRACE
     appenders = ["console", "file"]
     file {
       path = "./logs/kace.log"
       roll-size = "10MB"
       max-history = 10
     }
   }
   ```

6. **缓存配置**：
   ```hocon
   cache {
     type = "memory"  # 可选: memory, redis
     # Redis配置（如果type=redis）
     redis {
       host = "localhost"
       port = 6379
       database = 0
     }
   }
   ```

### 插件配置管理

每个插件负责管理自己的配置：

1. **插件默认配置**：插件应提供默认配置，确保首次安装时可用
2. **插件配置存储**：插件可将配置存储在以下位置
   - 数据库中（推荐）
   - 单独的配置文件
3. **配置UI**：插件应提供配置界面，让管理员可以修改配置
4. **配置验证**：插件应验证配置有效性，提供友好的错误提示

### 配置示例 - 通知插件

```kotlin
// 通知插件配置管理示例
class NotificationPluginConfig(private val configService: ConfigService) {
    // 获取插件配置
    fun getConfig(): NotificationConfig {
        // 从数据库获取配置，如果不存在则返回默认配置
        return configService.getPluginConfig("notification-plugin")
            ?.let { json -> Json.decodeFromString(json) }
            ?: defaultConfig
    }
    
    // 保存插件配置
    fun saveConfig(config: NotificationConfig) {
        // 验证配置
        validateConfig(config)
        // 保存到数据库
        configService.savePluginConfig(
            pluginId = "notification-plugin",
            config = Json.encodeToString(config)
        )
    }
    
    // 验证配置
    private fun validateConfig(config: NotificationConfig) {
        // 验证邮件配置
        if (config.email.enabled) {
            require(config.email.smtpHost.isNotBlank()) { "SMTP主机不能为空" }
            require(config.email.smtpPort > 0) { "SMTP端口无效" }
        }
        
        // 验证短信配置
        if (config.sms.enabled) {
            require(config.sms.apiKey.isNotBlank()) { "短信API密钥不能为空" }
        }
    }
    
    // 默认配置
    private val defaultConfig = NotificationConfig(
        email = EmailConfig(
            enabled = false,
            smtpHost = "",
            smtpPort = 587,
            username = "",
            password = "",
            useTls = true,
            defaultSender = ""
        ),
        sms = SmsConfig(
            enabled = false,
            provider = "none",
            apiKey = "",
            apiSecret = ""
        ),
        inApp = InAppConfig(
            enabled = true,
            maxNotificationsPerUser = 100,
            expirationDays = 30
        ),
        general = GeneralConfig(
            batchSize = 50,
            retryCount = 3,
            retryDelay = 300
        )
    )
}

// 配置数据类
@Serializable
data class NotificationConfig(
    val email: EmailConfig,
    val sms: SmsConfig,
    val inApp: InAppConfig,
    val general: GeneralConfig
)

@Serializable
data class EmailConfig(
    val enabled: Boolean,
    val smtpHost: String,
    val smtpPort: Int,
    val username: String,
    val password: String,
    val useTls: Boolean,
    val defaultSender: String
)

@Serializable
data class SmsConfig(
    val enabled: Boolean,
    val provider: String,
    val apiKey: String,
    val apiSecret: String
)

@Serializable
data class InAppConfig(
    val enabled: Boolean,
    val maxNotificationsPerUser: Int,
    val expirationDays: Int
)

@Serializable
data class GeneralConfig(
    val batchSize: Int,
    val retryCount: Int,
    val retryDelay: Int
)
```

### 配置加载流程

1. **系统启动**：加载系统级配置
2. **插件初始化**：每个插件加载自己的配置
3. **配置变更**：
   - 系统配置变更：需要重启系统
   - 插件配置变更：插件可自行处理，通常不需要重启

### 敏感信息保护

1. **环境变量注入**：敏感信息通过环境变量注入配置
2. **加密存储**：数据库中的敏感配置使用加密存储
3. **配置掩码**：在UI和日志中对敏感信息进行掩码处理

这种配置管理方式既保证了系统配置的简洁性，也提供了插件配置的灵活性，使系统更易于维护和扩展。

## 角色资源权限控制

### 资源访问控制模型

KAce系统采用细粒度的资源访问控制模型，使不同角色能够按照预设的权限范围访问系统资源：

1. **资源所有权**：每个资源有明确的所有者（用户或角色）
2. **资源访问范围**：定义不同角色可访问的资源范围
3. **资源操作权限**：在资源范围内可执行的操作

### 资源访问范围定义

系统支持以下资源访问范围：

```
- all       - 所有资源
- role      - 同一角色内的资源
- department - 同一部门内的资源
- own       - 仅自己创建的资源
- assigned  - 被明确分配的资源
```

### 角色资源权限示例

不同角色的资源访问权限配置示例：

```
- 系统管理员:
  - media:read:all      - 可访问所有媒体资源
  - media:write:all     - 可修改所有媒体资源
  - user:manage:all     - 可管理所有用户

- 媒体管理员:
  - media:read:all      - 可访问所有媒体资源
  - media:write:role    - 只能修改同角色用户的媒体资源
  - user:read:role      - 只能查看同角色用户信息

- 部门管理员:
  - media:read:department - 可访问本部门的媒体资源
  - media:write:department - 可修改本部门的媒体资源
  - user:read:department   - 只能查看本部门用户信息

- 普通用户:
  - media:read:own      - 只能访问自己的媒体资源
  - media:write:own     - 只能修改自己的媒体资源
  - media:read:assigned - 可访问被分享给自己的资源
```

### 资源权限检查实现

资源权限检查在数据访问层实现，确保用户只能按照权限访问资源：

```kotlin
// 媒体资源仓库实现
class MediaRepositoryImpl(
    private val permissionChecker: PermissionChecker,
    private val mediaDao: MediaDao,
    private val userContextProvider: UserContextProvider
) : MediaRepository {
    
    // 获取媒体资源列表
    override suspend fun getMediaList(query: MediaQuery): List<Media> {
        // 获取当前用户ID和角色
        val currentUserId = userContextProvider.getCurrentUserId()
        val currentRoleId = userContextProvider.getCurrentRoleId()
        val currentDeptId = userContextProvider.getCurrentDepartmentId()
        
        // 根据权限确定查询范围
        return when {
            // 管理员可以查看所有资源
            permissionChecker.hasPermission("media:read:all") -> 
                mediaDao.findAll(query)
                
            // 可以查看本角色资源
            permissionChecker.hasPermission("media:read:role") -> 
                mediaDao.findByRoleId(currentRoleId, query)
                
            // 可以查看本部门资源
            permissionChecker.hasPermission("media:read:department") -> 
                mediaDao.findByDepartmentId(currentDeptId, query)
                
            // 可以查看被分配的资源
            permissionChecker.hasPermission("media:read:assigned") -> 
                mediaDao.findAssignedToUser(currentUserId, query)
                
            // 默认只能查看自己的资源
            else -> mediaDao.findByOwnerId(currentUserId, query)
        }
    }
    
    // 更新媒体资源
    override suspend fun updateMedia(mediaId: String, updateData: MediaUpdateDto): Result<Media> {
        val media = mediaDao.findById(mediaId) ?: return Result.failure(NotFoundException("媒体不存在"))
        val currentUserId = userContextProvider.getCurrentUserId()
        val currentRoleId = userContextProvider.getCurrentRoleId()
        val currentDeptId = userContextProvider.getCurrentDepartmentId()
        
        // 检查当前用户是否有权限更新此媒体
        val canUpdate = when {
            permissionChecker.hasPermission("media:write:all") -> true
            permissionChecker.hasPermission("media:write:role") && media.roleId == currentRoleId -> true
            permissionChecker.hasPermission("media:write:department") && media.departmentId == currentDeptId -> true
            media.ownerId == currentUserId && permissionChecker.hasPermission("media:write:own") -> true
            else -> false
        }
        
        return if (canUpdate) {
            Result.success(mediaDao.update(mediaId, updateData))
        } else {
            Result.failure(ForbiddenException("无权更新此媒体资源"))
        }
    }
}
```

### 数据模型扩展

为支持资源权限控制，数据模型需要包含所有权和共享信息：

```kotlin
@Entity
data class Media(
    @Id val id: String,
    val name: String,
    val type: String,
    val size: Long,
    val path: String,
    val mimeType: String,
    val hash: String,
    val createTime: Instant,
    val updateTime: Instant,
    
    // 资源所有权字段
    val ownerId: String,        // 创建者ID
    val ownerType: String,      // 所有者类型：USER, ROLE, DEPARTMENT, SYSTEM
    val roleId: String?,        // 所属角色ID
    val departmentId: String?,  // 所属部门ID
    
    // 资源共享和权限
    val isPublic: Boolean,      // 是否公开资源
    val sharedWith: Set<String> // 共享给的用户/角色/部门ID列表
)
```

### 资源共享机制

系统提供资源共享机制，允许资源所有者将资源共享给其他用户：

1. **直接共享**：将资源共享给特定用户
2. **角色共享**：将资源共享给特定角色的所有用户
3. **部门共享**：将资源共享给特定部门的所有用户
4. **公开资源**：将资源设为公开，所有用户可访问

```kotlin
// 资源共享服务
interface MediaSharingService {
    // 共享给用户
    suspend fun shareWithUser(mediaId: String, userId: String): Result<Unit>
    
    // 共享给角色
    suspend fun shareWithRole(mediaId: String, roleId: String): Result<Unit>
    
    // 共享给部门
    suspend fun shareWithDepartment(mediaId: String, departmentId: String): Result<Unit>
    
    // 设置为公开资源
    suspend fun makePublic(mediaId: String, isPublic: Boolean): Result<Unit>
    
    // 撤销共享
    suspend fun revokeSharing(mediaId: String, targetId: String): Result<Unit>
}
```

### 角色资源管理UI

系统提供直观的界面，用于管理角色对资源的权限：

```kotlin
@Composable
fun RoleResourcePermissionsScreen(
    viewModel: RoleResourcePermissionsViewModel = getViewModel()
) {
    val roles by viewModel.roles.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val permissions by viewModel.resourcePermissions.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 角色选择器
        Text("选择角色", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(8.dp))
        RoleSelector(
            roles = roles,
            selectedRole = selectedRole,
            onRoleSelected = { viewModel.selectRole(it) }
        )
        
        Spacer(Modifier.height(16.dp))
        
        // 资源权限设置
        Text("资源访问权限", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(8.dp))
        
        // 媒体资源权限
        ResourcePermissionCard(
            title = "媒体资源",
            permissions = listOf(
                PermissionOption("media:read:all", "访问所有媒体", 
                    isSelected = permissions.contains("media:read:all")),
                PermissionOption("media:read:role", "访问本角色媒体", 
                    isSelected = permissions.contains("media:read:role")),
                PermissionOption("media:read:department", "访问本部门媒体", 
                    isSelected = permissions.contains("media:read:department")),
                PermissionOption("media:read:own", "只访问自己的媒体", 
                    isSelected = permissions.contains("media:read:own")),
                // 写入权限
                PermissionOption("media:write:all", "修改所有媒体", 
                    isSelected = permissions.contains("media:write:all")),
                PermissionOption("media:write:role", "修改本角色媒体", 
                    isSelected = permissions.contains("media:write:role")),
                PermissionOption("media:write:own", "只修改自己的媒体", 
                    isSelected = permissions.contains("media:write:own"))
            ),
            onPermissionChanged = { permission, isSelected ->
                viewModel.updatePermission(permission, isSelected)
            }
        )
        
        Spacer(Modifier.height(16.dp))
        
        // 用户数据权限
        ResourcePermissionCard(
            title = "用户数据",
            permissions = listOf(
                PermissionOption("user:read:all", "查看所有用户", 
                    isSelected = permissions.contains("user:read:all")),
                PermissionOption("user:read:role", "查看本角色用户", 
                    isSelected = permissions.contains("user:read:role")),
                PermissionOption("user:read:department", "查看本部门用户", 
                    isSelected = permissions.contains("user:read:department"))
            ),
            onPermissionChanged = { permission, isSelected ->
                viewModel.updatePermission(permission, isSelected)
            }
        )
        
        Spacer(Modifier.height(24.dp))
        
        // 保存按钮
        Button(
            onClick = { viewModel.savePermissions() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("保存权限设置")
        }
    }
}
```

通过这种细粒度的资源权限控制机制，系统可以灵活定义不同角色对资源的访问范围，确保资源安全的同时提供良好的协作体验。

## Ktor中的插件安装卸载实现

### 插件系统架构

在Ktor框架中实现KAce的插件系统，需要设计一套可动态加载、卸载的插件机制。核心架构如下：

```
KAce Server (Ktor应用)
├── 插件管理器 (PluginManager)
├── 插件注册表 (PluginRegistry)
├── 插件类加载器 (PluginClassLoader)
├── 插件生命周期管理器 (PluginLifecycleManager)
└── 插件事件总线 (PluginEventBus)
```

### 插件加载机制

KAce使用自定义类加载器实现插件的动态加载：

```kotlin
/**
 * 插件类加载器，负责从插件JAR文件加载类
 */
class PluginClassLoader(
    private val pluginJarFile: File,
    parent: ClassLoader = PluginClassLoader::class.java.classLoader
) : URLClassLoader(arrayOf(pluginJarFile.toURI().toURL()), parent) {
    
    // 插件专用的资源缓存
    private val resourceCache = ConcurrentHashMap<String, URL>()
    
    // 重写类加载逻辑，确保插件类优先从自己的JAR加载
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        // 检查类是否已加载
        synchronized(getClassLoadingLock(name)) {
            // 先查找已加载的类
            findLoadedClass(name)?.let { return it }
            
            try {
                // 尝试从插件JAR加载类
                val classData = findClassInJar(name)
                if (classData != null) {
                    return defineClass(name, classData, 0, classData.size)
                }
            } catch (e: Exception) {
                // 忽略异常，尝试父类加载器
            }
            
            // 如果插件JAR中找不到，则委托给父类加载器
            return super.loadClass(name, resolve)
        }
    }
    
    // 从JAR文件查找类
    private fun findClassInJar(name: String): ByteArray? {
        val path = name.replace('.', '/') + ".class"
        val url = findResource(path) ?: return null
        
        return url.openStream().use { it.readBytes() }
    }
    
    // 清理资源
    fun dispose() {
        resourceCache.clear()
        close()
    }
}
```

### 插件管理器

插件管理器负责插件的安装、卸载和生命周期管理：

```kotlin
/**
 * 插件管理器 - 负责插件的安装、卸载和管理
 */
class PluginManager(
    private val application: Application,
    private val pluginsDir: File,
    private val configService: ConfigService,
    private val eventBus: EventBus
) {
    private val logger = LoggerFactory.getLogger(PluginManager::class.java)
    private val plugins = ConcurrentHashMap<String, PluginInfo>()
    
    // 插件信息类
    data class PluginInfo(
        val id: String,
        val instance: Plugin,
        val classLoader: PluginClassLoader,
        val jarFile: File,
        var status: PluginStatus = PluginStatus.INSTALLED
    )
    
    enum class PluginStatus {
        INSTALLED, ENABLED, DISABLED, ERROR
    }
    
    /**
     * 初始化插件管理器，加载已安装的插件
     */
    fun initialize() {
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs()
        }
        
        // 加载已安装的插件
        loadInstalledPlugins()
    }
    
    /**
     * 加载已安装的插件
     */
    private fun loadInstalledPlugins() {
        // 从数据库获取已安装的插件信息
        val installedPlugins = configService.getInstalledPlugins()
        
        // 依次加载每个插件
        installedPlugins.forEach { pluginInfo ->
            val pluginJarFile = File(pluginsDir, "${pluginInfo.id}-${pluginInfo.version}.jar")
            if (pluginJarFile.exists()) {
                try {
                    loadPlugin(pluginJarFile, pluginInfo.autoEnable)
                } catch (e: Exception) {
                    logger.error("Failed to load plugin: ${pluginInfo.id}", e)
                }
            } else {
                logger.warn("Plugin JAR not found: ${pluginInfo.id}")
            }
        }
    }
    
    /**
     * 安装插件
     * @param jarFile 插件JAR文件
     * @param autoEnable 是否自动启用
     * @return 安装结果
     */
    fun installPlugin(jarFile: File, autoEnable: Boolean = false): Result<PluginInfo> {
        return try {
            // 验证插件JAR
            val validationResult = validatePluginJar(jarFile)
            if (!validationResult.isSuccess) {
                return Result.failure(validationResult.exceptionOrNull()!!)
            }
            
            // 读取插件元数据
            val metadata = readPluginMetadata(jarFile)
            val pluginId = metadata.id
            
            // 检查是否已安装
            if (plugins.containsKey(pluginId)) {
                return Result.failure(IllegalStateException("Plugin already installed: $pluginId"))
            }
            
            // 复制插件JAR到插件目录
            val targetFile = File(pluginsDir, "${metadata.id}-${metadata.version}.jar")
            jarFile.copyTo(targetFile, overwrite = true)
            
            // 加载插件
            val pluginInfo = loadPlugin(targetFile, autoEnable)
            
            // 保存插件信息到数据库
            configService.saveInstalledPlugin(
                InstalledPluginDto(
                    id = metadata.id,
                    name = metadata.name,
                    version = metadata.version,
                    description = metadata.description,
                    installTime = Instant.now(),
                    autoEnable = autoEnable,
                    status = if (autoEnable) "ENABLED" else "INSTALLED"
                )
            )
            
            // 发布插件安装事件
            eventBus.publish(PluginInstalledEvent(pluginId))
            
            Result.success(pluginInfo)
        } catch (e: Exception) {
            logger.error("Failed to install plugin", e)
            Result.failure(e)
        }
    }
    
    /**
     * 卸载插件
     * @param pluginId 插件ID
     * @return 卸载结果
     */
    fun uninstallPlugin(pluginId: String): Result<Unit> {
        return try {
            val pluginInfo = plugins[pluginId] ?: 
                return Result.failure(NoSuchElementException("Plugin not found: $pluginId"))
            
            // 如果插件已启用，先禁用它
            if (pluginInfo.status == PluginStatus.ENABLED) {
                disablePlugin(pluginId)
            }
            
            // 卸载插件
            unloadPlugin(pluginInfo)
            
            // 删除插件JAR文件
            pluginInfo.jarFile.delete()
            
            // 从数据库删除插件信息
            configService.deleteInstalledPlugin(pluginId)
            
            // 发布插件卸载事件
            eventBus.publish(PluginUninstalledEvent(pluginId))
            
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to uninstall plugin: $pluginId", e)
            Result.failure(e)
        }
    }
    
    /**
     * 启用插件
     * @param pluginId 插件ID
     * @return 启用结果
     */
    fun enablePlugin(pluginId: String): Result<Unit> {
        return try {
            val pluginInfo = plugins[pluginId] ?: 
                return Result.failure(NoSuchElementException("Plugin not found: $pluginId"))
            
            if (pluginInfo.status == PluginStatus.ENABLED) {
                return Result.success(Unit) // 已经启用，无需操作
            }
            
            // 启用插件
            val plugin = pluginInfo.instance
            
            // 检查依赖
            val dependencyResult = checkDependencies(plugin)
            if (!dependencyResult.isSuccess) {
                return Result.failure(dependencyResult.exceptionOrNull()!!)
            }
            
            // 调用插件生命周期方法
            plugin.onEnable()
            
            // 注册插件服务和路由
            registerPluginServices(plugin)
            registerPluginRoutes(plugin)
            
            // 更新插件状态
            pluginInfo.status = PluginStatus.ENABLED
            plugins[pluginId] = pluginInfo
            
            // 更新数据库状态
            configService.updatePluginStatus(pluginId, "ENABLED")
            
            // 发布插件启用事件
            eventBus.publish(PluginEnabledEvent(pluginId))
            
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to enable plugin: $pluginId", e)
            Result.failure(e)
        }
    }
    
    /**
     * 禁用插件
     * @param pluginId 插件ID
     * @return 禁用结果
     */
    fun disablePlugin(pluginId: String): Result<Unit> {
        return try {
            val pluginInfo = plugins[pluginId] ?: 
                return Result.failure(NoSuchElementException("Plugin not found: $pluginId"))
            
            if (pluginInfo.status != PluginStatus.ENABLED) {
                return Result.success(Unit) // 未启用，无需操作
            }
            
            // 检查是否有其他插件依赖此插件
            val dependents = findDependentPlugins(pluginId)
            if (dependents.isNotEmpty()) {
                return Result.failure(IllegalStateException(
                    "Cannot disable plugin $pluginId because it is required by: ${dependents.joinToString()}"
                ))
            }
            
            // 调用插件生命周期方法
            val plugin = pluginInfo.instance
            plugin.onDisable()
            
            // 取消注册插件路由
            unregisterPluginRoutes(plugin)
            
            // 更新插件状态
            pluginInfo.status = PluginStatus.DISABLED
            plugins[pluginId] = pluginInfo
            
            // 更新数据库状态
            configService.updatePluginStatus(pluginId, "DISABLED")
            
            // 发布插件禁用事件
            eventBus.publish(PluginDisabledEvent(pluginId))
            
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to disable plugin: $pluginId", e)
            Result.failure(e)
        }
    }
    
    /**
     * 加载插件
     * @param jarFile 插件JAR文件
     * @param autoEnable 是否自动启用
     * @return 插件信息
     */
    private fun loadPlugin(jarFile: File, autoEnable: Boolean): PluginInfo {
        // 创建插件类加载器
        val classLoader = PluginClassLoader(jarFile)
        
        // 读取插件元数据
        val metadata = readPluginMetadata(jarFile)
        
        // 加载插件主类
        val pluginClass = classLoader.loadClass(metadata.mainClass) as Class<Plugin>
        val plugin = pluginClass.getDeclaredConstructor().newInstance()
        
        // 初始化插件
        plugin.initialize()
        
        // 创建插件信息
        val pluginInfo = PluginInfo(
            id = metadata.id,
            instance = plugin,
            classLoader = classLoader,
            jarFile = jarFile,
            status = PluginStatus.INSTALLED
        )
        
        // 保存到插件映射
        plugins[metadata.id] = pluginInfo
        
        // 如果需要自动启用，则启用插件
        if (autoEnable) {
            enablePlugin(metadata.id)
        }
        
        return pluginInfo
    }
    
    /**
     * 卸载插件
     * @param pluginInfo 插件信息
     */
    private fun unloadPlugin(pluginInfo: PluginInfo) {
        // 从插件映射中移除
        plugins.remove(pluginInfo.id)
        
        try {
            // 释放类加载器资源
            pluginInfo.classLoader.dispose()
        } catch (e: Exception) {
            logger.warn("Error disposing plugin class loader: ${pluginInfo.id}", e)
        }
    }
    
    /**
     * 验证插件JAR
     * @param jarFile 插件JAR文件
     * @return 验证结果
     */
    private fun validatePluginJar(jarFile: File): Result<Unit> {
        if (!jarFile.exists() || !jarFile.isFile) {
            return Result.failure(IllegalArgumentException("Invalid plugin JAR file"))
        }
        
        try {
            JarFile(jarFile).use { jar ->
                // 检查是否存在plugin.json
                val pluginJsonEntry = jar.getJarEntry("plugin.json")
                    ?: return Result.failure(IllegalArgumentException("Missing plugin.json in JAR"))
                
                // 读取并验证plugin.json
                val pluginJson = jar.getInputStream(pluginJsonEntry).bufferedReader().use { it.readText() }
                val metadata = Json.decodeFromString<PluginMetadata>(pluginJson)
                
                // 验证必要字段
                if (metadata.id.isBlank() || metadata.name.isBlank() || 
                    metadata.version.isBlank() || metadata.mainClass.isBlank()) {
                    return Result.failure(IllegalArgumentException(
                        "Invalid plugin.json: missing required fields"
                    ))
                }
                
                // 检查主类是否存在
                val mainClassPath = metadata.mainClass.replace('.', '/') + ".class"
                if (jar.getJarEntry(mainClassPath) == null) {
                    return Result.failure(IllegalArgumentException(
                        "Main class not found in JAR: ${metadata.mainClass}"
                    ))
                }
            }
            
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    /**
     * 读取插件元数据
     * @param jarFile 插件JAR文件
     * @return 插件元数据
     */
    private fun readPluginMetadata(jarFile: File): PluginMetadata {
        JarFile(jarFile).use { jar ->
            val pluginJsonEntry = jar.getJarEntry("plugin.json")
            val pluginJson = jar.getInputStream(pluginJsonEntry).bufferedReader().use { it.readText() }
            return Json.decodeFromString(pluginJson)
        }
    }
    
    /**
     * 检查插件依赖
     * @param plugin 插件实例
     * @return 检查结果
     */
    private fun checkDependencies(plugin: Plugin): Result<Unit> {
        for (dependency in plugin.dependencies) {
            val dependencyPlugin = plugins[dependency.id]
                ?: return Result.failure(DependencyException(
                    "Missing dependency: ${dependency.id}"
                ))
            
            if (dependencyPlugin.status != PluginStatus.ENABLED) {
                return Result.failure(DependencyException(
                    "Dependency not enabled: ${dependency.id}"
                ))
            }
            
            // 检查版本兼容性
            if (!isVersionCompatible(dependencyPlugin.instance.version, dependency.versionRequirement)) {
                return Result.failure(DependencyException(
                    "Incompatible dependency version: ${dependency.id} " +
                    "(required: ${dependency.versionRequirement}, found: ${dependencyPlugin.instance.version})"
                ))
            }
        }
        
        return Result.success(Unit)
    }
    
    /**
     * 查找依赖该插件的其他插件
     * @param pluginId 插件ID
     * @return 依赖该插件的插件ID列表
     */
    private fun findDependentPlugins(pluginId: String): List<String> {
        return plugins.values
            .filter { it.status == PluginStatus.ENABLED }
            .filter { plugin -> 
                plugin.instance.dependencies.any { it.id == pluginId }
            }
            .map { it.id }
    }
    
    /**
     * 注册插件服务
     * @param plugin 插件实例
     */
    private fun registerPluginServices(plugin: Plugin) {
        val serviceRegistry = application.attributes.computeIfAbsent(ServiceRegistry.KEY) { ServiceRegistryImpl() }
        plugin.registerServices(serviceRegistry)
    }
    
    /**
     * 注册插件路由
     * @param plugin 插件实例
     */
    private fun registerPluginRoutes(plugin: Plugin) {
        application.routing {
            val routeRegistry = RouteRegistryImpl(this)
            plugin.registerRoutes(routeRegistry)
        }
    }
    
    /**
     * 取消注册插件路由
     * @param plugin 插件实例
     */
    private fun unregisterPluginRoutes(plugin: Plugin) {
        // 在Ktor中取消注册路由比较复杂，因为路由一旦注册就不能直接移除
        // 这里我们可以使用一个代理机制，在路由处理前检查插件状态
        // 如果插件已禁用，则拒绝处理请求
        
        // 这是一个简化的实现
        val pluginId = plugin.id
        application.routing {
            intercept(ApplicationCallPipeline.Plugins) {
                val requestedPluginId = call.attributes.getOrNull(PluginAttribute)
                if (requestedPluginId == pluginId) {
                    val pluginInfo = plugins[pluginId]
                    if (pluginInfo == null || pluginInfo.status != PluginStatus.ENABLED) {
                        call.respond(HttpStatusCode.NotFound, "Plugin not available")
                        finish()
                    }
                }
            }
        }
    }
    
    /**
     * 检查版本兼容性
     * @param version 实际版本
     * @param requirement 版本要求
     * @return 是否兼容
     */
    private fun isVersionCompatible(version: String, requirement: String): Boolean {
        // 简化的版本兼容性检查
        // 实际实现应使用语义化版本规则
        return version == requirement || requirement == "*"
    }
    
    /**
     * 依赖异常
     */
    class DependencyException(message: String) : Exception(message)
    
    /**
     * 插件元数据
     */
    @Serializable
    data class PluginMetadata(
        val id: String,
        val name: String,
        val version: String,
        val description: String,
        val mainClass: String,
        val dependencies: List<PluginDependency> = emptyList()
    )
    
    /**
     * 插件依赖
     */
    @Serializable
    data class PluginDependency(
        val id: String,
        val versionRequirement: String
    )
    
    /**
     * 已安装插件DTO
     */
    data class InstalledPluginDto(
        val id: String,
        val name: String,
        val version: String,
        val description: String,
        val installTime: Instant,
        val autoEnable: Boolean,
        val status: String
    )
    
    /**
     * 插件属性键
     */
    companion object PluginAttribute : AttributeKey<String>("PluginId")
} 