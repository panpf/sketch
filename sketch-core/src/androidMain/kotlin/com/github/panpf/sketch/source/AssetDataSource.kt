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

import android.content.Context
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.source.DataFrom.LOCAL
import okio.Path
import okio.Source
import okio.source
import java.io.IOException

/**
 * Provides access to image data in asset resources
 */
class AssetDataSource constructor(
    val context: Context,
    val fileName: String
) : DataSource {

    override val key: String by lazy { newAssetUri(fileName) }

    override val dataFrom: DataFrom = LOCAL

    @Throws(IOException::class)
    override fun openSource(): Source = context.assets.open(fileName).source()

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path = cacheFile(sketch)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AssetDataSource
        return fileName == other.fileName
    }

    override fun hashCode(): Int {
        return fileName.hashCode()
    }

    override fun toString(): String = "AssetDataSource('$fileName')"
}