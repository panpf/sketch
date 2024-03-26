package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun platformDefaultDiskCacheOptions(context: PlatformContext): DiskCache.Options {
    return DiskCache.Options(
        appCacheDirectory = getCacheDirectory().toPath(),
        downloadMaxSize = 300L * 1024 * 1024,
        resultMaxSize = 200L * 1024 * 1024,
        downloadAppVersion = null,
        resultAppVersion = null,
    )
}

private fun getCacheDirectory(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
    return paths.first() as String
}