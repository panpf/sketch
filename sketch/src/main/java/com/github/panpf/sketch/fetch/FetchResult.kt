package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.internal.HeaderBytes
import com.github.panpf.sketch.request.DataFrom

fun FetchResult(dataSource: DataSource, mimeType: String?): FetchResult =
    DefaultFetchResult(dataSource, mimeType)

/**
 * The result of [Fetcher.fetch]
 */
interface FetchResult {

    val dataSource: DataSource

    val mimeType: String?

    val from: DataFrom
        get() = dataSource.dataFrom

    val headerBytes: HeaderBytes
}

open class DefaultFetchResult constructor(
    override val dataSource: DataSource, override val mimeType: String?
) : FetchResult {

    override val headerBytes: HeaderBytes by lazy {
        val byteArray = ByteArray(1024)
        val readLength = dataSource.newInputStream().use {
            it.read(byteArray)
        }
        HeaderBytes(
            if (readLength == byteArray.size) {
                byteArray
            } else {
                byteArray.copyOf(readLength)
            }
        )
    }

    override fun toString(): String = "FetchResult(source=$dataSource,mimeType='$mimeType')"
}
