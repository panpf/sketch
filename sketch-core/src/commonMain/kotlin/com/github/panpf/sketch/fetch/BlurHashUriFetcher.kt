@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri

/**
 * Check if the uri is a blurhash image uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.BlurHashUriFetcherTest.testIsBlurHashUri
 */
fun isBlurHashUri(uri: Uri): Boolean {
    val data = uri.toString()
    return BlurhashUtil.isValid(data)
}

class BlurHashUriFetcher constructor(
    val blurHashString: String,
    val size: Size
) : Fetcher {

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        return Result.success(
            FetchResult(BlurhashDataSource(blurHashString, DataFrom.NETWORK), "")
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurHashUriFetcher
        if (blurHashString != other.blurHashString) return false
        return true
    }

    override fun hashCode(): Int {
        return blurHashString.hashCode()
    }

    override fun toString(): String {
        return "BlurHashUriFetcher(blurHash='$blurHashString')"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): BlurHashUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isBlurHashUri(uri)) return null
            println("logblur BlurHashUriFetcher uri = $uri")
            val uriString = uri.toString()
            return BlurHashUriFetcher(
                blurHashString = uriString,
                size = requestContext.size
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "BlurHashUriFetcher"
    }
}