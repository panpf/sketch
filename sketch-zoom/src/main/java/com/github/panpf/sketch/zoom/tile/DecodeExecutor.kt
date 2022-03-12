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
import androidx.annotation.MainThread
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.tile.TileDecoder.Factory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList

class DecodeExecutor constructor(
    val context: Context,
    val imageUri: String,
    val exifOrientation: Int
) {

    private var destroyed = false
    private val decoderPool = DecoderPool()
    private val bitmapPool = context.sketch.bitmapPool
    private val decoderFactory = Factory()
    private val decodeDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)

    @MainThread
    suspend fun decode(key: Int, tile: Tile): Bitmap? {
        requiredMainThread()
        if (destroyed || tile.isExpired(key)) return null

        return withContext(decodeDispatcher) {
            if (tile.isExpired(key)) {
                return@withContext null
            }

            val decoder = decoderPool.poll()
                ?: decoderFactory.create(context, imageUri, exifOrientation)
                ?: return@withContext null

            val bitmap = decoder.decode(tile.srcRect, tile.inSampleSize)
            decoderPool.put(decoder)
            if (tile.isExpired(key)) {
                bitmapPool.free(bitmap)
                return@withContext null
            }

            bitmap
        }
    }

    fun destroy() {
        destroyed = true
        decoderPool.destroy()
    }

    private class DecoderPool {

        private val pool = LinkedList<TileDecoder>()
        private var destroyed = false

        fun poll(): TileDecoder? =
            synchronized(this) {
                if (destroyed) {
                    null
                } else {
                    pool.poll()
                }
            }

        fun put(decoder: TileDecoder) {
            synchronized(this) {
                if (destroyed) {
                    decoder.recycle()
                } else {
                    pool.add(decoder)
                }
            }
        }

        fun destroy() {
            synchronized(this) {
                destroyed = true
                pool.forEach {
                    it.recycle()
                }
                pool.clear()
            }
        }
    }
}