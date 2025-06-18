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

package com.github.panpf.sketch.http

import com.github.panpf.sketch.request.Extras
import okio.Closeable
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Responsible for sending HTTP requests and returning responses
 *
 * @see com.github.panpf.sketch.http.hurl.common.test.http.HurlStackTest
 * @see com.github.panpf.sketch.http.okhttp.common.test.http.OkHttpStackTest
 * @see com.github.panpf.sketch.http.ktor2.common.test.http.KtorStackTest
 * @see com.github.panpf.sketch.http.ktor3.common.test.http.KtorStackTest
 */
interface HttpStack {

    /**
     * Send a request and execute the block with the response
     */
    @Throws(IOException::class, CancellationException::class)
    suspend fun <T> request(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?,
        block: suspend (Response) -> T
    ): T

    /**
     * Send a request and return a response
     */
    @Throws(IOException::class, CancellationException::class)
    @Deprecated(
        message = "The Ktor version of getResponse() will read all the contents into memory before returning the response. Please use request instead.",
        replaceWith = ReplaceWith("request(url, httpHeaders, extras) { it }")
    )
    suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): Response =
        throw UnsupportedOperationException("getResponse is deprecated, use request instead")

    /**
     * Http Response
     */
    interface Response {
        /**
         * Http status code
         */
        val code: Int

        /**
         * Http status message
         */
        val message: String?

        /**
         * Http response content length
         */
        val contentLength: Long

        /**
         * Http response content type
         */
        val contentType: String?

        /**
         * Get the value of the header field
         */
        fun getHeaderField(name: String): String?

        /**
         * Get response content
         */
        @Throws(IOException::class, CancellationException::class)
        suspend fun content(): Content
    }

    /**
     * Http response content, which can be read by the client
     */
    interface Content : Closeable {

        /**
         * Read the content of the response to the buffer
         *
         * @return The number of bytes read, or -1 if the end of the stream has been reached
         */
        suspend fun read(buffer: ByteArray): Int
    }
}