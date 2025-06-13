object Versions {
    const val kotlin = "1.9.0"
    const val ktor = "2.3.0"
    const val exposed = "0.45.0"
    const val koin = "3.5.0"
    const val logback = "1.4.11"
    const val postgresql = "42.6.0"
    const val hikaricp = "5.0.1"
    const val flyway = "9.20.0"
    const val junit = "5.10.0"
    const val lettuce = "6.3.0.RELEASE"
    const val kotlinxCoroutines = "1.7.3"
    const val kotlinxSerialization = "1.6.0"
    const val kotlinxDateTime = "0.4.1"
    const val bcrypt = "0.10.2"
    const val hocon = "1.4.3"
    const val kotest = "5.7.2"
    const val mockk = "1.13.7"
    const val jjwt = "0.11.5"
    const val redis = "4.4.3"
    const val rabbitmq = "5.18.0"
    const val elasticsearch = "8.10.0"
    const val minio = "8.5.6"
}

object Deps {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinxDateTime}"
    }
    
    object Ktor {
        const val server = "io.ktor:ktor-server-core:${Versions.ktor}"
        const val netty = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val auth = "io.ktor:ktor-server-auth:${Versions.ktor}"
        const val authJwt = "io.ktor:ktor-server-auth-jwt:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
        const val client = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val clientJvm = "io.ktor:ktor-client-cio:${Versions.ktor}"
        const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val cors = "io.ktor:ktor-server-cors:${Versions.ktor}"
        const val statusPages = "io.ktor:ktor-server-status-pages:${Versions.ktor}"
        const val contentNegotiation = "io.ktor:ktor-server-content-negotiation:${Versions.ktor}"
        const val metrics = "io.ktor:ktor-server-metrics:${Versions.ktor}"
        const val callLogging = "io.ktor:ktor-server-call-logging:${Versions.ktor}"
    }
    
    object Database {
        const val exposedCore = "org.jetbrains.exposed:exposed-core:${Versions.exposed}"
        const val exposedDao = "org.jetbrains.exposed:exposed-dao:${Versions.exposed}"
        const val exposedJdbc = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}"
        const val exposedJavaTime = "org.jetbrains.exposed:exposed-java-time:${Versions.exposed}"
        const val postgresql = "org.postgresql:postgresql:${Versions.postgresql}"
        const val hikaricp = "com.zaxxer:HikariCP:${Versions.hikaricp}"
        const val flyway = "org.flywaydb:flyway-core:${Versions.flyway}"
    }
    
    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val ktor = "io.insert-koin:koin-ktor:${Versions.koin}"
        const val logger = "io.insert-koin:koin-logger-slf4j:${Versions.koin}"
    }
    
    object Logging {
        const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
    }
    
    object Redis {
        const val lettuce = "io.lettuce:lettuce-core:${Versions.lettuce}"
        const val kotlinx = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.kotlinxCoroutines}"
    }
    
    object Security {
        const val bcrypt = "at.favre.lib:bcrypt:${Versions.bcrypt}"
    }
    
    object Config {
        const val hocon = "com.typesafe:config:${Versions.hocon}"
    }
    
    object Testing {
        const val junit = "org.junit.jupiter:junit-jupiter:${Versions.junit}"
        const val ktor = "io.ktor:ktor-server-test-host:${Versions.ktor}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        const val koin = "io.insert-koin:koin-test:${Versions.koin}"
        const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
    }
} 