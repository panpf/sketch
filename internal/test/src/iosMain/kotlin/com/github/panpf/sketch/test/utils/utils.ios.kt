package com.github.panpf.sketch.test.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun isGitHubActions(): Boolean {
    val env = getenv("GITHUB_ACTIONS")
    return env?.toKString() == "true"
}