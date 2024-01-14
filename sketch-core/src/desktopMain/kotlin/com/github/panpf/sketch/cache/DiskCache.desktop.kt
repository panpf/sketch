package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.DiskCache.Options
import com.github.panpf.sketch.cache.internal.EmptyDiskCache
import java.io.File

actual fun createDiskCache(
    context: PlatformContext,
    type: DiskCache.Type,
    options: Options?
): DiskCache {
    return EmptyDiskCache(
        maxSize = options?.maxSize ?: 0L,
        directory = options?.directory ?: File("/tmp/${DiskCache.DEFAULT_DIR_NAME}"),
    )
}