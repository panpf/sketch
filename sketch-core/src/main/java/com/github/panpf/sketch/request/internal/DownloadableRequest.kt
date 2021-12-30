package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.cache.CachePolicy

interface DownloadableRequest : ImageRequest {
    val httpHeaders: Map<String, String>?
    val diskCacheKey: String
    val diskCachePolicy: CachePolicy
}