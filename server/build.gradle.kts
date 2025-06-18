plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
}

allprojects {
    group = "com.kace"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

// 设置Java版本
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // Ktor 核心依赖
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverAuthJwt)
    implementation(libs.ktor.serverCors)
    implementation(libs.ktor.serverStatusPages)
    implementation(libs.ktor.serverCallLogging)
    implementation(libs.ktor.serializationKotlinxJson)
    implementation(libs.ktor.serverSwagger)
    
    // Koin 依赖注入
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    
    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.javaTime)
    
    // 数据库驱动
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.mysql)
    
    // 日志
    implementation(libs.logback)
    implementation(libs.slf4j.api)
    
    // 配置管理
    implementation(libs.config)
    
    // 序列化
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    
    // 协程
    implementation(libs.kotlinx.coroutines.core)
    
    // 测试依赖
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.test)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}