package com.github.panpf.sketch.test.utils

import kotlin.js.js

@Suppress("KotlinUnreachableCode")
actual fun isGitHubActions(): Boolean {
    val isProcessDefined =
        js("typeof process !== 'undefined' && process.env !== 'undefined'") as Boolean
    return if (isProcessDefined) {
        js("process.env.GITHUB_ACTIONS") == "true"
    } else {
        false
    }
}