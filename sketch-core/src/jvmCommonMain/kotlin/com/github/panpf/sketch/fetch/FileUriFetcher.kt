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
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(filePath: String): String = "$SCHEME://$filePath"

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(file: File): String = "$SCHEME://${file.path}"

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val file: File,
) : Fetcher {

    companion object {
        const val SCHEME = "file"

        fun parseFilePathFromFileUri(uriString: String): String? {
            val startFlag = "$SCHEME://"
            val start = uriString.substring(0, startFlag.length)
            return if (startFlag.equals(start, ignoreCase = true)) {
                val subStartIndex = startFlag.length
                val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                    ?: uriString.indexOf("#").takeIf { it != -1 }
                    ?: uriString.length
                val filePath = uriString.substring(subStartIndex, subEndIndex)
                URLDecoder.decode(filePath, StandardCharsets.UTF_8.name())
            } else if (uriString.startsWith("/")) {
                uriString
            } else {
                null
            }
        }
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = MimeTypeMap.getMimeTypeFromExtension(file.extension)
        FetchResult(FileDataSource(sketch, request, file), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val filePath = parseFilePathFromFileUri(request.uriString) ?: return null
            return FileUriFetcher(sketch, request, File(filePath))
        }

        override fun toString(): String = "FileUriFetcher"

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