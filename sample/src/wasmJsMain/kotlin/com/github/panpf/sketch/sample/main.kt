package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initialApp(PlatformContext.INSTANCE)
    CanvasBasedWindow("SketchSample-WASM") {
        App()
    }
}