/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.Size

val Image.size: Size
    get() = Size(width, height)

@Stable
interface Image {

    /** The width of the image in pixels. */
    val width: Int

    /** The height of the image in pixels. */
    val height: Int

    /** Returns the minimum number of bytes that can be used to store this bitmap's pixels. */
    val byteCount: Long

    /** Returns the size of the allocated memory used to store this bitmap's pixels.. */
    val allocationByteCount: Long

    /**
     * True if the image can be shared between multiple [Target]s at the same time.
     *
     * For example, a bitmap can be shared between multiple targets if it's immutable.
     * Conversely, an animated image cannot be shared as its internal state is being mutated while
     * its animation is running.
     */
    val shareable: Boolean

    fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value?

    fun checkValid(): Boolean

    fun transformer(): ImageTransformer?

    fun getPixels(): IntArray?
}

interface ImageTransformer {

    fun scale(image: Image, scaleFactor: Float): Image

    fun mapping(image: Image, mapping: ResizeMapping): Image
}

interface ByteCountProvider {
    val byteCount: Long
    val allocationByteCount: Long
}