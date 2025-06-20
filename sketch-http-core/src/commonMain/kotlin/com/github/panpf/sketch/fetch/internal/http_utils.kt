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

package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpStack.Content
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.MimeTypeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import okio.BufferedSink
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Write all the content of the input stream to the output stream.
 *
 * @see com.github.panpf.sketch.http.core.common.test.fetch.internal.HttpUtilsTest.testCopyToWithProgress
 */
@Throws(IOException::class, CancellationException::class)
internal suspend fun copyToWithProgress(
    coroutineScope: CoroutineScope,
    logger: Logger,
    sink: BufferedSink,
    content: Content,
    request: ImageRequest,
    contentLength: Long,
    bufferSize: Int = 1024 * 8,
): Long {
    val progressListener = request.progressListener
    if (progressListener != null && contentLength <= 0) {
        logger.w { "Invalid contentLength $contentLength, progressListener will not be called. ${request.uri}" }
    }
    val progressListenerDelegate = if (progressListener != null && contentLength > 0) {
        ProgressListenerDelegate(coroutineScope, progressListener)
    } else {
        null
    }
    val buffer = ByteArray(bufferSize)
    var completedLength = 0L
    while (coroutineScope.isActive) {
        val readLength = content.read(buffer)
        if (readLength > 0) {
            sink.write(buffer, 0, readLength)
            completedLength += readLength
            progressListenerDelegate?.callbackProgress(
                request = request,
                contentLength = contentLength,
                completedLength = completedLength
            )
        } else {
            break
        }
    }
    if (!coroutineScope.isActive) {
        throw CancellationException("Canceled")
    }
    return completedLength
}

/**
 * Parse the response's `content-type` header.
 *
 * "text/plain" is often used as a default/fallback MIME type.
 * Attempt to guess a better MIME type from the file extension.
 *
 * @see com.github.panpf.sketch.http.core.common.test.fetch.internal.HttpUtilsTest.testGetMimeType
 */
internal fun getMimeType(url: String, contentType: String?): String? {
    if (contentType == null
        || contentType.trim().isEmpty()
        || contentType.startsWith(HttpUriFetcher.MIME_TYPE_TEXT_PLAIN)
    ) {
        MimeTypeMap.getMimeTypeFromUrl(url)?.let { return it }
    }
    return contentType?.substringBefore(';')
}