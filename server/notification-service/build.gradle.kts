plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    application
}

group = "com.kace"
version = "0.1.0"

application {
    mainClass.set("com.kace.notification.NotificationServiceApplicationKt")
}

dependencies {
    // 项目依赖
    implementation(project(":common"))
    
    // Ktor 服务器
    implementation("io.ktor:ktor-server-core:2.3.0")
    implementation("io.ktor:ktor-server-netty:2.3.0")
} 