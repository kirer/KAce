This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop, Server.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.

# KAce - 跨平台自动化应用框架

> 基于 Kotlin Multiplatform 的跨平台自动化应用框架，支持 Android、iOS、Windows、macOS 等多个平台的 UI 自动化操作。

## 🚀 项目简介

KAce 是一个强大的跨平台自动化应用框架，旨在提供统一的 API 来实现不同平台的 UI 自动化操作。项目采用 Kotlin Multiplatform (KMP) 技术，支持原生 API 和 Frida 两种自动化方案，同时集成图像识别和 OCR 功能。

### ✨ 核心特性

- 🔄 **跨平台支持**: Android、iOS、Windows、macOS 统一 API
- 🎯 **多种自动化方案**: 原生 API + Frida Hook 双重支持
- 🖼️ **图像识别**: 支持模板匹配、颜色检测、OCR 文字识别
- 🔒 **最小权限原则**: 每个平台使用最低权限要求的方案
- 📝 **脚本化**: 支持 JavaScript 脚本和原生 Kotlin 脚本
- 🌐 **远程控制**: 支持 REST API 和 WebSocket 远程操作
- 📱 **现代 UI**: 基于 Compose Multiplatform 的现代化界面

## 🏗️ 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application Layer)                │
├─────────────────┬─────────────────┬─────────────────────────┤
│   用户界面        │    REST API     │      命令行接口          │
│ Compose MP      │   Ktor Server   │      CLI Tool          │
└─────────────────┴─────────────────┴─────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 业务逻辑层 (Business Logic Layer)            │
├─────────────────┬─────────────────┬─────────────────────────┤
│   脚本管理器      │    执行引擎       │      结果管理器          │
│ Script Manager  │ Execution Engine│   Result Manager       │
└─────────────────┴─────────────────┴─────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│              自动化抽象层 (Automation Abstraction Layer)      │
├─────────────────┬─────────────────┬─────────────────────────┤
│   原生自动化      │   Frida自动化    │      图像识别            │
│ Native Auto     │  Frida Auto     │  Image Recognition     │
└─────────────────┴─────────────────┴─────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│               平台适配层 (Platform Adaptation Layer)         │
├──────────┬──────────┬──────────┬──────────────────────────┤
│ Android  │   iOS    │ Windows  │         macOS            │
├──────────┼──────────┼──────────┼──────────────────────────┤
│Accessibility│XCTest │UI Auto   │    Accessibility API     │
│ADB+Shizuku │Frida   │Frida     │        Frida             │
└──────────┴──────────┴──────────┴──────────────────────────┘
```

### 核心组件

#### 1. 统一自动化控制器 (Unified Automation Controller)
- 提供跨平台统一的自动化 API
- 支持元素定位、点击、滑动、输入等操作
- 自动选择最适合的底层实现方案

#### 2. Frida 集成模块
- 支持动态代码注入和 Hook
- JavaScript 脚本执行引擎
- 进程附加和会话管理

#### 3. 图像识别引擎
- 模板匹配算法
- 颜色检测和像素分析
- OCR 文字识别
- 截图和图像处理

## 🛠️ 技术栈

### 核心技术
- **Kotlin Multiplatform**: 跨平台代码共享
- **Compose Multiplatform**: 现代化 UI 框架
- **Ktor**: 网络通信和服务端
- **SQLDelight**: 跨平台数据库
- **Kotlinx.coroutines**: 异步编程
- **Kotlinx.serialization**: 数据序列化

### 平台特定技术

| 平台 | 原生自动化 | Frida支持 | 权限要求 |
|------|-----------|-----------|----------|
| Android | AccessibilityService + ADB/Shizuku | ✅ | 辅助功能权限 |
| iOS | XCTest Framework | ✅ | 开发者证书 |
| Windows | UI Automation API | ✅ | 用户级权限 |
| macOS | Accessibility API | ✅ | 辅助功能权限 |

### 第三方库
- **OpenCV**: 图像处理和计算机视觉
- **Frida**: 动态分析和代码注入
- **Tesseract**: OCR 文字识别引擎
- **JNA**: Java 原生接口访问

## 📁 项目结构

```
KAce/
├── shared/                     # 共享模块 (KMP)
│   ├── src/
│   │   ├── commonMain/kotlin/  # 通用代码
│   │   │   ├── core/          # 核心模块
│   │   │   ├── automation/    # 自动化核心
│   │   │   ├── image/         # 图像处理
│   │   │   ├── frida/         # Frida 相关
│   │   │   ├── script/        # 脚本管理
│   │   │   ├── network/       # 网络通信
│   │   │   ├── storage/       # 数据存储
│   │   │   └── utils/         # 工具类
│   │   ├── androidMain/       # Android 特定实现
│   │   ├── iosMain/           # iOS 特定实现
│   │   ├── desktopMain/       # 桌面平台实现
│   │   └── commonTest/        # 通用测试
│   └── build.gradle.kts
├── androidApp/                 # Android 应用
│   ├── src/main/
│   │   ├── kotlin/
│   │   │   ├── services/      # 系统服务
│   │   │   ├── ui/            # 用户界面
│   │   │   └── utils/         # 工具类
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── iosApp/                     # iOS 应用
│   ├── iosApp/
│   │   ├── ContentView.swift
│   │   ├── Services/          # 系统服务
│   │   ├── UI/                # 用户界面
│   │   └── Utils/             # 工具类
│   └── iosApp.xcodeproj/
├── desktopApp/                 # 桌面应用
│   ├── src/jvmMain/kotlin/
│   │   ├── ui/                # 用户界面
│   │   ├── services/          # 系统服务
│   │   └── utils/             # 工具类
│   └── build.gradle.kts
├── server/                     # 服务端 (可选)
│   ├── src/main/kotlin/
│   │   ├── routes/            # API 路由
│   │   ├── plugins/           # 插件配置
│   │   └── services/          # 业务服务
│   └── build.gradle.kts
├── cli/                        # 命令行工具
│   ├── src/main/kotlin/
│   │   ├── commands/          # 命令实现
│   │   └── utils/             # 工具类
│   └── build.gradle.kts
├── docs/                       # 文档
├── scripts/                    # 构建脚本
├── examples/                   # 示例项目
└── README.md
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Android Studio**: 最新版本 (用于 Android 开发)
- **Xcode**: 最新版本 (用于 iOS 开发，仅 macOS)
- **Gradle**: 8.0 或更高版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/KAce.git
   cd KAce
   ```

2. **安装依赖**
   ```bash
   ./gradlew build
   ```

3. **运行桌面应用**
   ```bash
   ./gradlew :desktopApp:run
   ```

4. **构建 Android 应用**
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

5. **构建 iOS 应用** (仅 macOS)
   ```bash
   cd iosApp
   xcodebuild -scheme iosApp -configuration Debug
   ```

### Frida 环境配置

#### Android
```bash
# 下载并推送 frida-server
adb push frida-server-16.1.4-android-arm64 /data/local/tmp/frida-server
adb shell chmod 755 /data/local/tmp/frida-server
adb shell /data/local/tmp/frida-server &
```

#### iOS (需要越狱设备)
```bash
# 通过 Cydia 安装 Frida
# 或通过 SSH 安装
ssh root@<device-ip>
apt update && apt install frida
```

#### Desktop
```bash
# 安装 frida-tools
pip install frida-tools
```

## 📖 使用示例

### 基础自动化操作

```kotlin
// 初始化自动化控制器
val controller = UnifiedAutomationController()

