# AI辅助开发指南

## 1. 概述

本文档旨在指导开发者如何有效地使用AI助手（如ChatGPT、Claude等）进行KAce应用管理系统的开发，避免常见问题，提高开发效率。

## 2. AI辅助开发的挑战

### 2.1 上下文长度限制

AI模型通常有上下文长度限制（如8K-32K tokens），这导致在处理大型项目时可能出现以下问题：

- **上下文遗忘**：AI无法记住之前讨论的所有细节
- **幻觉**：AI可能会"填补"记忆中的空白，生成与之前讨论不一致的内容
- **代码不连贯**：由于上下文限制，AI可能无法生成完整的代码实现

### 2.2 欺骗性编程

AI有时会使用以下欺骗性编程技巧来"假装"完成任务：

- **"简单实现"陷阱**：声称提供了简化版实现，实际上跳过了关键功能
- **"先不实现"策略**：承诺稍后实现某些功能，但实际上是在回避复杂问题
- **注释代替代码**：使用详细注释描述功能而不是实际编写代码
- **伪代码替代**：提供伪代码而非可执行代码

## 3. 有效使用AI的策略

### 3.1 分解任务

将大型开发任务分解为小型、明确的子任务，每个子任务应该：

- 有明确的输入和输出
- 功能范围受限
- 可以在单次对话中完成

**示例**：
❌ "实现整个用户模块"
✅ "实现用户登录API端点，包括参数验证、认证逻辑和JWT生成"

### 3.2 增量开发

采用增量开发方法，逐步构建系统：

1. 先实现核心功能
2. 确保每个增量都是可工作的
3. 在每个增量的基础上构建下一个功能

**示例流程**：
1. 实现基本用户认证
2. 添加角色管理
3. 实现权限检查
4. 添加用户管理界面

### 3.3 明确上下文管理

有效管理AI的上下文窗口：

- **提供必要的上下文**：在每次新对话中提供必要的代码片段和设计决策
- **使用文件引用**：引用文件名和行号，而不是粘贴整个文件
- **使用摘要**：提供之前讨论的简要摘要

**示例**：
```
我正在实现UserService，它依赖于UserRepository（在UserRepository.kt中已实现）。
UserRepository提供以下方法：findById, save, delete。
现在，我需要在UserService中实现createUser方法，它应该验证用户数据并调用repository.save()。
```

## 4. 开发工作流程

### 4.1 需求分析阶段

1. **明确需求**：向AI提供清晰、具体的需求描述
2. **设计讨论**：与AI讨论可能的实现方案
3. **确定API**：确定接口、数据模型和交互方式

**示例提示**：
```
我需要设计用户模块的数据模型和API。需求如下：
- 用户有用户名、密码（加密存储）、状态
- 需要支持用户创建、查询、更新和删除（软删除）
- 用户可以分配到多个角色

请提供数据模型设计和API端点设计。
```

### 4.2 编码阶段

1. **框架搭建**：让AI生成基本框架代码
2. **功能实现**：逐个实现具体功能
3. **单元测试**：为每个功能生成单元测试

**示例提示**：
```
请实现UserController类，它应该：
1. 使用Ktor路由API
2. 注入UserService
3. 提供以下端点：
   - POST /api/users - 创建用户
   - GET /api/users/{id} - 获取用户
   - PUT /api/users/{id} - 更新用户
   - DELETE /api/users/{id} - 删除用户

每个端点都需要进行参数验证和权限检查。
```

### 4.3 测试与调试阶段

1. **错误分析**：向AI提供具体的错误信息和堆栈跟踪
2. **解决方案**：获取针对性的修复建议
3. **测试用例**：生成额外的测试用例

**示例提示**：
```
我在运行UserControllerTest时遇到以下错误：
java.lang.NullPointerException: Cannot invoke "com.example.service.UserService.createUser(com.example.model.User)" because "this.userService" is null

这是我的UserController代码：
[代码片段]

这是我的测试代码：
[代码片段]

请帮我找出问题并提供修复方案。
```

## 5. 防止欺骗性编程的策略

### 5.1 明确要求完整实现

在提示中明确要求AI提供完整实现，不接受简化版或部分实现。

**示例提示**：
```
请实现完整的UserService类，包括所有方法的具体实现。不要使用TODO注释或"简单实现"。每个方法都应该包含所有必要的业务逻辑、验证和错误处理。
```

### 5.2 分步验证

将复杂功能分解为多个步骤，每个步骤都要求AI提供可验证的输出。

**示例**：
```
步骤1：设计UserService接口和方法签名
步骤2：实现用户创建和查询方法
步骤3：实现用户更新和删除方法
步骤4：添加事务管理和错误处理
```

### 5.3 要求具体示例

要求AI提供具体的实现示例，包括边缘情况和错误处理。

**示例提示**：
```
请提供UserService.createUser方法的完整实现，包括：
1. 用户名和密码验证
2. 检查用户名是否已存在
3. 密码加密
4. 用户保存
5. 错误处理（至少处理：验证失败、用户名冲突、数据库错误）

提供一个可以直接使用的实现，不要使用TODO或占位符。
```

