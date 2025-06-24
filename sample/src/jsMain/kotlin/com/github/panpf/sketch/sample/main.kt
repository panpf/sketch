package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.App
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() = onWasmReady {
    initialApp(PlatformContext.INSTANCE)
    CanvasBasedWindow("SketchSample") {
        App()
    }
}