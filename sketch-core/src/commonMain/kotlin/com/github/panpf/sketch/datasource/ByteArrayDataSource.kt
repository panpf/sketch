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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getDataSourceCacheFile
import okio.Buffer
import okio.IOException
import okio.Path
import okio.Source

/**
 * Provides access to byte array image data.
 */
class ByteArrayDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val dataFrom: DataFrom,
    val data: ByteArray,
) : DataSource {

    @WorkerThread
    @Throws(IOException::class)
    override fun openSourceOrNull(): Source = Buffer().write(data)

    @WorkerThread
    @Throws(IOException::class)
    override fun getFileOrNull(): Path = getDataSourceCacheFile(sketch, request, this)

    override fun toString(): String =
        "ByteArrayDataSource(from=$dataFrom,length=${data.size.toLong()})"
}