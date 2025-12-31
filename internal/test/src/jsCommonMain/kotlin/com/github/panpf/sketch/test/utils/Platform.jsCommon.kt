package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.Web

actual val Platform.isWindows: Boolean
    get() = false