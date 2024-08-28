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

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.util.Uri
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
fun newBase64Uri(mimeType: String, imageDataBase64String: String): String =
    "${Base64UriFetcher.SCHEME}:$mimeType;${Base64UriFetcher.BASE64_IDENTIFIER},$imageDataBase64String"

/**
 * 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
@OptIn(ExperimentalEncodingApi::class)
fun newBase64Uri(mimeType: String, imageData: ByteArray): String {
    val imageDataBase64String = Base64.Default.encode(imageData)
    return newBase64Uri(mimeType, imageDataBase64String)
}

/**
 * Check if the uri is a base64 uri
 *
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
fun isBase64Uri(uri: Uri): Boolean {
    val data = uri.toString()
    return (data.startsWith("${Base64UriFetcher.SCHEME}:image/") || data.startsWith("${Base64UriFetcher.SCHEME}:img/"))
            && data.indexOf(";${Base64UriFetcher.BASE64_IDENTIFIER},") != -1
}

enum class Base64Specification {
    Default, Mime, UrlSafe
}

const val BASE64_SPECIFICATION_KEY = "sketch#base64_specification"

fun ImageOptions.Builder.base64Specification(
    specification: Base64Specification?
): ImageOptions.Builder = apply {
    if (specification != null) {
        setExtra(
            key = BASE64_SPECIFICATION_KEY,
            value = specification.name,
            cacheKey = null
        )
    } else {
        removeExtra(BASE64_SPECIFICATION_KEY)
    }
}

fun ImageRequest.Builder.base64Specification(
    specification: Base64Specification?
): ImageRequest.Builder = apply {
    if (specification != null) {
        setExtra(
            key = BASE64_SPECIFICATION_KEY,
            value = specification.name,
            cacheKey = null
        )
    } else {
        removeExtra(BASE64_SPECIFICATION_KEY)
    }
}

val ImageOptions.base64Specification: Base64Specification?
    get() = extras?.value<String>(BASE64_SPECIFICATION_KEY)
        ?.let { Base64Specification.valueOf(it) }

val ImageRequest.base64Specification: Base64Specification?
    get() = extras?.value<String>(BASE64_SPECIFICATION_KEY)
        ?.let { Base64Specification.valueOf(it) }

/**
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri uri
 */
class Base64UriFetcher constructor(
    val sketch: Sketch,
    val request: ImageRequest,
    val mimeType: String,
    val imageDataBase64String: String,
) : Fetcher {

    companion object {
        const val SCHEME = "data"
        const val BASE64_IDENTIFIER = "base64"
    }

    @OptIn(ExperimentalEncodingApi::class)
    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        val base64Specification = request.base64Specification
        val base64 = when (base64Specification) {
            Base64Specification.Mime -> Base64.Mime
            Base64Specification.UrlSafe -> Base64.UrlSafe
            else -> Base64.Default
        }
        val bytes = base64.decode(imageDataBase64String)
        return Result.success(
            FetchResult(ByteArrayDataSource(sketch, request, MEMORY, bytes), mimeType)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Base64UriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (mimeType != other.mimeType) return false
        if (imageDataBase64String != other.imageDataBase64String) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + imageDataBase64String.hashCode()
        return result
    }

    override fun toString(): String {
        return "Base64UriFetcher('$imageDataBase64String')"
    }

    /**
     * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
     */
    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): Base64UriFetcher? {
            val uri = request.uri
            if (!isBase64Uri(uri)) return null
            val data = uri.toString()
            val colonSymbolIndex = data.indexOf(":").takeIf { it != -1 }
                ?: throw UriInvalidException("Invalid base64 image uri: $uri")
            val semicolonSymbolIndex = data.indexOf(";").takeIf { it != -1 }
                ?: throw UriInvalidException("Invalid base64 image uri: $uri")
            val commaSymbolIndex = data.indexOf(",").takeIf { it != -1 }
                ?: throw UriInvalidException("Invalid base64 image uri: $uri")
            val mimeType =
                data.substring(colonSymbolIndex + 1, semicolonSymbolIndex).replace("img/", "image/")
            val imageDataBase64String = data.substring(commaSymbolIndex + 1)
            return Base64UriFetcher(
                sketch = sketch,
                request = request,
                mimeType = mimeType,
                imageDataBase64String = imageDataBase64String
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "Base64UriFetcher"
    }
}