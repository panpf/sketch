package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import okio.Path.Companion.toOkioPath

actual fun platformDefaultDiskCacheOptions(
    context: PlatformContext,
): DiskCache.Options? {
    val appCacheDirectory = context.externalCacheDir ?: context.cacheDir
    return DiskCache.Options(
        appCacheDirectory = appCacheDirectory.toOkioPath(),
        downloadMaxSize = 300L * 1024 * 1024,
        resultMaxSize = 200L * 1024 * 1024,
        downloadAppVersion = null,
        resultAppVersion = null,
    )
}