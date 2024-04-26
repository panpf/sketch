package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext

actual fun platformDefaultDiskCacheMaxSize(context: PlatformContext): Long? = 500L * 1024 * 1024