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

package com.github.panpf.sketch

import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.util.Size

/**
 * Size of the image
 *
 * @see com.github.panpf.sketch.core.common.test.ImageTest.testSize
 */
val Image.size: Size
    get() = Size(width, height)

/**
 * An image
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapImageTest
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaAnimatedImageTest
 * @see com.github.panpf.sketch.compose.core.common.test.PainterImageTest
 */
interface Image {

    /**
     * The width of the image in pixels.
     */
    val width: Int

    /**
     * The height of the image in pixels.
     */
    val height: Int

    /**
     * Returns the minimum number of bytes that can be used to store this bitmap's pixels.
     */
    val byteCount: Long

    /**
     * Returns the size of the allocated memory used to store this bitmap's pixels..
     */
    val allocationByteCount: Long

    /**
     * True if the image can be shared between multiple [Target]s at the same time.
     *
     * For example, a bitmap can be shared between multiple targets if it's immutable.
     * Conversely, an animated image cannot be shared as its internal state is being mutated while
     * its animation is running.
     */
    val shareable: Boolean

    /**
     * Get the cache value used to cache the current Image into the memory cache
     */
    fun cacheValue(extras: Map<String, Any?>? = null): Value?

    /**
     * Check if the image is valid
     */
    fun checkValid(): Boolean

    /**
     * Get the transformer used to resize the image
     */
    // TODO remove
    fun transformer(): ImageTransformer?
}

/**
 * Image transformer, used for later resize after decoding
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testAndroidBitmapImageTransformer
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapImageTest.testSkiaBitmapImageTransformer
 */
interface ImageTransformer {

    /**
     * Zoom image
     */
    fun scale(image: Image, scaleFactor: Float): Image

    /**
     * Create a new image based on the mapping relationship represented by [ResizeMapping]
     */
    fun mapping(image: Image, mapping: ResizeMapping): Image
}

/**
 * Provides byte count
 */
interface ByteCountProvider {
    /**
     * Returns the minimum number of bytes that can be used to store this object's data.
     */
    val byteCount: Long

    /**
     * Returns the size of the allocated memory used to store this object's data.
     */
    val allocationByteCount: Long
}