package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.Desktop

actual val Platform.isWindows: Boolean
    get() = System.getProperty("os.name").startsWith("Win")