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
package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size

class TileManager constructor(context: Context, imageSize: Size, viewSize: Size, val decodeTile: suspend (tile: Tile) -> Bitmap?) {

    val tileMap = initializeTileMap(imageSize, viewSize)
    val bitmapPool = context.sketch.bitmapPool
    val memoryCache = context.sketch.memoryCache

    init {

    }

    fun onMatrixChanged(
        newVisibleRect: Rect,
        drawableSize: Size,
        viewSize: Size,
        imageSize: Size,
        zooming: Boolean
    ) {

    }

    fun onDraw() {

    }

    fun cleanMemory() {
        tileMap.values.forEach { tileList ->
            tileList.forEach { tile ->
                // todo memory cache
                bitmapPool.free(tile.bitmap)
                tile.bitmap = null
            }
        }
    }

//    private fun getCacheKey(){
//
//
//        val memoryCacheKey: String by lazy {
//            "${imageUri}_${exifOrientation}_${srcRect.toShortString()}_${inSampleSize}"
//        }
//    }
}