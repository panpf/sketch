package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

/**
 * Support 'content://sample.jpg' uri
 */
class ContentUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
) : Fetcher {

    override suspend fun fetch(): FetchResult =
        FetchResult(ContentDataSource(sketch.appContext, request.uri))

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): ContentUriFetcher? =
            if (request is LoadRequest && request.uri.scheme == "content") {
                ContentUriFetcher(sketch, request)
            } else {
                null
            }
    }
}