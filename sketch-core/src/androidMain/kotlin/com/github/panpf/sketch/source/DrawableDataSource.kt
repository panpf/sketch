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
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.DrawableFetcher
import okio.Path
import okio.Source
import java.io.IOException

/**
 * Provides access to local file image data
 */
class DrawableDataSource constructor(
    val context: Context,
    val drawableFetcher: DrawableFetcher,
    override val dataFrom: DataFrom,
) : DataSource {

    val drawable: Drawable by lazy { drawableFetcher.getDrawable(context) }
    override val key: String by lazy { drawableFetcher.key }

    @Throws(IOException::class)
    override fun openSource(): Source = throw UnsupportedOperationException("Not supported")

    @Throws(IOException::class)
    override fun getFile(sketch: Sketch): Path =
        throw UnsupportedOperationException("Not supported")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawableDataSource
        if (drawableFetcher != other.drawableFetcher) return false
        if (dataFrom != other.dataFrom) return false
        return true
    }

    override fun hashCode(): Int {
        var result = drawableFetcher.hashCode()
        result = 31 * result + dataFrom.hashCode()
        return result
    }

    override fun toString(): String =
        "DrawableDataSource(drawable=${drawableFetcher}, from=$dataFrom)"
}