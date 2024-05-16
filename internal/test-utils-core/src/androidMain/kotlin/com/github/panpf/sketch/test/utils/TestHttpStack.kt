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
package com.github.panpf.sketch.test.utils

import android.content.Context
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.Parameters

class TestHttpStack constructor(
    private val context: Context,
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

    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        parameters: Parameters?
    ): HttpStack.Response {
        connectionDelayMillis?.let {
            Thread.sleep(it)
        }
        val testImage = testImages.plus(errorImage).plus(chunkedErrorImage).plus(lengthErrorImage)
            .find { it.uri == url }
        return if (testImage != null) {
            TestResponse(context, testImage, readDelayMillis)
        } else {
            ErrorResponse(404, "Not found resource")
        }
    }

    override fun toString(): String {
        return "TestHttpStack(readDelayMillis=$readDelayMillis)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestHttpStack) return false
        if (readDelayMillis != other.readDelayMillis) return false
        return true
    }

    override fun hashCode(): Int {
        return readDelayMillis?.hashCode() ?: 0
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
        private val context: Context,
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
            val assetFileName =
                testImage.uri.substring(testImage.uri.lastIndexOf("/") + 1)
            val inputStream = context.assets.open(assetFileName).run {
                if (readDelayMillis != null) {
                    SlowInputStream(this, readDelayMillis)
                } else {
                    this
                }
            }
            return HurlStack.Content(inputStream)
        }
    }

    class TestImage constructor(
        val uri: String,
        val contentLength: Long,
        val headerMap: Map<String, String>? = null
    )
}