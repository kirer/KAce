# 插件SDK设计

## 概述

插件SDK是KAce应用管理系统的核心组件，它提供了一套统一的接口和工具，使开发者能够创建可以加载到系统中的应用插件。插件SDK的设计目标是：

1. 提供简单清晰的插件开发接口
2. 支持插件的安装、卸载和更新
3. 实现插件之间的通信
4. 提供统一的权限、菜单、API和资源管理
5. 支持插件配置和数据存储

## 核心接口

### 插件基础接口

```kotlin
/**
 * 插件基础接口，所有插件必须实现此接口
 */
interface KAcePlugin {
    /**
     * 插件元数据
     */
    val metadata: PluginMetadata
    
    /**
     * 插件安装时调用
     */
    fun onInstall()
    
    /**
     * 插件卸载时调用
     */
    fun onUninstall()
    
    /**
     * 插件启用时调用
     */
    fun onEnable()
    
    /**
     * 插件禁用时调用
     */
    fun onDisable()
    
    /**
     * 注册插件提供的服务
     */
    fun registerServices(serviceRegistry: ServiceRegistry)
    
    /**
     * 注册插件提供的权限
     */
    fun registerPermissions(): List<Permission>
    
    /**
     * 注册插件提供的菜单
     */
    fun registerMenus(): List<Menu>
    
    /**
     * 注册插件提供的API
     */
    fun registerApis(): List<Api>
    
    /**
     * 注册插件提供的资源
     */
    fun registerResources(): List<Resource>
    
    /**
     * 注册插件需要的数据库表
     */
    fun registerTables(): List<TableDefinition>
    
    /**
     * 注册插件配置
     */
    fun registerConfigs(): List<ConfigDefinition>
}
```

### 插件元数据

```kotlin
/**
 * 插件元数据，描述插件的基本信息
 */
data class PluginMetadata(
    /**
     * 插件ID，全局唯一
     */
    val id: String,
    
    /**
     * 插件名称
     */
    val name: String,
    
    /**
     * 插件简码，用于表前缀
     */
    val shortCode: String,
    
    /**
     * 插件版本
     */
    val version: String,
    
    /**
     * 插件描述
     */
    val description: String,
    
    /**
     * 插件作者
     */
    val author: String,
    
    /**
     * 插件依赖
     */
    val dependencies: List<PluginDependency> = emptyList()
)

/**
 * 插件依赖
 */
data class PluginDependency(
    /**
     * 依赖的插件ID
     */
    val pluginId: String,
    
    /**
     * 依赖的版本要求
     */
    val version: String,
    
    /**
     * 是否可选依赖
     */
    val isOptional: Boolean = false
)
```

### 服务注册与发现

```kotlin
/**
 * 服务注册表，用于注册和发现服务
 */
interface ServiceRegistry {
    /**
     * 注册服务
     */
    fun <T : Any> register(serviceClass: KClass<T>, implementation: T)
    
    /**
     * 获取服务
     */
    fun <T : Any> get(serviceClass: KClass<T>): T?
    
    /**
     * 获取所有实现了指定服务接口的服务
     */
    fun <T : Any> getAll(serviceClass: KClass<T>): List<T>
}
```

### 权限注册

```kotlin
/**
 * 权限定义
 */
data class Permission(
    /**
     * 权限编码，格式为：应用简码:模块:操作，例如：sys:user:create
     */
    val code: String,
    
    /**
     * 权限名称
     */
    val name: String,
    
    /**
     * 权限描述
     */
    val description: String,
    
    /**
     * 权限类型
     */
    val type: PermissionType
)

/**
 * 权限类型
 */
enum class PermissionType {
    /**
     * API权限
     */
    API,
    
    /**
     * 资源权限
     */
    RESOURCE
}
```

### 菜单注册

