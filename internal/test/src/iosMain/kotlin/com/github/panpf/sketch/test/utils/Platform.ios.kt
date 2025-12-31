package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.iOS

actual val Platform.isWindows: Boolean
    get() = false