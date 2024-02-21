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

import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.ImageRequest

/**
 * Support 'content://sample.jpg' uri
 */
class ContentUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val contentUri: Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "content"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = request.context.contentResolver.getType(contentUri)
        FetchResult(ContentDataSource(sketch, request, contentUri), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ContentUriFetcher? {
            val uri = request.uriString.toUri()
            return if (SCHEME.equals(uri.scheme, ignoreCase = true)) {
                ContentUriFetcher(sketch, request, uri)
            } else {
                null
            }
        }

        override fun toString(): String = "ContentUriFetcher"

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