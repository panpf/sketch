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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.defaultFileSystem
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Sample: 'file:///sdcard/sample.jpg'
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.FileUriFetcherTest.testNewFileUri
 */
fun newFileUri(path: String): String = "${FileUriFetcher.SCHEME}://$path"

/**
 * Sample: 'file:///sdcard/sample.jpg'
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.FileUriFetcherTest.testNewFileUri
 */
fun newFileUri(path: Path): String = "${FileUriFetcher.SCHEME}://$path"

/**
 * Check if the uri is a file uri
 *
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.FileUriFetcherTest.testIsFileUri
 */
fun isFileUri(uri: Uri): Boolean =
    (uri.scheme == null || FileUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true))
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && uri.path?.takeIf { it.isNotEmpty() } != null

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.FileUriFetcherTest
 */
class FileUriFetcher constructor(
    val path: Path,
    val fileSystem: FileSystem = defaultFileSystem()
) : Fetcher {

    companion object {
        const val SCHEME = "file"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val extension = MimeTypeMap.getExtensionFromUrl(path.name)
        val mimeType = extension?.let { MimeTypeMap.getMimeTypeFromExtension(it) }
        FetchResult(
            dataSource = FileDataSource(
                path = path,
                fileSystem = fileSystem,
                dataFrom = LOCAL
            ),
            mimeType = mimeType
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FileUriFetcher
        if (path != other.path) return false
        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun toString(): String {
        return "FileUriFetcher('$path')"
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): FileUriFetcher? {
            val uri = requestContext.request.uri
            if (!isFileUri(uri)) return null
            return FileUriFetcher(
                path = uri.path.orEmpty().toPath(),
                fileSystem = requestContext.sketch.fileSystem
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "FileUriFetcher"
    }
}