package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext

// Disk caching is not supported
actual fun platformDefaultDiskCacheMaxSize(context: PlatformContext): Long? = null