package com.github.panpf.sketch3.download

import com.github.panpf.sketch3.common.cache.disk.DiskCache


sealed interface DownloadData

class ByteArrayDownloadData(
    val data: ByteArray,
) : DownloadData

class DiskCacheDownloadData(
    val diskCacheEntry: DiskCache.Entry,
) : DownloadData