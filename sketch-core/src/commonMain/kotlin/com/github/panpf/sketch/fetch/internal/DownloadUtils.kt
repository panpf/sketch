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
package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import com.github.panpf.sketch.util.MimeTypeMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Throws(IOException::class, CancellationException::class)
internal fun CoroutineScope.copyToWithActive(
    request: ImageRequest,
    inputStream: InputStream,
    outputStream: OutputStream,
    contentLength: Long,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
): Long {
    var bytesCopied = 0L
    val buffer = ByteArray(bufferSize)
    var bytes = inputStream.read(buffer)
    var lastNotifyTime = 0L
    val progressListenerDelegate = request.progressListener?.let {
        ProgressListenerDelegate(this@copyToWithActive, it)
    }
    var lastUpdateProgressBytesCopied = 0L
    while (bytes >= 0 && isActive) {
        outputStream.write(buffer, 0, bytes)
        bytesCopied += bytes
        if (progressListenerDelegate != null && contentLength > 0) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastNotifyTime) > 300) {
                lastNotifyTime = currentTime
                val currentBytesCopied = bytesCopied
                lastUpdateProgressBytesCopied = currentBytesCopied
                progressListenerDelegate.onUpdateProgress(
                    request, contentLength, currentBytesCopied
                )
            }
        }
        bytes = inputStream.read(buffer)
    }
    if (!isActive) {
        throw CancellationException()
    }
    if (progressListenerDelegate != null
        && contentLength > 0
        && lastUpdateProgressBytesCopied != bytesCopied
    ) {
        progressListenerDelegate.onUpdateProgress(request, contentLength, bytesCopied)
    }
    return bytesCopied
}

/**
 * Parse the response's `content-type` header.
 *
 * "text/plain" is often used as a default/fallback MIME type.
 * Attempt to guess a better MIME type from the file extension.
 */
internal fun getMimeType(url: String, contentType: String?): String? {
    if (contentType == null || contentType.startsWith(HttpUriFetcher.MIME_TYPE_TEXT_PLAIN)) {
        MimeTypeMap.getMimeTypeFromUrl(url)?.let { return it }
    }
    return contentType?.substringBefore(';')
}