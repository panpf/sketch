package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.cache.DiskCache
import okio.use

fun DiskCache.exist(key: String): Boolean {
    return openSnapshot(key).use { it != null }
}