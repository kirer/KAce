# KAce 公共服务库 (Common Service)

## 概述

公共服务库是KAce平台的基础组件库，提供了一系列可复用的核心功能模块、工具类、数据模型和配置，用于支持其他微服务的开发。该服务库不是独立的微服务，而是一个共享的依赖库，被所有其他微服务引用，确保了代码复用、一致性和标准化实现，显著减少了重复工作并提高了系统整体质量。

## 核心功能

### 1. 异常处理
- 统一异常层次结构
- 全局异常处理机制
- 错误码和消息标准化
- 国际化错误消息支持

### 2. 通用响应模型
- 统一响应格式
- 分页模型
- 数据包装和转换
- 成功/失败响应标准化

### 3. 安全工具
- JWT令牌工具
- 加密/解密工具
- 密码哈希工具
- 安全配置类

### 4. 数据库支持
- 基础实体类
- 通用仓库接口
- 审计支持
- 数据转换工具

### 5. 公共工具集
- 日期时间工具
- 字符串处理工具
- 验证工具
- 序列化/反序列化工具

### 6. 服务发现
- 服务注册与发现接口
- 客户端负载均衡
- 服务健康检查
- 服务路由工具

### 7. 日志和监控
- 结构化日志支持
- 性能指标收集
- 跟踪ID生成和传播
- 日志上下文管理

## 主要逻辑

公共服务库采用模块化设计，每个功能区域都有明确的边界和职责。主要组件和逻辑包括：

1. **异常处理框架**：
   - 定义基础异常类层次结构 → 专用业务异常扩展 → 错误代码映射
   - 全局异常拦截器 → 异常转换 → 统一响应格式转换 → 日志记录

2. **安全框架**：
   - JWT配置 → 令牌生成 → 令牌验证 → 权限解析
   - 密码编码器 → 散列算法应用 → 安全参数配置 → 验证逻辑

3. **公共数据模型**：
   - 基础实体设计 → 审计字段整合 → ID生成策略
   - 数据传输对象映射 → 转换工具 → 验证注解

4. **工具类集合**：
   - 专用工具实现 → 静态方法封装 → 常量定义
   - 扩展函数设计 → 语言特性增强

## 代码结构

```
service-common/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── com.kace.common/
│   │   │   │   ├── exception/                       # 异常处理
│   │   │   │   │   ├── BaseException.kt
│   │   │   │   │   ├── BusinessException.kt
│   │   │   │   │   ├── ResourceNotFoundException.kt
│   │   │   │   │   ├── ValidationException.kt
│   │   │   │   │   ├── SecurityException.kt
│   │   │   │   │   └── handler/
│   │   │   │   │       ├── GlobalExceptionHandler.kt
│   │   │   │   │       └── ErrorResponseBuilder.kt
│   │   │   │   ├── model/                           # 数据模型
│   │   │   │   │   ├── response/
│   │   │   │   │   │   ├── ApiResponse.kt
│   │   │   │   │   │   ├── PageResponse.kt
│   │   │   │   │   │   ├── ErrorResponse.kt
│   │   │   │   │   │   └── SuccessResponse.kt
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── BaseEntity.kt
│   │   │   │   │   │   ├── AuditableEntity.kt
│   │   │   │   │   │   └── SoftDeleteEntity.kt
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── BaseDto.kt
│   │   │   │   │       └── converter/
│   │   │   │   │           └── DtoConverter.kt
│   │   │   │   ├── security/                        # 安全工具
│   │   │   │   │   ├── jwt/
│   │   │   │   │   │   ├── JwtConfig.kt
│   │   │   │   │   │   ├── JwtTokenProvider.kt
│   │   │   │   │   │   └── JwtTokenValidator.kt
│   │   │   │   │   ├── crypto/
│   │   │   │   │   │   ├── EncryptionService.kt
│   │   │   │   │   │   └── PasswordEncoder.kt
│   │   │   │   │   └── principal/
│   │   │   │   │       ├── UserPrincipal.kt
│   │   │   │   │       └── AnonymousPrincipal.kt
│   │   │   │   ├── util/                            # 工具类
│   │   │   │   │   ├── DateTimeUtils.kt
│   │   │   │   │   ├── StringUtils.kt
│   │   │   │   │   ├── ValidationUtils.kt
│   │   │   │   │   ├── JsonUtils.kt
│   │   │   │   │   ├── CryptoUtils.kt
│   │   │   │   │   ├── IdGenerator.kt
│   │   │   │   │   └── extensions/
│   │   │   │   │       ├── StringExtensions.kt
│   │   │   │   │       └── CollectionExtensions.kt
│   │   │   │   ├── service/                         # 通用服务接口
│   │   │   │   │   ├── discovery/
│   │   │   │   │   │   ├── ServiceDiscovery.kt
│   │   │   │   │   │   └── ServiceRegistry.kt
│   │   │   │   │   └── cache/
│   │   │   │   │       ├── CacheService.kt
│   │   │   │   │       └── CacheConfig.kt
│   │   │   │   ├── config/                          # 配置类
│   │   │   │   │   ├── DatabaseConfig.kt
│   │   │   │   │   ├── SecurityConfig.kt
│   │   │   │   │   ├── WebConfig.kt
│   │   │   │   │   └── CorsConfig.kt
│   │   │   │   ├── logging/                         # 日志组件
│   │   │   │   │   ├── LoggingInterceptor.kt
│   │   │   │   │   ├── RequestResponseLogger.kt
│   │   │   │   │   ├── LoggingContext.kt
│   │   │   │   │   └── TraceIdGenerator.kt
│   │   │   │   ├── validation/                      # 验证组件
│   │   │   │   │   ├── validator/
│   │   │   │   │   │   ├── EmailValidator.kt
│   │   │   │   │   │   ├── PhoneValidator.kt
│   │   │   │   │   │   └── PasswordValidator.kt
│   │   │   │   │   └── annotation/
│   │   │   │   │       ├── ValidEmail.kt
│   │   │   │   │       ├── ValidPhone.kt
│   │   │   │   │       └── StrongPassword.kt
│   │   │   │   └── constants/                       # 常量定义
│   │   │   │       ├── ErrorCodes.kt
│   │   │   │       ├── SecurityConstants.kt
│   │   │   │       ├── DateTimeConstants.kt
│   │   │   │       └── ApiConstants.kt
│   │   ├── resources/
│   │   │   ├── common-messages.properties           # 国际化消息
│   │   │   ├── common-messages_zh_CN.properties     # 中文消息
│   │   │   └── common-messages_en_US.properties     # 英文消息
│   └── test/                                        # 测试代码
├── build.gradle.kts                                 # 构建配置
└── README.md                                        # 文档
```

