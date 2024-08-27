package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.sample.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initials()
    CanvasBasedWindow("SketchSample-WASM") {
        App()
    }
}

private fun initials() {
    SingletonSketch.setSafe { newSketch(it) }
}