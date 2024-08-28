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

package com.github.panpf.sketch.source

import android.net.Uri
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.util.toUri
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Source
import okio.source
import java.io.File
import java.io.IOException

/**
 * Provides access to image data in content resources
 */
class ContentDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val contentUri: Uri
) : DataSource {

    override val dataFrom: DataFrom = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openSourceOrNull(): Source =
        (request.context.contentResolver.openInputStream(contentUri)
            ?: throw IOException("Invalid content uri: $contentUri")).source()

    @WorkerThread
    @Throws(IOException::class)
    override fun getFileOrNull(): Path? {
        val sketchUri = contentUri.toString().toUri()
        return if (isFileUri(sketchUri)) {
            val filePath = sketchUri.path!!
            File(filePath).toOkioPath()
        } else {
            getDataSourceCacheFile(sketch, request, this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ContentDataSource
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

    override fun toString(): String = "ContentDataSource('$contentUri')"
}