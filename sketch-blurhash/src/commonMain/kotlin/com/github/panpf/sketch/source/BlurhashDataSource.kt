package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import okio.IOException
import okio.Path
import okio.Source

class BlurhashDataSource constructor(
    val blurhash: String,
    override val dataFrom: DataFrom,
) : DataSource {

    override val key: String = blurhash

    @Throws(IOException::class)
    override fun openSource(): Source = throw UnsupportedOperationException("Not supported")

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path =
        throw UnsupportedOperationException("Not supported")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurhashDataSource
        if (dataFrom != other.dataFrom) return false
        return true
    }
}