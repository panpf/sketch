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
 * @see com.github.panpf.sketch.http.core.common.test.http.HttpStackTest
 */
interface HttpStack {

    companion object {
        const val DEFAULT_TIMEOUT = 7 * 1000
    }

    /**
     * Send a request and return a response
     */
    @Throws(IOException::class, CancellationException::class)
    suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): Response

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