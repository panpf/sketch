/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.request.ImageRequest
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream

class DiskCacheDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val dataFrom: DataFrom,
    val diskCacheSnapshot: DiskCache.Snapshot,
) : DataSource {

    private var _length = -1L

    @Throws(IOException::class)
    override fun length(): Long =
        _length.takeIf { it != -1L }
            ?: diskCacheSnapshot.file.length().apply {
                this@DiskCacheDataSource._length = this
            }

    @Throws(IOException::class)
    override fun newInputStream(): InputStream = diskCacheSnapshot.newInputStream()

    override fun newFileDescriptor(): FileDescriptor? = null

    override suspend fun file(): File = diskCacheSnapshot.file

    override fun toString(): String =
        "DiskCacheDataSource(from=$dataFrom,file='${diskCacheSnapshot.file.path}')"
}