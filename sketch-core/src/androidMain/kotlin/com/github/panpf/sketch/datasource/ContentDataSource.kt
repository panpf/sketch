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
package com.github.panpf.sketch.datasource

import android.net.Uri
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getCacheFileFromStreamDataSource
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Provides access to image data in content resources
 */
class ContentDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val contentUri: Uri
) : BasedFileDataSource {

    override val dataFrom: DataFrom = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openInputStream(): InputStream =
        request.context.contentResolver.openInputStream(contentUri)
            ?: throw IOException("Invalid content uri: $contentUri")

    @WorkerThread
    @Throws(IOException::class)
    override fun getFile(): File =
        if (contentUri.scheme.equals("file", ignoreCase = true)) {
            val filePath = FileUriFetcher.parseFilePathFromFileUri(contentUri.toString())!!
            File(filePath)
        } else {
            getCacheFileFromStreamDataSource(sketch, request, this)
        }

    override fun toString(): String = "ContentDataSource('$contentUri')"
}