package com.github.kirer.kace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform