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
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.tile.TileDecoder.Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TileManager constructor(
    context: Context,
    viewSize: Size,
    private val decoder: TileDecoder,
) {
    var viewSize: Size = viewSize
        internal set(value) {
            if (field != value) {
                field = value
                // todo
//                reset()
            }
        }

    val tileMap = initializeTileMap(decoder.imageSize, viewSize)
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



//    private suspend fun decodeTile(tile: Tile): Bitmap? {
//        requiredMainThread()
//
//        val tileDecoder = tileDecoder
//        return if (tileDecoder != null) {
//            tileDecoder.decode(tile.key, tile)
//        } else {
//            if (decoderInitializing?.isActive != true) {
//                decoderInitializing = scope.launch {
//                    this@Tiles.tileDecoder = withContext(Dispatchers.IO) {
//                        Factory(context, imageUri, exifOrientation).create()
//                    }
//                }
//            }
//            null
//        }
//    }

    fun destroy(){
        cleanMemory()
        decoder.destroy()
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

    private fun findTileListByScale() {

    }
}