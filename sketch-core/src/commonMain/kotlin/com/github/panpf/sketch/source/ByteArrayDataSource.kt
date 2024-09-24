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

package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import okio.Buffer
import okio.ByteString.Companion.toByteString
import okio.IOException
import okio.Path
import okio.Source

/**
 * Provides access to byte array image data.
 *
 * @see com.github.panpf.sketch.core.common.test.source.ByteArrayDataSourceTest
 */
class ByteArrayDataSource constructor(
    val data: ByteArray,
    override val dataFrom: DataFrom,
) : DataSource {

    override val key: String by lazy { data.toByteString().md5().hex() }

    @Throws(IOException::class)
    override fun openSource(): Source = Buffer().write(data)

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path = cacheFile(sketch)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ByteArrayDataSource
        if (dataFrom != other.dataFrom) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + dataFrom.hashCode()
        return result
    }

    override fun toString(): String = "ByteArrayDataSource(data=${data}, from=$dataFrom)"
}