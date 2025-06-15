rootProject.name = "KAce"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            maven(url="https://maven.aliyun.com/repository/public/")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url="https://maven.aliyun.com/repository/public/")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app-android")
include(":app-desktop")
include(":app-web")
include(":app-ios")
include(":shared")

// Server modules aliases (without server prefix)
include(":service-common")
include(":service-auth")
include(":service-gateway")
include(":service-user")
include(":service-content")
include(":service-media")
include(":service-analytics")
include(":service-notification")
include(":service-plugin")

// Plugin modules aliases
include(":service-plugin:plugin-api")
include(":service-plugin:plugin-article")
include(":service-plugin:plugin-product")
include(":service-plugin:plugin-event")

// 设置项目别名
project(":service-common").projectDir = file("server/service-common")
project(":service-auth").projectDir = file("server/service-auth")
project(":service-gateway").projectDir = file("server/service-gateway")
project(":service-user").projectDir = file("server/service-user")
project(":service-content").projectDir = file("server/service-content")
project(":service-media").projectDir = file("server/service-media")
project(":service-analytics").projectDir = file("server/service-analytics")
project(":service-notification").projectDir = file("server/service-notification")
project(":service-plugin").projectDir = file("server/service-plugin")

// 设置插件模块别名
project(":service-plugin:plugin-api").projectDir = file("server/service-plugin/plugin-api")
project(":service-plugin:plugin-article").projectDir = file("server/service-plugin/plugin-article")
project(":service-plugin:plugin-product").projectDir = file("server/service-plugin/plugin-product")
project(":service-plugin:plugin-event").projectDir = file("server/service-plugin/plugin-event")
