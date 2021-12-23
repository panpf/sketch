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
package com.github.panpf.sketch3.common.datasource

import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.cache.disk.DiskCache
import java.io.File
import java.io.IOException
import java.io.InputStream

class DiskCacheDataSource(
    val diskCacheEntry: DiskCache.Entry,
    override val from: DataFrom
) : DataSource {

    @get:Throws(IOException::class)
    override var length: Long = -1
        get() {
            if (field >= 0) {
                return field
            }
            field = diskCacheEntry.file.length()
            return field
        }
        private set
    var isFromProcessedCache = false // 标识是否来自已处理缓存，后续对已处理缓存的图片会有额外处理
        private set

    @Throws(IOException::class)
    override fun newInputStream(): InputStream {
        return diskCacheEntry.newInputStream()
    }

    override fun getFile(outDir: File?, outName: String?): File {
        return diskCacheEntry.file
    }

    fun setFromProcessedCache(fromProcessedCache: Boolean): DiskCacheDataSource {
        isFromProcessedCache = fromProcessedCache
        return this
    }

    override fun toString(): String {
        return "DiskCacheDataSource(from=$from, file=${diskCacheEntry.file.path})"
    }
}