```kotlin
/**
 * 菜单定义
 */
data class Menu(
    /**
     * 菜单编码，全局唯一
     */
    val code: String,
    
    /**
     * 菜单名称
     */
    val name: String,
    
    /**
     * 菜单路径
     */
    val path: String,
    
    /**
     * 菜单图标
     */
    val icon: String? = null,
    
    /**
     * 父菜单编码
     */
    val parentCode: String? = null,
    
    /**
     * 排序值
     */
    val sort: Int = 0,
    
    /**
     * 关联的权限编码
     */
    val permissionCode: String
)
```

### API注册

```kotlin
/**
 * API定义
 */
data class Api(
    /**
     * API路径
     */
    val path: String,
    
    /**
     * HTTP方法
     */
    val method: HttpMethod,
    
    /**
     * 关联的权限编码
     */
    val permissionCode: String,
    
    /**
     * API描述
     */
    val description: String
)

/**
 * HTTP方法
 */
enum class HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
}
```

### 资源注册

```kotlin
/**
 * 资源定义
 */
data class Resource(
    /**
     * 资源路径
     */
    val path: String,
    
    /**
     * 资源类型
     */
    val type: ResourceType,
    
    /**
     * 关联的权限编码
     */
    val permissionCode: String
)

/**
 * 资源类型
 */
enum class ResourceType {
    /**
     * 图片资源
     */
    IMAGE,
    
    /**
     * 文件资源
     */
    FILE,
    
    /**
     * 其他资源
     */
    OTHER
}
```

### 表结构注册

```kotlin
/**
 * 表结构定义
 */
data class TableDefinition(
    /**
     * 表名（不包含前缀）
     */
    val name: String,
    
    /**
     * 列定义
     */
    val columns: List<ColumnDefinition>,
    
    /**
     * 索引定义
     */
    val indices: List<IndexDefinition>,
    
    /**
     * 主键列名
     */
    val primaryKey: String
)

/**
 * 列定义
 */
data class ColumnDefinition(
    /**
     * 列名
     */
    val name: String,
    
    /**
     * 列类型
     */
    val type: String,
    
    /**
     * 是否可为空
     */
    val nullable: Boolean = false,
    
    /**
     * 默认值
     */
    val defaultValue: String? = null,
    
    /**
     * 列注释
     */
    val comment: String? = null
)

/**
 * 索引定义
 */
data class IndexDefinition(
    /**
     * 索引名
     */
    val name: String,
    
    /**
     * 索引列
     */
    val columns: List<String>,
    
    /**
     * 是否唯一索引
     */
    val unique: Boolean = false
)
```

### 配置注册

```kotlin
/**
 * 配置定义
 */
data class ConfigDefinition(
    /**
     * 配置键
     */
    val key: String,
    
    /**
     * 默认值
     */
    val defaultValue: String,
    
    /**
     * 配置类型
     */
    val type: ConfigType,
    
    /**
     * 配置描述
     */
    val description: String,
    
    /**
     * 是否系统配置（系统配置不可通过UI修改）
     */
    val isSystem: Boolean = false
)

/**
 * 配置类型
 */
enum class ConfigType {
    /**
     * 字符串类型
     */
    STRING,
    
    /**
     * 数字类型
     */
    NUMBER,
    
    /**
     * 布尔类型
     */
    BOOLEAN,
    
    /**
     * JSON类型
     */
    JSON
}
```

### 事件机制

```kotlin
/**
 * 事件总线，用于插件间通信
 */
interface EventBus {
    /**
     * 发布事件
     */
    fun publish(event: Any)
    
    /**
     * 订阅事件
     */
    fun <T : Any> subscribe(eventType: KClass<T>, handler: (T) -> Unit): Subscription
}

/**
 * 订阅
 */
interface Subscription {
    /**
     * 取消订阅
     */
    fun cancel()
}
```

## 插件生命周期

插件的生命周期包括以下几个阶段：

### 1. 安装 (Install)

在安装阶段，系统会：
- 解析插件包，提取元数据
- 检查插件依赖关系
- 创建应用记录
- 注册插件提供的权限、菜单、API和资源
- 创建插件所需的数据库表
- 初始化插件配置
- 调用插件的onInstall方法

