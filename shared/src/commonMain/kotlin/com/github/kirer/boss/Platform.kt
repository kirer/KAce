package com.github.kirer.boss

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform