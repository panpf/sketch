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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri

/**
 * Sample: 'file:///android_asset/test.png'
 */
fun newAssetUri(fileName: String): String =
    "${AssetUriFetcher.SCHEME}:///${AssetUriFetcher.PATH_ROOT}/$fileName"

/**
 * Check if the uri is a android asset uri
 *
 * Support 'file:///android_asset/test.png' uri
 */
fun isAssetUri(uri: Uri): Boolean =
    AssetUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && AssetUriFetcher.PATH_ROOT.equals(uri.pathSegments.firstOrNull(), ignoreCase = true)

/**
 * Support 'file:///android_asset/test.png' uri
 */
class AssetUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val fileName: String
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val PATH_ROOT = "android_asset"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val mimeType = MimeTypeMap.getMimeTypeFromUrl(fileName)
        FetchResult(AssetDataSource(sketch, request, fileName), mimeType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AssetUriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (fileName != other.fileName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + fileName.hashCode()
        return result
    }

    override fun toString(): String {
        return "AssetUriFetcher('$fileName')"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): AssetUriFetcher? {
            val uri = request.uri
            if (!isAssetUri(uri)) return null
            val fileName = uri.pathSegments.drop(1).joinToString("/")
            return AssetUriFetcher(sketch = sketch, request = request, fileName = fileName)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "AssetUriFetcher"
    }
}