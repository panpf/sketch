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
package com.github.panpf.sketch.stateimage

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.asSketchImage
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.ImageRequest

/**
 * Get a Bitmap from memory using the given memory cache key as a state Drawable, if not found, use defaultImage
 */
class MemoryCacheStateImage(
    private val memoryCacheKey: String?,
    private val defaultImage: StateImage? = null
) : StateImage {

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        val memoryCache = sketch.memoryCache
        val cachedValue = memoryCacheKey?.let { memoryCache[it] }
        return cachedValue?.asSketchImage(request.context.resources)
            ?: defaultImage?.getImage(sketch, request, throwable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MemoryCacheStateImage
        if (memoryCacheKey != other.memoryCacheKey) return false
        if (defaultImage != other.defaultImage) return false
        return true
    }

    override fun hashCode(): Int {
        var result = memoryCacheKey?.hashCode() ?: 0
        result = 31 * result + (defaultImage?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MemoryCacheStateImage(memoryCacheKey=$memoryCacheKey, defaultImage=$defaultImage)"
    }
}