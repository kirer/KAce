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
    mainClass.set("com.kace.user.UserServiceApplicationKt")
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
    implementation(Deps.Ktor.callLogging)
    
    // 数据库
    implementation(Deps.Database.exposedCore)
    implementation(Deps.Database.exposedDao)
    implementation(Deps.Database.exposedJdbc)
    implementation(Deps.Database.exposedJavaTime)
    implementation(Deps.Database.postgresql)
    implementation(Deps.Database.hikaricp)
    implementation(Deps.Database.flyway)
    
    // 依赖注入
    implementation(Deps.Koin.core)
    implementation(Deps.Koin.ktor)
    implementation(Deps.Koin.logger)
    
    // 日志
    implementation(Deps.Logging.logback)
    
    // 序列化
    implementation(Deps.Kotlin.serialization)
    
    // 协程
    implementation(Deps.Kotlin.coroutines)
    
    // 安全
    implementation(Deps.Security.bcrypt)
    
    // 配置
    implementation(Deps.Config.hocon)
    
    // 测试
    testImplementation(Deps.Testing.junit)
    testImplementation(Deps.Testing.ktor)
    testImplementation(Deps.Testing.kotlin)
    testImplementation(Deps.Testing.koin)
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