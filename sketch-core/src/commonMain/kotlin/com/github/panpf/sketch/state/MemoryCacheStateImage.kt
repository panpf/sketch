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

package com.github.panpf.sketch.state

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest

/**
 * Get a Bitmap from memory using the given memory cache key as a state Drawable, if not found, use defaultImage
 *
 * @see com.github.panpf.sketch.core.common.test.state.MemoryCacheStateImageTest
 */
data class MemoryCacheStateImage(
    val cacheKey: String?,
    val defaultImage: StateImage? = null
) : StateImage {

    override val key: String = "MemoryCache('$cacheKey',${defaultImage?.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        val memoryCache = sketch.memoryCache
        val cachedValue = cacheKey?.let { memoryCache[it] }
        return cachedValue?.image ?: defaultImage?.getImage(sketch, request, throwable)
    }

    override fun toString(): String =
        "MemoryCacheStateImage(cacheKey='$cacheKey', defaultImage=$defaultImage)"
}