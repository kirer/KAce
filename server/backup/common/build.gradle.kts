plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(Deps.Kotlin.stdlib)
    implementation(Deps.Kotlin.reflect)
    implementation(Deps.Kotlin.serialization)
    implementation(Deps.Kotlin.coroutines)
    implementation(Deps.Kotlin.dateTime)
    
    // Ktor
    implementation(Deps.Ktor.server)
    implementation(Deps.Ktor.auth)
    implementation(Deps.Ktor.authJwt)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Ktor.contentNegotiation)
    
    // Database
    implementation(Deps.Database.exposedCore)
    implementation(Deps.Database.exposedDao)
    implementation(Deps.Database.exposedJdbc)
    implementation(Deps.Database.exposedJavaTime)
    implementation(Deps.Database.hikaricp)
    
    // DI
    implementation(Deps.Koin.core)
    implementation(Deps.Koin.ktor)
    
    // Logging
    implementation(Deps.Logging.logback)
    
    // Security
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jjwt}")
    implementation("io.jsonwebtoken:jjwt-impl:${Versions.jjwt}")
    implementation("io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}")
    implementation(Deps.Security.bcrypt)
    
    // Testing
    testImplementation(Deps.Testing.junit)
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
    testImplementation(Deps.Testing.kotlin)
    testImplementation("io.mockk:mockk:${Versions.mockk}")
} 