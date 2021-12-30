package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.request.internal.ImageResult
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

sealed interface DownloadResult : ImageResult {

    @Throws(IOException::class)
    fun newInputStream(): InputStream
}

class ByteArrayDownloadResult(
    val data: ByteArray,
    override val from: DataFrom
) : DownloadResult {

    override fun newInputStream(): InputStream = ByteArrayInputStream(data)
}

class DiskCacheDownloadResult(
    val diskCacheEntry: DiskCache.Entry,
    override val from: DataFrom
) : DownloadResult {

    override fun newInputStream(): InputStream {
        return diskCacheEntry.newInputStream()
    }
}