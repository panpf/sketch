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
package com.github.panpf.sketch.state

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.shaper.ImageShaper

/**
 * 从内存缓存中获取图片作为状态图片，支持 [ShapeSize] 和 [ImageShaper]
 */
class MemoryCacheStateImage(
    val memoryCacheKey: String, val whenEmptyImage: StateImage?
) : StateImage {

    override fun getDrawable(
        context: Context,
        sketchView: SketchView,
        displayOptions: DisplayOptions
    ): Drawable? {
        val memoryCache = Sketch.with(context).configuration.memoryCache
        val cachedRefBitmap = memoryCache[memoryCacheKey]
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled) {
                memoryCache.remove(memoryCacheKey)
            } else {
                val bitmapDrawable = SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE)
                val shapeSize = displayOptions.shapeSize
                val imageShaper = displayOptions.shaper
                return if (shapeSize != null || imageShaper != null) {
                    SketchShapeBitmapDrawable(context, bitmapDrawable, shapeSize, imageShaper)
                } else {
                    bitmapDrawable
                }
            }
        }
        return whenEmptyImage?.getDrawable(context, sketchView, displayOptions)
    }
}