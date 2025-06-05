package com.github.kirer.kace

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.kirer.kace.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KAce",
    ) {
        App()
    }
}