## 技术实现

- **语言与框架**: Kotlin
- **安全框架**: JWT, BCrypt
- **序列化**: Jackson / Kotlinx Serialization
- **工具库**: Apache Commons, Guava
- **验证框架**: Jakarta Validation (前身为Hibernate Validator)
- **日志框架**: SLF4J + Logback
- **测试框架**: JUnit 5, MockK

## 优势

1. **代码复用**：消除各微服务间的重复代码，遵循DRY原则。
2. **一致性保证**：确保所有微服务使用相同的异常处理、响应格式和安全机制。
3. **标准化实现**：提供最佳实践实现，减少开发者决策负担和潜在错误。
4. **易于维护**：集中式变更，一处修改多处生效，简化版本升级。
5. **开发加速**：即用即取的组件显著减少了样板代码，缩短开发周期。
6. **质量提升**：共享组件经过充分测试和验证，提高整体代码质量。
7. **学习曲线降低**：统一的模式和工具集降低了新开发者的学习门槛。

## 局限性

1. **依赖传染**：更新公共库可能需要所有微服务同步更新，增加发版协调难度。
2. **灵活性减少**：为了保持一致性，可能限制了各服务的个性化实现。
3. **版本管理复杂**：需要谨慎管理版本和兼容性，避免破坏性变更。
4. **过度工程风险**：可能尝试过度通用化，导致接口复杂或性能开销。
5. **单点故障**：核心组件的缺陷可能影响到所有依赖服务。

## 使用指南

### 1. 添加依赖
在微服务的`build.gradle.kts`文件中添加：
```kotlin
dependencies {
    implementation(project(":service-common"))
}
```

### 2. 全局异常处理
```kotlin
@RestControllerAdvice
class YourExceptionHandler : GlobalExceptionHandler() {
    // 继承默认异常处理，添加自定义异常处理方法
}
```

### 3. 使用统一响应
```kotlin
@GetMapping("/resource")
fun getResource(): ApiResponse<ResourceDto> {
    val resource = resourceService.findById(id)
    return SuccessResponse.of(resource)
}
```

### 4. JWT工具使用
```kotlin
@Service
class AuthService(private val jwtTokenProvider: JwtTokenProvider) {
    fun createToken(user: User): String {
        return jwtTokenProvider.generateToken(user.id, user.roles)
    }
}
```

### 5. 使用通用实体
```kotlin
@Entity
@Table(name = "products")
class Product : AuditableEntity() {
    @Column(nullable = false)
    lateinit var name: String
    
    // 自动继承id, createdAt, createdBy, updatedAt, updatedBy字段
}
```

## 维护与更新

### 版本控制策略
- 遵循语义化版本
- 主版本号变更表示不兼容的API修改
- 次版本号变更表示向后兼容的功能性增强
- 修订号变更表示向后兼容的问题修复

### 发布流程
1. 代码审查和验证
2. 编写详细的变更日志
3. 运行全面的单元测试和集成测试
4. 构建并发布到内部Maven仓库
5. 通知所有依赖团队

### 兼容性保证
- 避免删除公共API
- 使用弃用(deprecation)标记而非直接移除
- 保持主要版本内的向后兼容性
- 提供迁移指南和工具

## 测试

运行测试：
```bash
./gradlew :service-common:test
```

## 贡献指南

1. 确保新代码遵循现有的代码风格和模式
2. 为新功能添加完善的单元测试
3. 更新文档以反映任何变更
4. 所有提交都需要经过代码审查
5. 主要变更需要编写迁移指南 