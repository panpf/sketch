package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.toUri

/**
 * Sample: 'kotlin.resource://test.png'
 */
fun newKotlinResourceUri(resourceName: String): String =
    "${KotlinResourceUriFetcher.SCHEME}://$resourceName"

class KotlinResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourceName: String,
) : Fetcher {

    companion object {
        const val SCHEME = "kotlin.resource"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourceName)
        val dataSource = KotlinResourceDataSource(sketch, request, resourceName)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): KotlinResourceUriFetcher? {
            val uri = request.uriString.toUri()
            return ifOrNull(SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val resourcePath = "${uri.authority.orEmpty()}${uri.path.orEmpty()}"
                KotlinResourceUriFetcher(sketch, request, resourcePath)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "KotlinResourceUriFetcher"
    }
}