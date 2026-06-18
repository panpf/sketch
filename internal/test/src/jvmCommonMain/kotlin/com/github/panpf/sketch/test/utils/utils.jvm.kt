package com.github.panpf.sketch.test.utils

actual fun isGitHubActions(): Boolean {
    return System.getenv("GITHUB_ACTIONS") == "true"
}