#!/bin/bash

# 为每个服务生成build.gradle.kts文件
generate_build_file() {
  service=$1
  service_name=$(echo $service | sed 's/service-//')
  
  echo "为 $service 生成 build.gradle.kts 文件"
  
  # 创建build.gradle.kts文件
  cat > "$service/build.gradle.kts" << EOF
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("com.kace.$service_name.${service_name^}ServiceApplicationKt")
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
    
    // 数据库
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    
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
EOF
  
  echo "已生成 $service/build.gradle.kts"
}

# 生成各服务的build.gradle.kts文件
generate_build_file "service-gateway"
generate_build_file "service-auth"
generate_build_file "service-content"
generate_build_file "service-user"
generate_build_file "service-media"
generate_build_file "service-notification"
generate_build_file "service-analytics"

# 为service-common生成特殊的build.gradle.kts文件
echo "为 service-common 生成 build.gradle.kts 文件"
cat > "service-common/build.gradle.kts" << EOF
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    
    // 日志
    implementation(libs.logback.classic)
    
    // 测试
    testImplementation(libs.junit5.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}
EOF
echo "已生成 service-common/build.gradle.kts"

echo "所有build.gradle.kts文件生成完成！" 