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
package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException

class MemoryCacheStateImage(
    private val memoryCacheKey: String?,
    private val defaultImage: StateImage?
) : StateImage {

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable? {
        val memoryCache = sketch.memoryCache
        val cachedCountBitmap = memoryCacheKey?.let { memoryCache[it] }
        return if (cachedCountBitmap != null) {
            SketchCountBitmapDrawable(
                request.context.resources, cachedCountBitmap, DataFrom.MEMORY_CACHE
            )
        } else {
            defaultImage?.getDrawable(sketch, request, exception)
        }
    }
}