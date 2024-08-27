package com.github.panpf.sketch.sample

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.sample.ui.App

const val appId = "com.github.panpf.sketch4.sample"

fun main() {
    initials()
    application {
        Window(
            title = "Sketch4",
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        ) {
            App()
        }
    }
}

private fun initials() {
    SingletonSketch.setSafe { newSketch(it) }
}