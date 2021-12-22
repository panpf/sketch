package com.github.panpf.sketch3.common.fetch

import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.datasource.DataSource

/**
 * The result of [Fetcher.fetch]
 */
class FetchResult(
    val source: DataSource,
    val from: DataFrom,
)
