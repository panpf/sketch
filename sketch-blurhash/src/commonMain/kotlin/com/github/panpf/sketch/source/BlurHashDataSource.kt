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

import com.github.panpf.sketch.Sketch
import okio.IOException
import okio.Path
import okio.Source

/**
 * A [DataSource] that provides access to image data represented by a BlurHash string.
 *
 * @see com.github.panpf.sketch.blurhash.common.test.source.BlurHashDataSourceTest
 */
class BlurHashDataSource constructor(
    val blurHash: String,
    override val dataFrom: DataFrom,
) : DataSource {

    override val key: String = blurHash

    @Throws(IOException::class)
    override fun openSource(): Source = throw UnsupportedOperationException("Not supported")

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path =
        throw UnsupportedOperationException("Not supported")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurHashDataSource
        if (blurHash != other.blurHash) return false
        if (dataFrom != other.dataFrom) return false
        return true
    }

    override fun hashCode(): Int {
        var result = blurHash.hashCode()
        result = 31 * result + dataFrom.hashCode()
        return result
    }

    override fun toString(): String = "BlurHashDataSource(blurHash='$blurHash', dataFrom=$dataFrom)"
}