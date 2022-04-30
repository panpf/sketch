package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.ImageRequest

/**
 * Support 'content://sample.jpg' uri
 */
class ContentUriFetcher(
    val request: ImageRequest,
    val contentUri: Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "content"
    }

    override suspend fun fetch(): FetchResult {
        val mimeType = request.context.contentResolver.getType(contentUri)
        return FetchResult(ContentDataSource(request, contentUri), mimeType)
    }

    class Factory : Fetcher.Factory {
        override fun create(request: ImageRequest): ContentUriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                ContentUriFetcher(request, request.uri)
            } else {
                null
            }

        override fun toString(): String = "ContentUriFetcher"
    }
}