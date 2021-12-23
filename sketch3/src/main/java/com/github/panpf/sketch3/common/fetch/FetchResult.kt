package com.github.panpf.sketch3.common.fetch

import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.datasource.DataSource

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult constructor(
    val from: DataFrom,
    val source: DataSource,
) {
    override fun toString(): String {
        return "FetchResult(from=$from, source=$source)"
    }
}
