package com.github.panpf.sketch3.download

import com.github.panpf.sketch3.common.cache.disk.DiskCache


sealed interface DownloadData

class BytesDownloadData(
    val imageData: ByteArray,
) : DownloadData

class CacheDownloadData(
    val diskCacheEntry: DiskCache.Entry,
) : DownloadData