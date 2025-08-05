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
 * Adds blurhash support
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.fetch.BlurhashUriFetcherTest.testSupportBlurhash
 */
fun ComponentRegistry.Builder.supportBlurhash(): ComponentRegistry.Builder = apply {
    addFetcher(BlurhashUriFetcher.Factory())
}

/**
 * Create a blurhash uri
 *
 * Sample: 'blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2&width=100&height=100'
 *
 * @see com.github.panpf.sketch.core.android.test.fetch.BlurhashUriFetcherTest.testNewBlurhashUri
 */
fun newBlurhashUri(blurhashString: String, width: Int? = null, height: Int? = null): String {
    if (BlurhashUtil.isValid(blurhashString)) {
        if (width != null && height != null) {
            require(width > 0 && height > 0) {
                "Width and height must be greater than zero"
            }
            return "${BlurhashUriFetcher.SCHEME}://${blurhashString}&width=${width}&height=${height}"
        } else {
            return "${BlurhashUriFetcher.SCHEME}://${blurhashString}"
        }
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
        val afterScheme = data.substring("${BlurhashUriFetcher.SCHEME}://".length)
        val blurhashString = if (afterScheme.contains('&')) {
            val endIndex = afterScheme.indexOf('&')
            afterScheme.substring(0, endIndex)
        } else {
            afterScheme
        }
        return BlurhashUtil.isValid(blurhashString)
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

class BlurhashUriFetcher constructor(
    blurhashString: String,
    fallbackSize: Size? = null,
) : Fetcher {

    val blurHashString: String
    val size: Size?

    init {
        if (blurhashString.contains('&')) {
            val endIndex = blurhashString.indexOf('&')
            blurHashString = blurhashString.substring(0, endIndex)
            val queryString = blurhashString.substring(endIndex + 1)
            val parsedSize = parseQueryParameters(queryString)
            size = parsedSize ?: fallbackSize
        } else {
            blurHashString = blurhashString
            size = fallbackSize
        }
    }

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
            val afterScheme = uriString.substring("${SCHEME}://".length)

            return BlurhashUriFetcher(
                blurhashString = afterScheme,
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

        override fun toString(): String = "BlurhashUriFetcher"
    }
}