plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

application {
    mainClass.set("com.kace.content.ContentServiceApplicationKt")
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
    implementation(Deps.Database.hikaricp)
    implementation(Deps.Database.postgresql)
    implementation(Deps.Database.flyway)
    
    // 依赖注入
    implementation(Deps.DI.koin)
    implementation(Deps.DI.koinKtor)
    
    // 日志
    implementation(Deps.Logging.logback)
    
    // 测试
    testImplementation(Deps.Testing.junit)
    testImplementation(Deps.Testing.kotlinTest)
    testImplementation(Deps.Testing.ktorServerTest)
    testImplementation(Deps.Testing.mockk)
} 