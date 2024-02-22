package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext

/** Return the default percent of the application's total memory to use for the memory cache. */
internal expect fun PlatformContext.defaultMemoryCacheSizePercent(): Double

/** Return the application's total memory in bytes. */
internal expect fun PlatformContext.totalAvailableMemoryBytes(): Long