package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.sample.util.Platform.Companion

actual val Companion.current: Platform
    get() = Platform.Macos