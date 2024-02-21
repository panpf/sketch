/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.fetch.Base64UriFetcher.Companion.BASE64_IDENTIFIER
import com.github.panpf.sketch.fetch.Base64UriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.util.ifOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
fun newBase64Uri(mimeType: String, imageDataBase64String: String): String =
    "$SCHEME:$mimeType;${BASE64_IDENTIFIER}$imageDataBase64String"

/**
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
class Base64UriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val mimeType: String,
    val imageDataBase64StringLazy: Lazy<String>,
) : Fetcher {

    companion object {
        const val SCHEME = "data"
        const val BASE64_IDENTIFIER = "base64,"
    }

    @OptIn(ExperimentalEncodingApi::class)
    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        val bytes = Base64.Default.decode(imageDataBase64StringLazy.value)
        return Result.success(
            FetchResult(ByteArrayDataSource(sketch, request, MEMORY, bytes), mimeType)
        )
    }

    /**
     * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
     */
    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): Base64UriFetcher? {
            val uriString = request.uriString
            val index = uriString.indexOf(":").takeIf { it != -1 } ?: return null
            return ifOrNull(SCHEME.equals(uriString.substring(0, index), ignoreCase = true)) {
                val mimeTypeEndSymbolIndex = uriString.indexOf(";")
                val base64IdentifierIndex = uriString.indexOf(BASE64_IDENTIFIER)
                if (mimeTypeEndSymbolIndex != -1 && base64IdentifierIndex != -1) {
                    val mimeType =
                        uriString.substring(SCHEME.length + 1, mimeTypeEndSymbolIndex)
                    Base64UriFetcher(sketch, request, mimeType, lazy {
                        uriString.substring(base64IdentifierIndex + BASE64_IDENTIFIER.length)
                    })
                } else {
                    throw UriInvalidException("Invalid base64 image: $uriString")
                }
            }
        }

        override fun toString(): String = "Base64UriFetcher"

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }
    }
}