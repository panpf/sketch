package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.request.internal.ImageData
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

sealed interface DownloadData: ImageData {

    @Throws(IOException::class)
    fun newInputStream(): InputStream

    val dataFrom: DataFrom

    class Bytes(
        val data: ByteArray,
        override val dataFrom: DataFrom
    ) : DownloadData {

        override fun newInputStream(): InputStream = ByteArrayInputStream(data)
    }

    class Cache(
        val diskCacheEntry: DiskCache.Entry,
        override val dataFrom: DataFrom
    ) : DownloadData {

        override fun newInputStream(): InputStream {
            return diskCacheEntry.newInputStream()
        }
    }
}