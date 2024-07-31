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
package com.github.panpf.sketch.source

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.request.ImageRequest
import okio.Path
import okio.Source

/**
 * Provides access to local file image data
 */
class DrawableDataSource constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val dataFrom: DataFrom,
    val drawableFetcher: DrawableFetcher
) : DataSource {

    val drawable: Drawable by lazy {
        drawableFetcher.getDrawable(request.context)
    }

    override fun openSourceOrNull(): Source? = null

    override fun getFileOrNull(): Path? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawableDataSource
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (dataFrom != other.dataFrom) return false
        if (drawableFetcher != other.drawableFetcher) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + dataFrom.hashCode()
        result = 31 * result + drawableFetcher.hashCode()
        return result
    }

    override fun toString(): String = "DrawableDataSource(drawable=${drawable}, from=$dataFrom)"
}