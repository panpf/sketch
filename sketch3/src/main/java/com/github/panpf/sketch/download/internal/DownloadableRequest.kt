package com.github.panpf.sketch.download.internal

import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.cache.CachePolicy

interface DownloadableRequest : ImageRequest {
    val diskCacheKey: String
    val diskCachePolicy: CachePolicy
    // todo httpHeaders: Map<String, String>
}