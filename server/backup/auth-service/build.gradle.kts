plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

application {
    mainClass.set("com.kace.auth.AuthServiceApplicationKt")
}

dependencies {
    // 项目依赖
    implementation(project(":common"))
    
    // Kotlin
    implementation(Deps.Kotlin.stdlib)
    implementation(Deps.Kotlin.reflect)
    implementation(Deps.Kotlin.serialization)
    implementation(Deps.Kotlin.coroutines)
    
    // Ktor
    implementation(Deps.Ktor.server)
    implementation(Deps.Ktor.netty)
    implementation(Deps.Ktor.auth)
    implementation(Deps.Ktor.authJwt)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(Deps.Ktor.statusPages)
    implementation(Deps.Ktor.cors)
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
    
    // 安全
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jjwt}")
    implementation("io.jsonwebtoken:jjwt-impl:${Versions.jjwt}")
    implementation("io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}")
    implementation(Deps.Security.bcrypt)
    
    // Redis
    implementation(Deps.Redis.lettuce)
    implementation(Deps.Redis.kotlinx)
    
    // 测试
    testImplementation(Deps.Testing.junit)
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("io.ktor:ktor-server-test-host:${Versions.ktor}")
} 