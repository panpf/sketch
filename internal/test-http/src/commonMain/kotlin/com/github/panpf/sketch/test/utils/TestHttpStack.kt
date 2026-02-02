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

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.content
import com.github.panpf.sketch.images.slow
import com.github.panpf.sketch.request.Extras
import com.github.panpf.sketch.util.toUri

class TestHttpStack(
    private val context: PlatformContext,
    val readDelayMillis: Long? = null,
    val connectionDelayMillis: Long? = null,
) : HttpStack {

    companion object {
        val testImages = arrayOf(TestImage("http://assets.com/sample.jpeg", 540456))
        val errorImage = TestImage("http://assets.com.com/error.jpeg", 540456)
        val chunkedErrorImage = TestImage(
            "http://assets.com/sample.png",
            -1,
            mapOf("Transfer-Encoding" to "chunked")
        )
        val lengthErrorImage = TestImage(
            "http://assets.com/sample.bmp",
            2833654 - 1,
        )
    }

    override suspend fun <T> request(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?,
        block: suspend (HttpStack.Response) -> T
    ): T {
        connectionDelayMillis?.let {
            block(it)
        }
        val testImage = testImages.plus(errorImage).plus(chunkedErrorImage).plus(lengthErrorImage)
            .find { it.uri == url }
        val response = if (testImage != null) {
            TestResponse(context, testImage, readDelayMillis)
        } else {
            ErrorResponse(404, "Not found resource")
        }
        return block(response)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestHttpStack
        if (readDelayMillis != other.readDelayMillis) return false
        if (connectionDelayMillis != other.connectionDelayMillis) return false
        return true
    }

    override fun hashCode(): Int {
        var result = readDelayMillis?.hashCode() ?: 0
        result = 31 * result + (connectionDelayMillis?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TestHttpStack(readDelayMillis=$readDelayMillis, connectionDelayMillis=$connectionDelayMillis)"
    }

    class ErrorResponse(
        override val code: Int,
        override val message: String,
    ) : HttpStack.Response {
        override val contentLength: Long
            get() = 0
        override val contentType: String
            get() = ""

        override fun getHeaderField(name: String): String? {
            return null
        }

        override suspend fun content(): HttpStack.Content = throw Exception()
    }

    class TestResponse(
        private val context: PlatformContext,
        private val testImage: TestImage,
        private val readDelayMillis: Long? = null
    ) : HttpStack.Response {

        override val code: Int
            get() = 200
        override val message: String?
            get() = null
        override val contentLength: Long
            get() = testImage.contentLength
        override val contentType: String
            get() = "image/jpeg"

        override fun getHeaderField(name: String): String? = testImage.headerMap?.get(name)

        override suspend fun content(): HttpStack.Content {
            val imageUri = testImage.uri
            val filePath = imageUri.toUri().pathSegments.joinToString("/")
            val image = ComposeResImageFiles.values.find { it.name == filePath }
                ?: throw IllegalArgumentException(
                    "Not found resource image. filePath='$filePath'. uri='$imageUri'"
                )
            return image.toDataSource(context).openSource().let {
                if (readDelayMillis != null) {
                    it.slow(readDelayMillis)
                } else {
                    it
                }
            }.content()
        }
    }

    class TestImage(
        val uri: String,
        val contentLength: Long,
        val headerMap: Map<String, String>? = null
    )
}