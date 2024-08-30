/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult

/**
 * Transformation decoding interceptor, used to transformation the image after decoding is completed
 *
 * @see com.github.panpf.sketch.core.common.test.transform.internal.TransformationDecodeInterceptorTest
 */
class TransformationDecodeInterceptor : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 90

    @WorkerThread
    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        val request = chain.request
        val requestContext = chain.requestContext
        val sketch = chain.sketch
        val result = chain.proceed()
        val transformations = request.transformations ?: return result
        val decodeResult = result.let { it.getOrNull() ?: return it }

        val oldImage = decodeResult.image
        val transformeds = mutableListOf<String>()
        val newImage = try {
            transformations.fold(oldImage) { inputImage, next ->
                val transformResult = next.transform(sketch, requestContext, inputImage)
                if (transformResult != null) {
                    check(transformResult.image.checkValid()) {
                        val transformedsString =
                            transformeds.joinToString(prefix = "[", postfix = "]")
                        "Invalid image after transform. transformeds=$transformedsString"
                    }
                    transformeds.add(transformResult.transformed)
                    transformResult.image
                } else {
                    inputImage
                }
            }
        } catch (e: Throwable) {
            return Result.failure(e)
        }
        return if (transformeds.isNotEmpty()) {
            val newDecodeResult = decodeResult.newResult(image = newImage) {
                transformeds.forEach {
                    addTransformed(it)
                }
            }
            Result.success(newDecodeResult)
        } else {
            Result.success(decodeResult)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String =
        "TransformationDecodeInterceptor(sortWeight=$sortWeight)"
}