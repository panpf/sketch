package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext

actual fun platformDefaultDiskCacheOptions(context: PlatformContext): DiskCache.Options? {
    return DiskCache.Options(
        // Unable to build the cache directory because we don’t know the package name of the desktop app.
        appCacheDirectory = null,
        downloadMaxSize = 300L * 1024 * 1024,
        resultMaxSize = 200L * 1024 * 1024,
        downloadAppVersion = null,
        resultAppVersion = null,
    )
}