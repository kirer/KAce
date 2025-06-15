plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.kace"
version = "0.1.0"

dependencies {
    // Kotlin标准库
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
} 