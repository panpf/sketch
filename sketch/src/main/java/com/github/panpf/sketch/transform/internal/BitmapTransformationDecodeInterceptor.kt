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
package com.github.panpf.sketch.transform.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.internal.freeBitmap
import java.util.LinkedList

class BitmapTransformationDecodeInterceptor : BitmapDecodeInterceptor {

    @WorkerThread
    override suspend fun intercept(
        chain: BitmapDecodeInterceptor.Chain,
    ): BitmapDecodeResult {
        val request = chain.request
        val sketch = chain.sketch
        val result = chain.proceed()
        val transformations = request.transformations ?: return result

        val oldBitmap = result.bitmap
        val transformedList = LinkedList<String>()
        val newBitmap = transformations.fold(oldBitmap) { inputBitmap, next ->
            val transformResult = next.transform(sketch, request, inputBitmap)
            if (transformResult != null) {
                if (transformResult.bitmap !== inputBitmap) {
                    freeBitmap(sketch.bitmapPool, sketch.logger, inputBitmap, "transform:${next}")
                }
                transformedList.add(transformResult.transformed)
                transformResult.bitmap
            } else {
                inputBitmap
            }
        }
        return if (transformedList.isNotEmpty()) {
            require(!newBitmap.isRecycled)
            result.newResult(bitmap = newBitmap) {
                transformedList.forEach {
                    addTransformed(it)
                }
            }
        } else {
            result
        }
    }

    override fun toString(): String = "BitmapTransformationDecodeInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}