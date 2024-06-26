package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import okio.Path

/** Return the global application context. */
internal expect val PlatformContext.application: PlatformContext

/** Return the application's total memory in bytes. */
internal expect fun PlatformContext.totalAvailableMemoryBytes(): Long

expect fun PlatformContext.appCacheDirectory(): Path?

expect fun PlatformContext.screenSize(): Size