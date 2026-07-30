package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.macOS

actual val Platform.isWindows: Boolean
    get() = false
