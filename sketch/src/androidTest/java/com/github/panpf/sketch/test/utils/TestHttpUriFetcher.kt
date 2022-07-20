package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest

class TestHttpUriFetcher(sketch: Sketch, request: ImageRequest, url: String) :
    HttpUriFetcher(sketch, request, url) {
    override suspend fun fetch(): FetchResult {
        return FetchResult(AssetDataSource(sketch, request, "fake_asset_name"), null)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): HttpUriFetcher? =
            if (
                SCHEME.equals(request.uri.scheme, ignoreCase = true)
                || SCHEME1.equals(request.uri.scheme, ignoreCase = true)
            ) {
                TestHttpUriFetcher(sketch, request, request.uriString)
            } else {
                null
            }
    }
}