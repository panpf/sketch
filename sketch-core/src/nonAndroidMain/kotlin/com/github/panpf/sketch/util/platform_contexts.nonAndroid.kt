package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext

/** Return the global application context. */
internal actual val PlatformContext.application: PlatformContext
    get() = this

// TODO: Compute the total available memory on non-Android platforms.
internal actual fun PlatformContext.totalAvailableMemoryBytes(): Long {
    return 512L * 1024L * 1024L // 512 MB
}