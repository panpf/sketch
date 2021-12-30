package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.request.internal.ImageResult

sealed interface DownloadResult : ImageResult {
    // todo new inputStream
    // todo suspend getFile
}

class ByteArrayDownloadResult(val data: ByteArray, override val from: DataFrom) : DownloadResult

class DiskCacheDownloadResult(val diskCacheEntry: DiskCache.Entry, override val from: DataFrom) :
    DownloadResult