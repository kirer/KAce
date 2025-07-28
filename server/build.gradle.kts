subprojects {
    group = "com.github.kirer.boss"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
    
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper> {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {

        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}