package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.sample.ui.App
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initials()
    CanvasBasedWindow("SketchSample-WASM") {
        App()
    }
}

private fun initials() {
    startKoin {
        modules(commonModule(PlatformContext.INSTANCE))
        modules(platformModule(PlatformContext.INSTANCE))
    }
    SingletonSketch.setSafe { newSketch(it) }
}