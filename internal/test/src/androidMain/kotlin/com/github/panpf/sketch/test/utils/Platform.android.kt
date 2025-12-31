package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.Android

actual val Platform.isWindows: Boolean
    get() = false