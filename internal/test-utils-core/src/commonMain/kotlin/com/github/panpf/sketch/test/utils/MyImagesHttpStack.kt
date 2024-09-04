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

package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import okio.BufferedSource
import okio.buffer
import okio.use

class MyImagesHttpStack(val sketch: Sketch) : HttpStack {

    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): HttpStack.Response {
        val fileName = url.toUri().authority
        val myImage = ResourceImages.values.find { it.resourceName == fileName } ?: throw IllegalArgumentException("Unknown image: $fileName")
        val request = ImageRequest(sketch.context, myImage.uri)
        val fetchResult = sketch.components.newFetcherOrThrow(
            request
                .toRequestContext(sketch, Size.Empty)
        ).fetch().getOrThrow()
        return MyImageResponse(url, fetchResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MyImagesHttpStack
        return sketch == other.sketch
    }

    override fun hashCode(): Int {
        return sketch.hashCode()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        return "MyImagesHttpStack(Sketch@${sketch.hashCode().toHexString()})"
    }

    class MyImageResponse(
        private val uri: String,
        private val fetchResult: FetchResult,
    ) : HttpStack.Response {

        override val code: Int
            get() = 200
        override val message: String?
            get() = null
        override val contentLength: Long
            get() = _contentLength
        override val contentType: String
            get() = MimeTypeMap.getMimeTypeFromUrl(uri) ?: "image/?"

        private val _contentLength: Long by lazy {
            fetchResult.dataSource.openSource().buffer().use {
                it.readByteArray().size.toLong()
            }
        }

        override fun getHeaderField(name: String): String? {
            return if (name.equals("Content-Length", ignoreCase = true)) {
                return contentLength.toString()
            } else if (name.equals("Content-Type", ignoreCase = true)) {
                return contentType
            } else {
                null
            }
        }

        override suspend fun content(): HttpStack.Content {
            return MyImageContent(fetchResult.dataSource.openSource().buffer())
        }
    }

    class MyImageContent(val source: BufferedSource) : HttpStack.Content {

        override suspend fun read(buffer: ByteArray): Int {
            return source.read(buffer)
        }

        override fun close() {
            source.close()
        }
    }
}