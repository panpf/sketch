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
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.UriCodec

/**
 * Adds BlurHash support
 *
 * @see com.github.panpf.sketch.blurhash.common.test.fetch.BlurHashUriFetcherTest.testSupportBlurHash
 */
fun ComponentRegistry.Builder.supportBlurHash(): ComponentRegistry.Builder = apply {
    addFetcher(BlurHashUriFetcher.Factory())
}

/**
 * Create a BlurHash uri
 *
 * Sample: 'blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=100&height=100'
 *
 * @see com.github.panpf.sketch.blurhash.common.test.fetch.BlurHashUriFetcherTest.testNewBlurHashUri
 */
fun newBlurHashUri(blurHash: String, width: Int? = null, height: Int? = null): String {
    return if (width != null && height != null && width > 0 && height > 0) {
        "${BlurHashUriFetcher.SCHEME}://${UriCodec.encode(blurHash)}?width=${width}&height=${height}"
    } else {
        "${BlurHashUriFetcher.SCHEME}://${UriCodec.encode(blurHash)}"
    }
}

/**
 * Create a BlurHash uri
 *
 * Sample: 'blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=100&height=100'
 *
 * @see com.github.panpf.sketch.blurhash.common.test.fetch.BlurHashUriFetcherTest.testNewBlurHashUri
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
 *
 * @see com.github.panpf.sketch.blurhash.common.test.fetch.BlurHashUriFetcherTest.testReadSizeFromBlurHashUri
 */
fun readSizeFromBlurHashUri(uri: Uri): Size? {
    if (!isBlurHashUri(uri)) return null
    val queryParameters: Map<String, String> = uri.queryParameters
    val width = queryParameters["width"]?.toIntOrNull() ?: return null
    val height = queryParameters["height"]?.toIntOrNull() ?: return null
    return Size(width, height)
}

// TODO remove
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

/**
 * A [Fetcher] that fetches images from a BlurHash uri.
 *
 * @see com.github.panpf.sketch.blurhash.common.test.fetch.BlurHashUriFetcherTest
 */
class BlurHashUriFetcher constructor(val blurHashUri: Uri) : Fetcher {

    companion object Companion {
        const val SCHEME = "blurhash"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        val dataSource = BlurHashDataSource(blurHashUri)
        val fetchResult = FetchResult(dataSource, mimeType = "image/jpeg")
        return Result.success(fetchResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurHashUriFetcher
        if (blurHashUri != other.blurHashUri) return false
        return true
    }

    override fun hashCode(): Int {
        return blurHashUri.hashCode()
    }

    override fun toString(): String {
        return "BlurHashUriFetcher(blurHashUri='$blurHashUri')"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): BlurHashUriFetcher? {
            val uri: Uri = requestContext.request.uri
            if (!isBlurHashUri(uri)) return null
            return BlurHashUriFetcher(blurHashUri = uri)
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