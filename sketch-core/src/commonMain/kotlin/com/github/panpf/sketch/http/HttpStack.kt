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
package com.github.panpf.sketch.http

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import okio.Closeable
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Responsible for sending HTTP requests and returning responses
 */
interface HttpStack {

    companion object {
        const val DEFAULT_TIMEOUT = 7 * 1000
    }

    @WorkerThread
    @Throws(IOException::class, CancellationException::class)
    suspend fun getResponse(request: ImageRequest, url: String): Response

    interface Response {
        val code: Int

        val message: String?

        val contentLength: Long

        val contentType: String?

        fun getHeaderField(name: String): String?

        @Throws(IOException::class, CancellationException::class)
        suspend fun content(): Content
    }

    interface Content : Closeable {
        suspend fun read(buffer: ByteArray): Int
    }
}