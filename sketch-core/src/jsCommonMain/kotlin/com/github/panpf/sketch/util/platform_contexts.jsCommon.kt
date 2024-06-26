package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import okio.Path

actual fun PlatformContext.appCacheDirectory(): Path? = null

actual fun PlatformContext.screenSize(): Size {
    return Size(1920, 1080) // TODO I hope there is another way to get the screen size
}