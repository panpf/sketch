package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import okio.Path

// Unable to build the cache directory because we don’t know the package name of the desktop app.
actual fun PlatformContext.appCacheDirectory(): Path? = null