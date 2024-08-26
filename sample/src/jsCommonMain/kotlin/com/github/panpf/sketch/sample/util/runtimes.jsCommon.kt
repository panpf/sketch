package com.github.panpf.sketch.sample.util

actual fun isDebugMode(): Boolean = true

actual val Platform.Companion.current: Platform
    get() = Platform.Js