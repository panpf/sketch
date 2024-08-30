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
import com.github.panpf.sketch.cache.SkiaBitmapImageValue
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.readIntPixels
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toLogString

/**
 * Convert [SkiaBitmap] to [Image]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapImageTest.testAsSketchImage
 */
fun SkiaBitmap.asSketchImage(): SkiaBitmapImage = SkiaBitmapImage(this)

/**
 * SkiaBitmap Image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapImageTest
 */
data class SkiaBitmapImage(
    val bitmap: SkiaBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = (bitmap.rowBytes * bitmap.height).toLong()

    override val allocationByteCount: Long = byteCount

    override fun cacheValue(extras: Map<String, Any?>?): Value =
        SkiaBitmapImageValue(this, extras)

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer = SkiaBitmapImageTransformer()

    override fun getPixels(): IntArray? = bitmap.readIntPixels()

    override fun toString(): String =
        "SkiaBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

/**
 * SkiaBitmap Image Transformer
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapImageTest.testSkiaBitmapImageTransformer
 */
class SkiaBitmapImageTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<SkiaBitmapImage>().bitmap
        val outBitmap = inputBitmap.scaled(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<SkiaBitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}