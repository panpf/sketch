package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.datasource.DataSource

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult(val source: DataSource) {

    val from: DataFrom = source.from

    override fun toString(): String {
        return "FetchResult(from=$from, source=$source)"
    }
}
