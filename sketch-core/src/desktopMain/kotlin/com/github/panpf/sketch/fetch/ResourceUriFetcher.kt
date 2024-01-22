package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.ResourceUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ResourceLoader
import com.github.panpf.sketch.util.getCacheFileFromStreamDataSource
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.toUri
import okio.Path
import okio.Source
import okio.source
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Sample: 'jvm.resource://test.png'
 */
fun newResourceUri(resourceName: String): String = "$SCHEME://$resourceName"

class ResourceUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val resourceName: String,
) : Fetcher {

    companion object {
        const val SCHEME = "jvm.resource"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        return Result.success(FetchResult(ResourceDataSource(sketch, request, resourceName), null))
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ResourceUriFetcher? {
            val uri = request.uriString.toUri()
            return ifOrNull(SCHEME.equals(uri.scheme, ignoreCase = true)) {
                ResourceUriFetcher(sketch, request, uri.authority.orEmpty())
            }
        }

        override fun toString(): String = "ResourceUriFetcher"

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

class ResourceDataSource(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val resourceName: String,
) : DataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openSourceOrNull(): Source = ResourceLoader.Default.load(resourceName).source()

    @WorkerThread
    @Throws(IOException::class)
    override fun getFileOrNull(): Path = getCacheFileFromStreamDataSource(sketch, request, this)

    override fun toString(): String = "ResourceDataSource($resourceName)"
}