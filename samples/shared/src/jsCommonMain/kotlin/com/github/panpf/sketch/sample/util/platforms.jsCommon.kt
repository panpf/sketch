package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import kotlinx.browser.window
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual fun copyToClipboard(context: PlatformContext, text: String) {
    window.navigator.clipboard.writeText(text)
}