package com.github.panpf.sketch.download.internal

import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.cache.CachePolicy

interface DownloadableRequest : ImageRequest {
    val httpHeaders: Map<String, String>?
    val diskCacheKey: String
    val diskCachePolicy: CachePolicy
}