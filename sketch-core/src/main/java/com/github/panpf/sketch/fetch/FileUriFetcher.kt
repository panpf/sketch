package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

fun newFileUri(filePath: String): Uri = FileUriFetcher.newUri(filePath)

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val fileUri: Uri
) : Fetcher {

    companion object {
        const val SCHEME = "file"

        @JvmStatic
        fun newUri(filePath: String): Uri = Uri.parse("$SCHEME://$filePath")
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(ContentDataSource(sketch, request, fileUri))

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val uri = request.uri
            return when {
                request is LoadRequest && SCHEME.equals(uri.scheme, ignoreCase = true) -> {
                    FileUriFetcher(sketch, request, uri)
                }
                request is LoadRequest && uri.scheme == null
                        && uri.path.orEmpty().startsWith("/") -> {
                    FileUriFetcher(sketch, request, newUri(request.uriString))
                }
                else -> {
                    null
                }
            }
        }
    }
}