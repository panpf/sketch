package com.github.panpf.sketch.fetch

import android.content.Context
import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.internal.ImageRequest

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    private val context: Context,
    private val fileUri: Uri
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        return FetchResult(ContentDataSource(context, fileUri))
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val uri = request.uri
            return if (uri.scheme == "file") {
                FileUriFetcher(sketch.appContext, uri)
            } else if (uri.scheme == null && uri.path.orEmpty().startsWith("/")) {
                FileUriFetcher(sketch.appContext, uri.buildUpon().scheme("file").build())
            } else {
                null
            }
        }
    }
}