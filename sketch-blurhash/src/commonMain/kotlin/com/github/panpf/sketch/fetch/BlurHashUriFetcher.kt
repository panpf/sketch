@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri

/**
 * Adds blur hash support
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.fetch.BlurHashUriFetcherTest.testSupportBlurHash
 */
fun ComponentRegistry.Builder.supportBlurHash(): ComponentRegistry.Builder = apply {
    addFetcher(BlurHashUriFetcher.Factory())
}

/**
 * Create a blur hash uri
 *
 * Sample: 'blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2&width=100&height=100'
 *
 * @see com.github.panpf.sketch.core.android.test.fetch.BlurHashUriFetcherTest.testNewBlurHashUri
 */
fun newBlurHashUri(blurHashString: String, width: Int? = null, height: Int? = null): String {
    if (BlurHashUtil.isValid(blurHashString)) {
        if (width != null && height != null) {
            require(width > 0 && height > 0) {
                "Width and height must be greater than zero"
            }
            return "${BlurHashUriFetcher.SCHEME}://${blurHashString}&width=${width}&height=${height}"
        } else {
            return "${BlurHashUriFetcher.SCHEME}://${blurHashString}"
        }
    }
    throw IllegalArgumentException("Not valid blurHash string: $blurHashString")
}

/**
 * Check if the uri is a blurHash image uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.BlurHashUriFetcherTest.testIsBlurHashUri
 */
fun isBlurHashUri(uri: Uri): Boolean {
    val data = uri.toString()
    if (BlurHashUriFetcher.SCHEME == uri.scheme && data.startsWith("${BlurHashUriFetcher.SCHEME}://")) {
        val afterScheme = data.substring("${BlurHashUriFetcher.SCHEME}://".length)
        val blurHashString = if (afterScheme.contains('&')) {
            val endIndex = afterScheme.indexOf('&')
            afterScheme.substring(0, endIndex)
        } else {
            afterScheme
        }
        return BlurHashUtil.isValid(blurHashString)
    }
    return false
}

fun parseQueryParameters(queryString: String): Size? {
    val params = mutableMapOf<String, String>()
    queryString.split('&').forEach { param ->
        val parts = param.split('=', limit = 2)
        if (parts.size == 2) {
            params[parts[0]] = parts[1]
        }
    }

    val width = params["width"]?.toIntOrNull()
    val height = params["height"]?.toIntOrNull()

    return if (width != null && height != null && width > 0 && height > 0) {
        Size(width, height)
    } else {
        null
    }
}

class BlurHashUriFetcher constructor(
    blurHashString: String,
    fallbackSize: Size? = null,
) : Fetcher {

    val blurHashString: String
    val size: Size?

    init {
        if (blurHashString.contains('&')) {
            val endIndex = blurHashString.indexOf('&')
            this@BlurHashUriFetcher.blurHashString = blurHashString.substring(0, endIndex)
            val queryString = blurHashString.substring(endIndex + 1)
            val parsedSize = parseQueryParameters(queryString)
            size = parsedSize ?: fallbackSize
        } else {
            this@BlurHashUriFetcher.blurHashString = blurHashString
            size = fallbackSize
        }
    }

    companion object Companion {
        const val SCHEME = "blurhash"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        return Result.success(
            FetchResult(
                BlurHashDataSource(
                    this@BlurHashUriFetcher.blurHashString,
                    DataFrom.NETWORK
                ), ""
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurHashUriFetcher
        if (this@BlurHashUriFetcher.blurHashString != other.blurHashString) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = this@BlurHashUriFetcher.blurHashString.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlurHashUriFetcher(blurHash='${this@BlurHashUriFetcher.blurHashString}', size=$size)"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): BlurHashUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isBlurHashUri(uri) || requestContext.size == Size.Empty) return null
            val uriString = uri.toString()
            val afterScheme = uriString.substring("${SCHEME}://".length)

            return BlurHashUriFetcher(
                blurHashString = afterScheme,
                fallbackSize = requestContext.size
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