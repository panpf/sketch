package com.github.panpf.sketch3.common.fetch

import android.net.Uri
import com.github.panpf.sketch3.Sketch3

class HttpUriFetcher(
    private val uri: Uri,
    private val sketch3: Sketch3
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        TODO("Not yet implemented")
    }

    class Factory : Fetcher.Factory {
        override fun create(data: Uri, sketch3: Sketch3): Fetcher? =
            if (isApplicable(data)) HttpUriFetcher(data, sketch3) else null

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}