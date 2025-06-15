subprojects {
    group = "com.kace"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
    
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper> {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}