package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri

/**
 * Sample: 'file:///kotlin_resource/test.png'
 */
fun newKotlinResourceUri(resourceName: String): String =
    "${KotlinResourceUriFetcher.SCHEME}:///${KotlinResourceUriFetcher.PATH_ROOT}/$resourceName"

/**
 * Check if the uri is a Kotlin resource uri
 *
 * Sample: 'file:///kotlin_resource/test.png'
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KotlinResourceUriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (resourcePath != other.resourcePath) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + resourcePath.hashCode()
        return result
    }

    override fun toString(): String {
        return "KotlinResourceUriFetcher('$resourcePath')"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): KotlinResourceUriFetcher? {
            val uri = request.uri
            if (!isKotlinResourceUri(uri)) return null
            val resourcePath = uri.pathSegments.drop(1).joinToString("/")
            return KotlinResourceUriFetcher(sketch, request, resourcePath)
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