### 2. 启用 (Enable)

在启用阶段，系统会：
- 启用插件功能
- 注册插件提供的服务
- 调用插件的onEnable方法

### 3. 禁用 (Disable)

在禁用阶段，系统会：
- 暂停插件功能
- 调用插件的onDisable方法

### 4. 卸载 (Uninstall)

在卸载阶段，系统会：
- 调用插件的onDisable方法
- 调用插件的onUninstall方法
- 更新应用状态为已卸载

## 插件间通信机制

### 1. 服务发现

插件可以通过ServiceRegistry注册和发现服务，允许插件提供服务接口供其他插件使用。

```kotlin
// 在插件A中定义服务接口
interface UserService {
    suspend fun getUserById(id: String): User?
    suspend fun listUsers(page: Int, size: Int): Page<User>
}

// 在插件A中实现服务
class UserServiceImpl : UserService {
    override suspend fun getUserById(id: String): User? {
        // 实现逻辑
    }
    
    override suspend fun listUsers(page: Int, size: Int): Page<User> {
        // 实现逻辑
    }
}

// 在插件A中注册服务
override fun registerServices(serviceRegistry: ServiceRegistry) {
    serviceRegistry.register(UserService::class, UserServiceImpl())
}

// 在插件B中使用服务
val userService = serviceRegistry.get(UserService::class)
userService?.getUserById(userId)
```

### 2. 事件总线

插件可以通过事件总线发布和订阅事件，实现插件间的异步通信。

```kotlin
// 定义事件
data class UserCreatedEvent(val user: User)

// 在插件A中发布事件
eventBus.publish(UserCreatedEvent(user))

// 在插件B中订阅事件
val subscription = eventBus.subscribe(UserCreatedEvent::class) { event ->
    // 处理事件
}

// 取消订阅
subscription.cancel()
```

## 插件开发示例

### 1. 创建插件类

```kotlin
class MyPlugin : KAcePlugin {
    override val metadata = PluginMetadata(
        id = "com.example.myplugin",
        name = "我的插件",
        shortCode = "my",
        version = "1.0.0",
        description = "这是一个示例插件",
        author = "张三",
        dependencies = listOf(
            PluginDependency("com.example.otherplugin", "^1.0.0", true)
        )
    )
    
    override fun onInstall() {
        // 安装逻辑
    }
    
    override fun onUninstall() {
        // 卸载逻辑
    }
    
    override fun onEnable() {
        // 启用逻辑
    }
    
    override fun onDisable() {
        // 禁用逻辑
    }
    
    override fun registerServices(serviceRegistry: ServiceRegistry) {
        serviceRegistry.register(MyService::class, MyServiceImpl())
    }
    
    override fun registerPermissions(): List<Permission> {
        return listOf(
            Permission(
                code = "my:user:create",
                name = "创建用户",
                description = "创建新用户",
                type = PermissionType.API
            ),
            Permission(
                code = "my:user:read",
                name = "查看用户",
                description = "查看用户信息",
                type = PermissionType.API
            )
        )
    }
    
    override fun registerMenus(): List<Menu> {
        return listOf(
            Menu(
                code = "my:user",
                name = "用户管理",
                path = "/my/user",
                icon = "user",
                sort = 1,
                permissionCode = "my:user:read"
            )
        )
    }
    
    override fun registerApis(): List<Api> {
        return listOf(
            Api(
                path = "/api/my/users",
                method = HttpMethod.POST,
                permissionCode = "my:user:create",
                description = "创建用户"
            ),
            Api(
                path = "/api/my/users",
                method = HttpMethod.GET,
                permissionCode = "my:user:read",
                description = "获取用户列表"
            )
        )
    }
    
    override fun registerResources(): List<Resource> {
        return listOf(
            Resource(
                path = "static/images/logo.png",
                type = ResourceType.IMAGE,
                permissionCode = "my:user:read"
            )
        )
    }
    
    override fun registerTables(): List<TableDefinition> {
        return listOf(
            TableDefinition(
                name = "user",
                columns = listOf(
                    ColumnDefinition(
                        name = "id",
                        type = "VARCHAR(36)",
                        nullable = false
                    ),
                    ColumnDefinition(
                        name = "username",
                        type = "VARCHAR(50)",
                        nullable = false
                    ),
                    ColumnDefinition(
                        name = "created_at",
                        type = "DATETIME",
                        nullable = false,
                        defaultValue = "CURRENT_TIMESTAMP"
                    )
                ),
                indices = listOf(
                    IndexDefinition(
                        name = "idx_username",
                        columns = listOf("username"),
                        unique = true
                    )
                ),
                primaryKey = "id"
            )
        )
    }
    
    override fun registerConfigs(): List<ConfigDefinition> {
        return listOf(
            ConfigDefinition(
                key = "maxUsers",
                defaultValue = "100",
                type = ConfigType.NUMBER,
                description = "最大用户数"
            )
        )
    }
}
```

