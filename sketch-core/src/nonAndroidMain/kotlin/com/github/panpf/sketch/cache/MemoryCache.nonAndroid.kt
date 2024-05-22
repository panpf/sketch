package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext


internal actual fun PlatformContext.platformDefaultMemoryCacheSizePercent(): Double {
    return 0.15
}