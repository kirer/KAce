plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("com.kace.gateway.GatewayApplicationKt")
}

dependencies {
    // 项目依赖
    implementation(project(":service-common"))
    
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    
    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    
    // Redis
    implementation(libs.lettuce.core)
    implementation(libs.kotlinx.coroutines.reactive)
    
    // 依赖注入
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    
    // 日志
    implementation(libs.logback.classic)
    
    // 测试
    testImplementation(libs.junit5.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test)
    testImplementation(libs.mockk)
}
