package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.DataFrom

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult constructor(val dataSource: DataSource, val mimeType: String?) {

    val from: DataFrom = dataSource.from

    val imageInfo: ImageInfo? by lazy {
        dataSource.readImageInfoWithBitmapFactoryOrNull()
    }

    override fun toString(): String = "FetchResult(source=$dataSource,mimeType='$mimeType')"
}
