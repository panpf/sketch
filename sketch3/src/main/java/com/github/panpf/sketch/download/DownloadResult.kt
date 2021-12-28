package com.github.panpf.sketch.download

import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.cache.DiskCache


sealed interface DownloadResult : ImageResult

class ByteArrayDownloadResult(val data: ByteArray, val from: DataFrom) : DownloadResult

class DiskCacheDownloadResult(val diskCacheEntry: DiskCache.Entry, val from: DataFrom) : DownloadResult