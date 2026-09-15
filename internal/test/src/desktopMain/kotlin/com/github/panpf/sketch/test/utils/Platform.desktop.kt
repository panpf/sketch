package com.github.panpf.sketch.test.utils

actual val Platform.Companion.current: Platform
    get() = Platform.Desktop

actual val Platform.isWindows: Boolean
    get() = System.getProperty("os.name").contains("win")

val Platform.isMacOS: Boolean
    get() = System.getProperty("os.name")
        .takeIf { it.contains("mac") || it.contains("darwin") } != null

val Platform.isLinux: Boolean
    get() = System.getProperty("os.name").contains("linux")