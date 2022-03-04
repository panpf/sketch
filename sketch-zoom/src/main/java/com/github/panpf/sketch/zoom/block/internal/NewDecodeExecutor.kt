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
package com.github.panpf.sketch.zoom.block.internal

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.MainThread
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.block.Block
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList

class NewDecodeExecutor constructor(
    val context: Context,
    val imageUri: String,
    val exifOrientation: Int
) {

    private var destroyed = false
    private val decoderPool = DecoderPool()
    private val bitmapPool = context.sketch.bitmapPool
    private val decoderFactory = NewBlockDecoder.Factory()
    private val blockDecodeDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)

    @MainThread
    suspend fun decodeBlock(key: Int, block: Block): Bitmap? {
        requiredMainThread()
        if (destroyed || block.isExpired(key)) return null

        return withContext(blockDecodeDispatcher) {
            if (block.isExpired(key)) {
                return@withContext null
            }

            val decoder = decoderPool.poll()
                ?: decoderFactory.create(context, imageUri, exifOrientation)
                ?: return@withContext null

            val bitmap = decoder.decodeBlock(key, block)
            decoderPool.put(decoder)
            if (block.isExpired(key)) {
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

        private val pool = LinkedList<NewBlockDecoder>()
        private var destroyed = false

        fun poll(): NewBlockDecoder? =
            synchronized(this) {
                if (destroyed) {
                    null
                } else {
                    pool.poll()
                }
            }

        fun put(decoder: NewBlockDecoder) {
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