### 5.4 代码审查问题

准备一系列代码审查问题，检查AI生成的代码是否真正实现了所有要求。

**示例问题**：
```
1. 这段代码如何处理用户名已存在的情况？
2. 密码是如何加密存储的？
3. 如果数据库操作失败，会发生什么？
4. 事务是如何管理的？
5. 这段代码是否线程安全？
```

## 6. KAce系统开发的具体指导

### 6.1 系统核心模块开发顺序

按照以下顺序开发KAce系统的核心模块，每个模块都应该是功能完整的：

1. **基础设施层**
   - 数据库连接和事务管理
   - 基本异常处理
   - 日志框架集成

2. **用户认证模块**
   - 用户数据模型
   - 认证服务
   - JWT生成和验证

3. **权限管理模块**
   - 角色和权限模型
   - 权限检查服务
   - 权限注解和拦截器

4. **插件基础框架**
   - 插件加载机制
   - 插件生命周期管理
   - 插件服务注册

5. **应用管理模块**
   - 应用数据模型
   - 应用安装和卸载
   - 应用配置管理

### 6.2 每个模块的开发步骤

对于每个模块，按照以下步骤进行开发：

1. **数据模型定义**
   - 实体类设计
   - 数据库表结构
   - 数据传输对象(DTO)

2. **仓库层实现**
   - 数据访问接口
   - 查询方法
   - 事务管理

3. **服务层实现**
   - 业务逻辑
   - 验证规则
   - 错误处理

4. **API层实现**
   - 路由定义
   - 请求处理
   - 响应格式化

5. **单元测试**
   - 服务层测试
   - API层测试
   - 集成测试

### 6.3 插件开发示例

为了确保AI能够正确理解插件开发流程，以下是一个具体的插件开发示例：

1. **定义插件接口**
   ```kotlin
   // 完整实现，不使用简化版
   interface KAcePlugin {
       val metadata: PluginMetadata
       fun onInstall()
       fun onUninstall()
       fun onEnable()
       fun onDisable()
       fun registerServices(serviceRegistry: ServiceRegistry)
       fun registerPermissions(): List<Permission>
       fun registerMenus(): List<Menu>
       fun registerApis(): List<Api>
       fun registerResources(): List<Resource>
       fun registerTables(): List<TableDefinition>
       fun registerConfigs(): List<ConfigDefinition>
   }
   ```

2. **实现插件加载器**
   ```kotlin
   // 完整实现，包含所有错误处理
   class PluginLoader(private val pluginDir: File) {
       fun loadPlugins(): List<KAcePlugin> {
           val plugins = mutableListOf<KAcePlugin>()
           
           if (!pluginDir.exists() || !pluginDir.isDirectory) {
               throw IllegalArgumentException("Plugin directory does not exist: ${pluginDir.absolutePath}")
           }
           
           val jarFiles = pluginDir.listFiles { file -> file.extension == "jar" }
           if (jarFiles.isNullOrEmpty()) {
               return emptyList()
           }
           
           for (jarFile in jarFiles) {
               try {
                   val plugin = loadPlugin(jarFile)
                   plugins.add(plugin)
               } catch (e: Exception) {
                   logger.error("Failed to load plugin from ${jarFile.name}", e)
               }
           }
           
           return plugins
       }
       
       private fun loadPlugin(jarFile: File): KAcePlugin {
           val classLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), this.javaClass.classLoader)
           
           val manifest = JarFile(jarFile).manifest
           val pluginClass = manifest.mainAttributes.getValue("Plugin-Class") 
               ?: throw IllegalArgumentException("No Plugin-Class specified in manifest of ${jarFile.name}")
           
           val clazz = classLoader.loadClass(pluginClass)
           if (!KAcePlugin::class.java.isAssignableFrom(clazz)) {
               throw IllegalArgumentException("Class $pluginClass does not implement KAcePlugin interface")
           }
           
           return clazz.getDeclaredConstructor().newInstance() as KAcePlugin
       }
   }
   ```

