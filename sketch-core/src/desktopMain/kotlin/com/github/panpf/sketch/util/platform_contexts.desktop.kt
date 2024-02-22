package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext


internal actual fun PlatformContext.defaultMemoryCacheSizePercent(): Double {
    return 0.15
}

// TODO: Compute the total available memory on non-Android platforms.
internal actual fun PlatformContext.totalAvailableMemoryBytes(): Long {
    return 512L * 1024L * 1024L // 512 MB
}