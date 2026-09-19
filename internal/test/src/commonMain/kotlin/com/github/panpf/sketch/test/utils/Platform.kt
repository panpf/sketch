package com.github.panpf.sketch.test.utils

enum class Platform {
    Android,
    iOS,
    macOS,
    Jvm,
    Web, ;

    companion object
}

expect val Platform.Companion.current: Platform

expect val Platform.isWindows: Boolean
