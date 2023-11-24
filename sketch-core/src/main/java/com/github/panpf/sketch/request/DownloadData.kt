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
package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DataFrom
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Data of [DownloadRequest]
 */
data class DownloadData(val data: Data, val dataFrom: DataFrom) : ImageData {

    constructor(bytes: ByteArray, dataFrom: DataFrom)
            : this(ByteArrayData(bytes), dataFrom)

    constructor(snapshot: DiskCache.Snapshot, dataFrom: DataFrom)
            : this(DiskCacheData(snapshot), dataFrom)

    sealed interface Data {
        @Throws(IOException::class)
        fun newInputStream(): InputStream
    }

    data class ByteArrayData(@Suppress("ArrayInDataClass") val bytes: ByteArray) : Data {

        override fun newInputStream(): InputStream = ByteArrayInputStream(bytes)
    }

    data class DiskCacheData(val snapshot: DiskCache.Snapshot) : Data {

        override fun newInputStream(): InputStream = snapshot.newInputStream()
    }
}