package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.ImageRequest

/**
 * Support 'content://sample.jpg' uri
 */
class ContentUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val contentUri: Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "content"
    }

    override suspend fun fetch(): FetchResult {
        val mimeType = request.context.contentResolver.getType(contentUri)
        return FetchResult(ContentDataSource(sketch, request, contentUri), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ContentUriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                ContentUriFetcher(sketch, request, request.uri)
            } else {
                null
            }

        override fun toString(): String = "ContentUriFetcher"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}