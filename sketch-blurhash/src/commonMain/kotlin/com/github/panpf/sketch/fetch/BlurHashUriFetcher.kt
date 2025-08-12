/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.UriCodec

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
 * Sample: 'blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2?width=100&height=100'
 *
 * @see com.github.panpf.sketch.core.android.test.fetch.BlurHashUriFetcherTest.testNewBlurHashUri
 */
fun newBlurHashUri(blurHash: String, width: Int? = null, height: Int? = null): String {
    return if (width != null && height != null && width > 0 && height > 0) {
        "${BlurHashUriFetcher.SCHEME}://${UriCodec.encode(blurHash)}?width=${width}&height=${height}"
    } else {
        "${BlurHashUriFetcher.SCHEME}://${UriCodec.encode(blurHash)}"
    }
}

/**
 * Create a blur hash uri
 *
 * Sample: 'blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2?width=100&height=100'
 *
 * @see com.github.panpf.sketch.core.android.test.fetch.BlurHashUriFetcherTest.testNewBlurHashUri
 */
fun newBlurHashUri(blurHash: String, size: Size? = null): String {
    return newBlurHashUri(blurHash = blurHash, size?.width, size?.height)
}

/**
 * Check if the uri is a blurHash image uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.BlurHashUriFetcherTest.testIsBlurHashUri
 */
fun isBlurHashUri(uri: Uri): Boolean = BlurHashUriFetcher.SCHEME == uri.scheme

/**
 * Check if the uri string is a blurHash image uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.BlurHashUriFetcherTest.testIsBlurHashUri
 */
fun isBlurHashUri(uri: String): Boolean = uri.startsWith("${BlurHashUriFetcher.SCHEME}://")

/**
 * Get the size from the blurHash uri
 */
fun getSizeFromBlurHashUri(uri: Uri): Size? {
    if (!isBlurHashUri(uri)) return null
    val queryParameters: Map<String, String> = uri.queryParameters
    val width = queryParameters["width"]?.toIntOrNull() ?: return null
    val height = queryParameters["height"]?.toIntOrNull() ?: return null
    return Size(width, height)
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