package com.github.panpf.sketch.download

import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.ImageData
import com.github.panpf.sketch.common.cache.disk.DiskCache


sealed interface DownloadData : ImageData

class ByteArrayDownloadData(val data: ByteArray, val from: DataFrom) : DownloadData

class DiskCacheDownloadData(val diskCacheEntry: DiskCache.Entry, val from: DataFrom) : DownloadData