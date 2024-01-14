package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.DiskCache.Options
import com.github.panpf.sketch.cache.internal.LruDiskCache
import java.io.File

actual fun createDiskCache(
    context: PlatformContext,
    type: DiskCache.Type,
    options: Options?
): DiskCache {

    val directory = options?.directory ?: File(
        context.externalCacheDir ?: context.cacheDir,
        DiskCache.DEFAULT_DIR_NAME + File.separator + type.dirName
    )

    val maxSize = options?.maxSize ?: when (type) {
        DiskCache.Type.DOWNLOAD -> 300 * 1024 * 1024
        DiskCache.Type.RESULT -> 200 * 1024 * 1024
        else -> throw IllegalArgumentException("Unknown type: $type")
    }

    val appVersion = options?.appVersion ?: 1

    return LruDiskCache(
        context = context,
        maxSize = maxSize,
        directory = directory,
        appVersion = appVersion,
        internalVersion = type.internalVersion,
    )
}