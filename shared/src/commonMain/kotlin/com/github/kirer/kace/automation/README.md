# KAce 自动化模块

这个模块提供跨平台的UI自动化能力，支持Android、iOS、桌面和Web平台。

## 基本用法

### 初始化控制器

```kotlin
// 创建自动化控制器
val controller = UnifiedAutomationController()

// 在Android平台，你需要初始化AccessibilityService
// controller.initialize(accessibilityService)
```

### 查找元素

```kotlin
// 根据ID查找
val element = controller.findElementById("login_button")

// 根据文本查找
val element = controller.findElementByText("登录")

// 根据类名查找
val element = controller.findElementByClassName("android.widget.Button")

// 使用选择器查找
val element = controller.selector()
    .textContains("登录")
    .clickable()
    .findElement()
```

### 基本操作

```kotlin
// 点击元素
controller.click(element)

// 长按元素
controller.longClick(element)

// 点击坐标
controller.click(Point(100, 200))

// 输入文本
controller.inputText(element, "Hello World")

// 滑动操作
controller.swipe(Point(100, 500), Point(100, 200))

// 截图
val screenshot = controller.captureScreen()
```

### 等待操作

```kotlin
// 等待元素出现
val element = controller.waitForElement(
    selector = controller.selector().text("加载完成"),
    timeout = 10000,  // 10秒超时
    interval = 500    // 每500毫秒检查一次
)

// 等待元素消失
val isGone = controller.waitForElementGone(
    selector = controller.selector().text("正在加载"),
    timeout = 10000,
    interval = 500
)
```

### 高级手势

```kotlin
// 创建滑动手势
val swipeGesture = Gesture.Swipe(
    start = Point(100, 500),
    end = Point(100, 200),
    duration = 300,
    steps = 10
)

// 执行手势
controller.performGesture(swipeGesture)
```

## 平台特定功能

### Android

Android实现使用AccessibilityService来执行UI自动化操作。

```kotlin
// 初始化控制器
val controller = UnifiedAutomationController()
controller.initialize(accessibilityService)

// 系统操作
controller.back()    // 返回
controller.home()    // 主页
controller.recents() // 最近任务
```

### 桌面平台

桌面实现使用Java AWT Robot来执行桌面自动化操作。

```kotlin
// 按键操作
controller.pressKey(KeyEvent.VK_ENTER)
```

### iOS

iOS实现需要与XCTest框架集成，提供基本的自动化操作。

### Web

Web实现使用JavaScript DOM API来执行Web自动化操作。

```kotlin
// 导航操作
controller.back()  // 浏览器后退
controller.home()  // 回到网站首页
```

## 完整示例

以下是一个完整的登录流程示例：

```kotlin
fun login(username: String, password: String) {
    val controller = UnifiedAutomationController()
    
    // 查找用户名输入框
    val usernameField = controller.findElementById("username_field")
    if (usernameField != null) {
        controller.click(usernameField)
        controller.inputText(username)
    }
    
    // 查找密码输入框
    val passwordField = controller.findElementById("password_field")
    if (passwordField != null) {
        controller.click(passwordField)
        controller.inputText(password)
    }
    
    // 查找并点击登录按钮
    val loginButton = controller.findElementByText("登录")
    if (loginButton != null) {
        controller.click(loginButton)
    }
    
    // 等待登录成功
    val successElement = controller.waitForElement(
        controller.selector().textContains("登录成功"),
        timeout = 5000
    )
    
    if (successElement != null) {
        println("登录成功!")
    } else {
        println("登录失败或超时")
    }
}
``` 