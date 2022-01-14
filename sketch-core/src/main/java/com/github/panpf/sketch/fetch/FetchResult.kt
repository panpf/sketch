package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.DataFrom

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult constructor(val dataSource: DataSource, val mimeType: String?) {

    val from: DataFrom = dataSource.from
    // todo mimeType 由 fetchResult 统一提供
    // todo 参考 coil 将 dataSource 改成 BufferedSource，因为有的 decoder 需要读取数据来判断

    override fun toString(): String {
        return "FetchResult(source=$dataSource,mimeType=$mimeType)"
    }
}
