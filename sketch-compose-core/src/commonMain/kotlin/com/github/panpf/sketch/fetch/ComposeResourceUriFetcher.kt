package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.toUri
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

/**
 * Sample: 'compose.resource://test.png'
 */
fun newComposeResourceUri(resourceName: String): String =
    "${ComposeResourceUriFetcher.SCHEME}://$resourceName"

class ComposeResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourcePath: String,
) : Fetcher {

    companion object {
        const val SCHEME = "compose.resource"
    }

    @OptIn(InternalResourceApi::class)
    override suspend fun fetch(): Result<FetchResult> {
        val bytes = readResourceBytes(resourcePath)
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourcePath)
        val dataSource = ByteArrayDataSource(sketch, request, LOCAL, bytes)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ComposeResourceUriFetcher? {
            val uri = request.uriString.toUri()
            return if (SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val resourcePath = "${uri.authority.orEmpty()}${uri.path.orEmpty()}"
                ComposeResourceUriFetcher(sketch, request, resourcePath)
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "ComposeResourceUriFetcher"
    }
}