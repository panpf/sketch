/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.zoomimage.sketch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.zoomimage.subsampling.BitmapFrom
import com.github.panpf.zoomimage.subsampling.ImageInfo
import com.github.panpf.zoomimage.subsampling.SkiaTileBitmap
import com.github.panpf.zoomimage.subsampling.TileBitmap
import com.github.panpf.zoomimage.subsampling.TileBitmapCache

actual class SketchTileBitmapCache actual constructor(
    private val sketch: Sketch,
) : TileBitmapCache {

    actual override fun get(key: String): TileBitmap? {
        val cacheValue = sketch.memoryCache[key] ?: return null
        cacheValue as SkiaBitmapImageValue
        val skiaBitmapImage = cacheValue.image
        val skiaBitmap = skiaBitmapImage.bitmap
        return SkiaTileBitmap(skiaBitmap, key, BitmapFrom.MEMORY_CACHE)
    }

    actual override fun put(
        key: String,
        tileBitmap: TileBitmap,
        imageUrl: String,
        imageInfo: ImageInfo,
    ): TileBitmap? {
        tileBitmap as SkiaTileBitmap
        val bitmap = tileBitmap.bitmap
        val cacheValue =
            SkiaBitmapImageValue(bitmap.asSketchImage(), extras = null)
        sketch.memoryCache.put(key, cacheValue)
        return null
    }
}