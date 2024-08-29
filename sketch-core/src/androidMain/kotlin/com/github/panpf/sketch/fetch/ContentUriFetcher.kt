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

package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.util.Uri

/**
 * Check if the uri is a android content uri
 *
 * Support 'content://sample.jpg' uri
 */
fun isContentUri(uri: Uri): Boolean = ContentUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)

/**
 * Support 'content://sample.jpg' uri
 */
class ContentUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val contentUri: android.net.Uri,
) : Fetcher {

    companion object {
        const val SCHEME = "content"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = request.context.contentResolver.getType(contentUri)
        FetchResult(ContentDataSource(sketch.context, contentUri), mimeType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ContentUriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (contentUri != other.contentUri) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + contentUri.hashCode()
        return result
    }

    override fun toString(): String {
        return "ContentUriFetcher('$contentUri')"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): ContentUriFetcher? {
            val uri = request.uri
            if (!isContentUri(uri)) return null
            return ContentUriFetcher(sketch, request, android.net.Uri.parse(uri.toString()))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "ContentUriFetcher"
    }
}