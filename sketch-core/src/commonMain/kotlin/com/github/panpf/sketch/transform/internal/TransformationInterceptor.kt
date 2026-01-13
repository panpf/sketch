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

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import kotlinx.coroutines.withContext

/**
 * Transformation request interceptor, used to transformation the image after decoding is completed
 *
 * @see com.github.panpf.sketch.core.common.test.transform.internal.TransformationInterceptorTest
 */
class TransformationInterceptor : Interceptor {

    companion object {
        const val SORT_WEIGHT = 75
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        val request = chain.request
        val requestContext = chain.requestContext
        val result = chain.proceed(request)
        val transformations = request.transformations ?: return result
        val imageData = result.let { it.getOrNull() ?: return it }

        val oldImage = imageData.image
        val transformeds = mutableListOf<String>()
        val transformResult = withContext(chain.sketch.decodeTaskDispatcher) {
            runCatching {
                transformations.fold(oldImage) { inputImage, next ->
                    val transformResult = next.transform(requestContext, inputImage)
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
            }
        }
        val newImage = transformResult.getOrNull()
        return when {
            newImage == null -> {
                Result.failure(transformResult.exceptionOrNull()!!)
            }

            transformeds.isNotEmpty() -> {
                val newImageData = imageData.newImageData(image = newImage) {
                    transformeds.forEach {
                        addTransformed(it)
                    }
                }
                Result.success(newImageData)
            }

            else -> {
                Result.success(imageData)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "TransformationInterceptor"
}