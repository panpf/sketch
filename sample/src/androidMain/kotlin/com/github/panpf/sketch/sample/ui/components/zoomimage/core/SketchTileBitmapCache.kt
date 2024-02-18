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
import com.github.panpf.sketch.cache.BitmapImageValue
import com.github.panpf.zoomimage.sketch.SketchTileBitmap
import com.github.panpf.zoomimage.subsampling.AndroidTileBitmap
import com.github.panpf.zoomimage.subsampling.CacheTileBitmap
import com.github.panpf.zoomimage.subsampling.TileBitmap
import com.github.panpf.zoomimage.subsampling.TileBitmapCache

class SketchTileBitmapCache constructor(
    private val sketch: Sketch,
    private val caller: String
) : TileBitmapCache {

    override fun get(key: String): CacheTileBitmap? {
        val cache = sketch.memoryCache[key] ?: return null
        return SketchTileBitmap(key = key, cacheValue = cache, caller = caller)
    }

    override fun put(
        key: String,
        tileBitmap: TileBitmap,
        imageUrl: String,
        imageInfo: com.github.panpf.zoomimage.subsampling.ImageInfo,
        disallowReuseBitmap: Boolean
    ): CacheTileBitmap? {
        val bitmap = (tileBitmap as AndroidTileBitmap).bitmap ?: return null
        val newCacheValue = BitmapImageValue(bitmap.asSketchImage())
        if (!sketch.memoryCache.put(key, newCacheValue)) {
            return null
        }
        return SketchTileBitmap(key, newCacheValue, caller)
    }
}