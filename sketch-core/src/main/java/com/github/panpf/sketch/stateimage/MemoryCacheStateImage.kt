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

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.util.SketchException

class MemoryCacheStateImage(
    private val memoryCacheKey: String?,
    private val defaultImage: StateImage?
) : StateImage {

    override fun getDrawable(
        context: Context, sketch: Sketch, request: DisplayRequest, throwable: SketchException?
    ): Drawable? {
        val memoryCache = sketch.memoryCache
        val cachedRefBitmap = memoryCacheKey?.let { memoryCache[it] }
        return if (cachedRefBitmap != null) {
            SketchBitmapDrawable(cachedRefBitmap, DataFrom.MEMORY_CACHE)
        } else {
            defaultImage?.getDrawable(context, sketch, request, throwable)
        }
    }
}