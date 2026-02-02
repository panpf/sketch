package com.github.panpf.sketch.sample.com.github.panpf.sketch.sample

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.initialApp
import com.github.panpf.sketch.sample.ui.App

fun main() {
    initialApp(PlatformContext.INSTANCE)
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