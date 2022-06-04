package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DataFrom
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
        val diskCacheSnapshot: DiskCache.Snapshot,
        override val dataFrom: DataFrom
    ) : DownloadData {

        override fun newInputStream(): InputStream = diskCacheSnapshot.newInputStream()
    }
}