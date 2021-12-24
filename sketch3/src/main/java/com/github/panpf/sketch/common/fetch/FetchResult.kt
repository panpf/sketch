package com.github.panpf.sketch.common.fetch

import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.datasource.DataSource

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult(val source: DataSource) {

    val from: DataFrom = source.from

    override fun toString(): String {
        return "FetchResult(from=$from, source=$source)"
    }
}
