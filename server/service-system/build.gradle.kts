import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "com.kace"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor 服务器
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.metrics)
    
    // Koin 依赖注入
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    
    // 数据库
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    
    // 日志
    implementation(libs.logback.classic)
    implementation(libs.slf4j.api)
    
    // 序列化
    implementation(libs.kotlinx.serialization.json)
    
    // 时间处理
    implementation(libs.kotlinx.datetime)
    
    // 配置
    implementation(libs.config4k)
    
    // Redis客户端
    implementation(libs.lettuce.core)
    
    // 测试
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.mockk)
    testImplementation(libs.h2.database)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.kace.system.SystemServiceApplicationKt")
} 