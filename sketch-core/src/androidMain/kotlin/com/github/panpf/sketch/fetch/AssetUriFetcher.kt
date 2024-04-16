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
import androidx.core.net.toUri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.fetch.AssetUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap

/**
 * Sample: 'asset://test.png'
 */
fun newAssetUri(assetFilePath: String): String = "$SCHEME://$assetFilePath"

/**
 * Support 'asset://test.png' uri
 */
class AssetUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val assetFileName: String
) : Fetcher {

    companion object {
        const val SCHEME = "asset"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(assetFileName)
        FetchResult(AssetDataSource(sketch, request, assetFileName), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): AssetUriFetcher? {
            val uri = request.uriString.toUri()
            return if (SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val resourcePath = "${uri.authority.orEmpty()}${uri.path.orEmpty()}"
                AssetUriFetcher(sketch, request, resourcePath)
            } else {
                null
            }
        }

        override fun toString(): String = "AssetUriFetcher"

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