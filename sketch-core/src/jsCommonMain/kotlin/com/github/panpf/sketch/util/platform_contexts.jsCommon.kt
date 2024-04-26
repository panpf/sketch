package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import okio.Path

actual fun PlatformContext.appCacheDirectory(): Path? = null