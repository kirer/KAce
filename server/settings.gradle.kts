rootProject.name = "kace-server"

include(":service-common")
include(":service-auth")
include(":service-gateway")
include(":service-user")
include(":service-content")
include(":service-media")
include(":service-analytics")
include(":service-notification")

include(
    ":service-plugin:plugin-api",
    ":service-plugin:plugin-article",
    ":service-plugin:plugin-product",
    ":service-plugin:plugin-event"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
} 