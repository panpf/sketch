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
import com.github.panpf.sketch.decode.internal.logString
import java.util.LinkedList

class BitmapTransformationDecodeInterceptor : BitmapDecodeInterceptor {

    override val key: String? = null
    override val sortWeight: Int = 90

    @WorkerThread
    override suspend fun intercept(
        chain: BitmapDecodeInterceptor.Chain,
    ): Result<BitmapDecodeResult> {
        val request = chain.request
        val requestContext = chain.requestContext
        val sketch = chain.sketch
        val result = chain.proceed()
        val transformations = request.transformations ?: return result
        val decodeResult = result.let { it.getOrNull() ?: return it }

        val oldBitmap = decodeResult.bitmap
        val transformedList = LinkedList<String>()
        val newBitmap = try {
            transformations.fold(oldBitmap) { inputBitmap, next ->
                val transformResult = next.transform(sketch, requestContext, inputBitmap)
                if (transformResult != null) {
                    if (transformResult.bitmap !== inputBitmap) {
                        sketch.bitmapPool.freeBitmap(
                            bitmap = inputBitmap,
                            disallowReuseBitmap = request.disallowReuseBitmap,
                            caller = "transform:${next}"
                        )
                        sketch.logger.d("BitmapTransformationDecodeInterceptor") {
                            "transform. freeBitmap. bitmap=${inputBitmap.logString}. '${requestContext.key}'"
                        }
                    }
                    transformedList.add(transformResult.transformed)
                    transformResult.bitmap
                } else {
                    inputBitmap
                }
            }
        } catch (e: Throwable) {
            return Result.failure(e)
        }
        return if (transformedList.isNotEmpty()) {
            require(!newBitmap.isRecycled)
            val newDecodeResult = decodeResult.newResult(bitmap = newBitmap) {
                transformedList.forEach {
                    addTransformed(it)
                }
            }
            Result.success(newDecodeResult)
        } else {
            Result.success(decodeResult)
        }
    }

    override fun toString(): String =
        "BitmapTransformationDecodeInterceptor(sortWeight=$sortWeight)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}