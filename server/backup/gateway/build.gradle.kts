import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    application
}

group = "com.kace"
version = "0.1.0"

application {
    mainClass.set("com.kace.gateway.GatewayApplicationKt")
}

dependencies {
    // 项目依赖
    implementation(project(":common"))
    
    // Ktor 服务器
    implementation(Deps.Ktor.server)
    implementation(Deps.Ktor.netty)
    implementation(Deps.Ktor.auth)
    implementation(Deps.Ktor.authJwt)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Ktor.cors)
    implementation(Deps.Ktor.statusPages)
    implementation(Deps.Ktor.contentNegotiation)
    
    // Ktor 客户端
    implementation(Deps.Ktor.client)
    implementation(Deps.Ktor.clientJvm)
    implementation(Deps.Ktor.clientJson)
    implementation(Deps.Ktor.clientLogging)
    
    // 依赖注入
    implementation(Deps.Koin.ktor)
    implementation(Deps.Koin.logger)
    
    // 日志
    implementation(Deps.Logging.logback)
    
    // Redis (用于限流)
    implementation(Deps.Redis.lettuce)
    implementation(Deps.Redis.kotlinx)
    
    // 配置
    implementation(Deps.Config.hocon)
    
    // 测试
    testImplementation(Deps.Testing.ktor)
    testImplementation(Deps.Testing.kotlin)
    testImplementation(Deps.Testing.logback)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
} 