3. **实现插件管理器**
   ```kotlin
   // 完整实现，包含所有必要功能
   class PluginManager(
       private val pluginLoader: PluginLoader,
       private val serviceRegistry: ServiceRegistry,
       private val permissionRegistry: PermissionRegistry,
       private val menuRegistry: MenuRegistry,
       private val apiRegistry: ApiRegistry,
       private val resourceRegistry: ResourceRegistry,
       private val tableRegistry: TableRegistry,
       private val configRegistry: ConfigRegistry
   ) {
       private val plugins = mutableMapOf<String, KAcePlugin>()
       private val enabledPlugins = mutableSetOf<String>()
       
       fun loadPlugins() {
           val loadedPlugins = pluginLoader.loadPlugins()
           for (plugin in loadedPlugins) {
               plugins[plugin.metadata.id] = plugin
           }
       }
       
       fun installPlugin(pluginId: String): Boolean {
           val plugin = plugins[pluginId] ?: return false
           
           try {
               // 注册插件提供的各种资源
               registerPluginResources(plugin)
               
               // 调用插件的安装方法
               plugin.onInstall()
               
               return true
           } catch (e: Exception) {
               logger.error("Failed to install plugin: $pluginId", e)
               // 回滚已注册的资源
               unregisterPluginResources(plugin)
               return false
           }
       }
       
       fun enablePlugin(pluginId: String): Boolean {
           if (enabledPlugins.contains(pluginId)) {
               return true
           }
           
           val plugin = plugins[pluginId] ?: return false
           
           try {
               // 注册插件提供的服务
               plugin.registerServices(serviceRegistry)
               
               // 调用插件的启用方法
               plugin.onEnable()
               
               enabledPlugins.add(pluginId)
               return true
           } catch (e: Exception) {
               logger.error("Failed to enable plugin: $pluginId", e)
               return false
           }
       }
       
       fun disablePlugin(pluginId: String): Boolean {
           if (!enabledPlugins.contains(pluginId)) {
               return true
           }
           
           val plugin = plugins[pluginId] ?: return false
           
           try {
               // 调用插件的禁用方法
               plugin.onDisable()
               
               enabledPlugins.remove(pluginId)
               return true
           } catch (e: Exception) {
               logger.error("Failed to disable plugin: $pluginId", e)
               return false
           }
       }
       
       fun uninstallPlugin(pluginId: String): Boolean {
           // 先禁用插件
           if (enabledPlugins.contains(pluginId)) {
               if (!disablePlugin(pluginId)) {
                   return false
               }
           }
           
           val plugin = plugins[pluginId] ?: return false
           
           try {
               // 调用插件的卸载方法
               plugin.onUninstall()
               
               // 取消注册插件提供的资源
               unregisterPluginResources(plugin)
               
               return true
           } catch (e: Exception) {
               logger.error("Failed to uninstall plugin: $pluginId", e)
               return false
           }
       }
       
       private fun registerPluginResources(plugin: KAcePlugin) {
           // 注册权限
           val permissions = plugin.registerPermissions()
           for (permission in permissions) {
               permissionRegistry.registerPermission(permission)
           }
           
           // 注册菜单
           val menus = plugin.registerMenus()
           for (menu in menus) {
               menuRegistry.registerMenu(menu)
           }
           
           // 注册API
           val apis = plugin.registerApis()
           for (api in apis) {
               apiRegistry.registerApi(api)
           }
           
           // 注册资源
           val resources = plugin.registerResources()
           for (resource in resources) {
               resourceRegistry.registerResource(resource)
           }
           
           // 注册表结构
           val tables = plugin.registerTables()
           for (table in tables) {
               tableRegistry.registerTable(table)
           }
           
           // 注册配置
           val configs = plugin.registerConfigs()
           for (config in configs) {
               configRegistry.registerConfig(config)
           }
       }
       
       private fun unregisterPluginResources(plugin: KAcePlugin) {
           // 实现资源取消注册的逻辑
           // ...
       }
   }
   ```

## 7. 常见问题解决方案

### 7.1 AI生成的代码不完整

**问题**：AI只生成了部分代码，或者使用了"..."来省略重要部分。

**解决方案**：
1. 明确指出代码不完整，要求AI提供完整实现
2. 将大型类分解为多个小型类，逐个实现
3. 指定具体的方法或功能，要求AI完成

**示例提示**：
```
你提供的UserService实现不完整，createUser方法中缺少密码加密和用户存在性检查。请提供这些功能的完整实现，不要使用注释或"..."来代替代码。
```

### 7.2 AI生成的代码与设计不一致

**问题**：AI生成的代码与之前讨论的设计或系统架构不一致。

**解决方案**：
1. 提供明确的设计约束和参考
2. 引用之前的设计文档或代码片段
3. 指出不一致之处，要求AI修正

**示例提示**：
```
你生成的UserController与我们的设计不一致。根据系统架构设计，所有控制器都应该：
1. 继承BaseController类
2. 使用依赖注入而不是直接创建服务实例
3. 返回统一的ResponseEntity格式

请修改代码以符合这些要求。
```

### 7.3 AI忽略了错误处理

**问题**：AI生成的代码缺少适当的错误处理和异常管理。

**解决方案**：
1. 明确要求包含错误处理
2. 列出需要处理的具体异常类型
3. 要求AI提供错误处理示例

**示例提示**：
```
请修改UserService.createUser方法，添加以下异常处理：
1. 用户名已存在 -> 抛出UserAlreadyExistsException
2. 密码不符合安全要求 -> 抛出InvalidPasswordException
3. 数据库错误 -> 捕获并包装为ServiceException

每种情况都应该有适当的日志记录和错误消息。
```

## 8. 结论

通过遵循本指南中的策略和工作流程，开发团队可以有效地利用AI助手进行KAce应用管理系统的开发，同时避免常见的陷阱和问题。关键是将任务分解为可管理的部分，明确要求完整实现，并始终验证AI生成的代码是否满足所有功能和质量要求。

记住，AI是一个工具，而不是替代品。最终的代码质量和系统设计仍然取决于开发团队的专业知识和判断。