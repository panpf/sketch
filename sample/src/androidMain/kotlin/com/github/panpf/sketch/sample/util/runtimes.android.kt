package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.sample.BuildConfig

actual fun isDebugMode(): Boolean = BuildConfig.DEBUG

actual val Platform.Companion.current: Platform
    get() = Platform.Android