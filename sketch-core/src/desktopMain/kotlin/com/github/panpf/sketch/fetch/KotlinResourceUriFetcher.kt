package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.pathSegments
import com.github.panpf.sketch.util.toUri

/**
 * Sample: 'file:///kotlin_resource/test.png'
 */
fun newKotlinResourceUri(resourceName: String): String =
    "${KotlinResourceUriFetcher.SCHEME}:///${KotlinResourceUriFetcher.PATH_ROOT}/$resourceName"

/**
 * Check if the uri is a Kotlin resource uri
 */
fun isKotlinResourceUri(uri: Uri): Boolean =
    KotlinResourceUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && KotlinResourceUriFetcher.PATH_ROOT
        .equals(uri.pathSegments.firstOrNull(), ignoreCase = true)

class KotlinResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourcePath: String,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val PATH_ROOT = "kotlin_resource"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(resourcePath)
        val dataSource = KotlinResourceDataSource(sketch, request, resourcePath)
        return Result.success(FetchResult(dataSource, mimeType))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): KotlinResourceUriFetcher? {
            val uri = request.uri.toUri()
            return if (isKotlinResourceUri(uri)) {
                val resourcePath = uri.pathSegments.drop(1).joinToString("/")
                KotlinResourceUriFetcher(sketch, request, resourcePath)
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

        override fun toString(): String = "KotlinResourceUriFetcher"
    }
}