### 2. 实现服务

```kotlin
interface MyService {
    suspend fun doSomething(): String
}

class MyServiceImpl : MyService {
    override suspend fun doSomething(): String {
        return "Hello, World!"
    }
}
```

### 3. 处理事件

```kotlin
class MyEventHandler(private val eventBus: EventBus) {
    init {
        eventBus.subscribe(UserCreatedEvent::class) { event ->
            handleUserCreated(event)
        }
    }
    
    private fun handleUserCreated(event: UserCreatedEvent) {
        // 处理用户创建事件
    }
}
```

### 4. 使用配置

```kotlin
class MyService(private val configService: ApplicationConfigService, private val applicationId: String) {
    suspend fun getMaxUsers(): Int {
        val maxUsers = configService.getConfigValue(applicationId, "maxUsers", "100")
        return maxUsers.toIntOrNull() ?: 100
    }
}
```

## 插件打包与部署

### 1. 插件目录结构

```
my-plugin/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── example/
│       │           └── myplugin/
│       │               ├── MyPlugin.kt
│       │               ├── MyService.kt
│       │               └── ...
│       └── resources/
│           ├── static/
│           │   └── images/
│           │       └── logo.png
│           └── plugin.properties
├── build.gradle.kts
└── README.md
```

### 2. 插件配置文件

```properties
# plugin.properties
plugin.id=com.example.myplugin
plugin.name=我的插件
plugin.shortCode=my
plugin.version=1.0.0
plugin.description=这是一个示例插件
plugin.author=张三
plugin.dependencies=com.example.otherplugin:^1.0.0:true
```

### 3. 构建脚本

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.8.20"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.example:kace-plugin-sdk:1.0.0")
}

tasks.jar {
    manifest {
        attributes(
            "Plugin-Class" to "com.example.myplugin.MyPlugin",
            "Plugin-Id" to "com.example.myplugin",
            "Plugin-Version" to "1.0.0"
        )
    }
}
```

### 4. 部署插件

将构建好的插件JAR文件上传到系统的插件管理界面，系统会自动安装并启用插件。

## 安全考虑

1. **插件隔离**：确保插件只能访问自己的资源和配置
2. **权限验证**：严格验证插件的权限请求
3. **依赖管理**：检查插件依赖，防止依赖冲突
4. **资源限制**：限制插件的资源使用，防止恶意插件消耗过多资源

## 初期实现建议

在系统初期，可以采用简化的插件加载机制：

1. **静态加载**：系统启动时加载所有插件，而不是运行时动态加载/卸载
2. **简化生命周期**：合并安装和启用阶段，简化插件生命周期管理
3. **基本隔离**：通过命名空间和权限检查实现基本的插件隔离
4. **有限的插件间通信**：先实现服务发现机制，后续再添加事件总线

这种简化的实现可以降低初期开发难度，同时保留未来扩展的可能性。随着系统的稳定和成熟，可以逐步实现完整的插件动态加载/卸载机制。