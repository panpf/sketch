package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import okio.Path.Companion.toOkioPath
import java.io.File

actual fun defaultDiskCacheOptions(
    context: PlatformContext,
    type: DiskCache.Type,
): DiskCache.Options {
    // TODO - Use a better directory
    val directory = File("/tmp/${DiskCache.DEFAULT_DIR_NAME}/${type.dirName}")

    val maxSize = when (type) {
        DiskCache.Type.DOWNLOAD -> 300L * 1024 * 1024
        DiskCache.Type.RESULT -> 200L * 1024 * 1024
        else -> throw IllegalArgumentException("Unknown type: $type")
    }

    return DiskCache.Options(
        directory = directory.toOkioPath(),
        maxSize = maxSize,
    )
}