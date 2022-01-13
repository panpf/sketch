package com.github.panpf.sketch.fetch

import android.net.Uri
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
    val contentUri: Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "content"
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(ContentDataSource(sketch, request, contentUri))

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): ContentUriFetcher? =
            if (request is LoadRequest && SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                ContentUriFetcher(sketch, request, request.uri)
            } else {
                null
            }
    }
}