// 获取元素信息
val element = controller.getElementInfo("button_login")
if (element != null) {
    // 点击登录按钮
    controller.click(element.bounds.centerX, element.bounds.centerY)
}

// 输入文本
controller.input("username@example.com")

// 滑动操作
controller.swipe(100, 500, 100, 200)

// 截图
val screenshot = controller.captureScreen()
```

### Frida 脚本执行

```kotlin
// 初始化 Frida 控制器
val fridaController = FridaController()

// 附加到目标进程
val session = fridaController.attachToProcess("com.example.app")

// 执行 JavaScript 脚本
val script = """
    Java.perform(function() {
        var MainActivity = Java.use("com.example.MainActivity");
        MainActivity.onCreate.implementation = function(savedInstanceState) {
            console.log("MainActivity.onCreate called!");
            return this.onCreate(savedInstanceState);
        };
    });
"""

val result = fridaController.executeScript(script)
if (result.success) {
    println("脚本执行成功: ${result.data}")
} else {
    println("脚本执行失败: ${result.error}")
}
```

### 图像识别

```kotlin
// 初始化图像匹配器
val imageMatcher = ImageMatcher()

// 模板匹配
val templateImage = loadImageFromAssets("login_button.png")
val matches = imageMatcher.findImageInScreen(templateImage)

if (matches.isNotEmpty()) {
    val firstMatch = matches.first()
    controller.click(firstMatch.x, firstMatch.y)
}

// 颜色检测
val color = imageMatcher.getPixelColor(100, 200)
if (color == Color.RED) {
    println("检测到红色像素")
}

// OCR 文字识别
val text = imageMatcher.performOCR(Rect(0, 0, 500, 100))
println("识别到的文字: $text")
```

## 🔧 配置说明

### Android 配置

1. **权限配置** (`AndroidManifest.xml`)
   ```xml
   <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
   <uses-permission android:name="android.permission.ACCESSIBILITY_SERVICE" />
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   ```

2. **无障碍服务配置** (`accessibility_service_config.xml`)
   ```xml
   <accessibility-service
       android:accessibilityEventTypes="typeAllMask"
       android:accessibilityFlags="flagDefault"
       android:accessibilityFeedbackType="feedbackGeneric"
       android:canRetrieveWindowContent="true" />
   ```

### iOS 配置

1. **权限配置** (`Info.plist`)
   ```xml
   <key>NSAppleEventsUsageDescription</key>
   <string>需要访问系统事件以实现自动化功能</string>
   ```

2. **开发者证书配置**
  - 需要有效的开发者证书
  - 启用相关的 Capabilities

## 🤝 贡献指南

我们欢迎所有形式的贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详细信息。

### 开发流程

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范

- 遵循 Kotlin 官方编码规范
- 使用有意义的变量和函数名
- 添加必要的注释和文档
- 编写单元测试

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - 跨平台开发框架
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - 现代化 UI 框架
- [Frida](https://frida.re/) - 动态分析工具
- [OpenCV](https://opencv.org/) - 计算机视觉库

## 📞 联系我们

- 项目主页: [https://github.com/your-username/KAce](https://github.com/your-username/KAce)
- 问题反馈: [Issues](https://github.com/your-username/KAce/issues)
- 讨论交流: [Discussions](https://github.com/your-username/KAce/discussions)

---

**KAce** - 让跨平台自动化变得简单而强大！ 🚀