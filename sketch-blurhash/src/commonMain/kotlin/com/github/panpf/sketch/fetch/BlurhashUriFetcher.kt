@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri

/**
 * Adds compose blurhash
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.fetch.BlurhashUriFetcherTest.testSupportBlurhash
 */
fun ComponentRegistry.Builder.supportBlurhash(): ComponentRegistry.Builder = apply {
    addFetcher(BlurhashUriFetcher.Factory())
}

/**
 * Sample: 'blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2'
 *
 * @see com.github.panpf.sketch.core.android.test.fetch.BlurhashUriFetcherTest.testNewBlurhashUri
 */
fun newBlurhashUri(blurhashString: String): String {
    if (BlurhashUtil.isValid(blurhashString)) {
        return "${BlurhashUriFetcher.SCHEME}://${blurhashString}"
    }
    throw IllegalArgumentException("Not valid blurhash string: $blurhashString")
}

/**
 * Check if the uri is a blurhash image uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.BlurHashUriFetcherTest.testIsBlurHashUri
 */
fun isBlurHashUri(uri: Uri): Boolean {
    val data = uri.toString()
    if (BlurhashUriFetcher.SCHEME == uri.scheme && data.startsWith("${BlurhashUriFetcher.SCHEME}://")) {
        val blurhashString = data.substring("${BlurhashUriFetcher.SCHEME}://".length)
        return BlurhashUtil.isValid(blurhashString)
    }
    return false
}

class BlurhashUriFetcher constructor(
    val blurHashString: String, val size: Size
) : Fetcher {

    companion object {
        const val SCHEME = "blurhash"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        return Result.success(
            FetchResult(BlurhashDataSource(blurHashString, DataFrom.NETWORK), "")
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurhashUriFetcher
        if (blurHashString != other.blurHashString) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = blurHashString.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlurHashUriFetcher(blurHash='$blurHashString', size=$size)"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): BlurhashUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isBlurHashUri(uri) || requestContext.size == Size.Empty) return null
            val uriString = uri.toString()
            val blurhashString = uriString.substring("${BlurhashUriFetcher.SCHEME}://".length)
            return BlurhashUriFetcher(
                blurHashString = blurhashString, size = requestContext.size
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "BlurhashUriFetcher"
    }
}