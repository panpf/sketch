package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DataFrom
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

data class DownloadData(val data: Data, val dataFrom: DataFrom) : ImageData {

    constructor(bytes: ByteArray, dataFrom: DataFrom)
            : this(ByteArrayData(bytes), dataFrom)

    constructor(snapshot: DiskCache.Snapshot, dataFrom: DataFrom)
            : this(DiskCacheData(snapshot), dataFrom)

    sealed interface Data {
        @Throws(IOException::class)
        fun newInputStream(): InputStream
    }

    data class ByteArrayData(@Suppress("ArrayInDataClass") val bytes: ByteArray) : Data {

        override fun newInputStream(): InputStream = ByteArrayInputStream(bytes)
    }

    data class DiskCacheData(val snapshot: DiskCache.Snapshot) : Data {

        override fun newInputStream(): InputStream = snapshot.newInputStream()
    }
}